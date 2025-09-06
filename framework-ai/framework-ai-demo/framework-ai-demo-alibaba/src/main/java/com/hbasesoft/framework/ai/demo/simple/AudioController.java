package com.hbasesoft.framework.ai.demo.simple;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioTranscriptionApi;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.audio.transcription.AudioTranscriptionModel;
import com.hbasesoft.framework.ai.demo.util.AudioUtil;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.io.IOUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * <Description> 声音 <br>
 *
 * @author 王伟
 * @version 1.0
 * @name AudioController
 * @date 2023/9/6 16:03
 */
@RestController
@RequestMapping("/simple/audio")
public class AudioController {

    /** 音频采样率 */
    private static final int SAMPLE_RATE = 16000;

    /** 音频转录模型 */
    private final AudioTranscriptionModel transcriptionModel;

    /** 模型 */
    private static final String DEFAULT_MODEL_2 = "paraformer-realtime-v2";

    /** 调度器 */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * 构造函数
     *
     * @param transcriptionModel 音频转录模型
     */
    public AudioController(final AudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    /**
     * 语音转文字接口
     *
     * @return 文字流
     */
    @RequestMapping("/stt")
    public Flux<String> stt() {

        // 创建一个 Sinks.Many（支持多播）
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        // 构建 Flux
        Flux<String> flux = sink.asFlux();

        AudioUtil.recorder(data -> {
            CountDownLatch latch = new CountDownLatch(1);
            try {
                File tempFile = IOUtil.createTempFile();
                IOUtil.writeFile(data, tempFile);

                // 创建音频转录任务
                AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(new FileSystemResource(tempFile),
                    DashScopeAudioTranscriptionOptions.builder().withSampleRate(SAMPLE_RATE)
                        .withFormat(DashScopeAudioTranscriptionOptions.AudioFormat.PCM)
                        .withDisfluencyRemovalEnabled(false).build());

                // 提交任务
                AudioTranscriptionResponse submitResponse = transcriptionModel.asyncCall(prompt);

                // 获取任务结果
                DashScopeAudioTranscriptionApi.Response.Output submitOutput = Objects
                    .requireNonNull(submitResponse.getMetadata().get("output"));
                String taskId = submitOutput.taskId();

                scheduler.scheduleAtFixedRate(() -> {
                }, 0, 1, java.util.concurrent.TimeUnit.SECONDS);

                latch.await();
            }
            catch (Exception e) {
                Thread.currentThread().interrupt();
                LoggerUtil.error(e);
            }
            finally {
                scheduler.shutdown();
            }
        });
        return flux;
    }

    /**
     * 检查任务状态
     *
     * @param taskId 任务ID
     * @param sink 数据流
     * @param latch 闭锁
     */
    private void checkTaskStatus(final String taskId, final Sinks.Many<String> sink, final CountDownLatch latch) {
        try {
            AudioTranscriptionResponse fetchResponse = transcriptionModel.fetch(taskId);
            DashScopeAudioTranscriptionApi.Response.Output fetchOutput = Objects
                .requireNonNull(fetchResponse.getMetadata().get("output"));
            DashScopeAudioTranscriptionApi.TaskStatus taskStatus = fetchOutput.taskStatus();

            if (taskStatus.equals(DashScopeAudioTranscriptionApi.TaskStatus.SUCCEEDED)) {
                sink.tryEmitNext(fetchResponse.getResult().getOutput());
                sink.tryEmitComplete(); // 完成流
                latch.countDown();
            }
            else if (taskStatus.equals(DashScopeAudioTranscriptionApi.TaskStatus.RUNNING)) {
                System.out.println(fetchResponse.getResult().getOutput());
            }
            else if (taskStatus.equals(DashScopeAudioTranscriptionApi.TaskStatus.FAILED)) {
                LoggerUtil.warn("Transcription failed.");
                latch.countDown();
            }
        }
        catch (Exception e) {
            latch.countDown();
            throw new ServiceException(e);
        }
    }

}
