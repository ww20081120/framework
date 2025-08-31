package com.hbasesoft.framework.ai.demo.util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.io.IOUtils;

import com.hbasesoft.framework.common.utils.date.DateUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> 声音工具<br>
 *
 * @author 王伟
 * @version 1.0
 * @date 2021/11/13 21:09
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AudioUtil {

    // ================== 音频格式配置常量 ==================

    /** 编码格式：PCM有符号 */
    private static final AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;

    /** 采样率：44.1kHz */
    private static final float SAMPLE_RATE = 44100.0F;

    /** 采样位数：16 bits */
    private static final int SAMPLE_SIZE_IN_BITS = 16;

    /** 声道数：1 - 单声道 */
    private static final int CHANNELS = 1;

    /** 帧大小：2 bytes (16 bits / 8) */
    private static final int FRAME_SIZE = 2;

    /** 帧速率：与采样率一致 */
    private static final float FRAME_RATE = SAMPLE_RATE;

    /** 字节序：false 表示大端 */
    private static final boolean BIG_ENDIAN = false;

    /** 音频格式对象 */
    private static final AudioFormat AUDIO_FORMAT = new AudioFormat(
        ENCODING, 
        SAMPLE_RATE, 
        SAMPLE_SIZE_IN_BITS,
        CHANNELS, 
        FRAME_SIZE, 
        FRAME_RATE, 
        BIG_ENDIAN
    );

    // ================== GUI 相关常量 ==================

    /** 主窗口默认宽度 */
    private static final int WINDOW_WIDTH = 400;

    /** 主窗口默认高度 */
    private static final int WINDOW_HEIGHT = 100;

    /** 默认录音文件名前缀 */
    private static final String DEFAULT_AUDIO_FILENAME_PREFIX = "audio_";

    /** 默认录音文件后缀 */
    private static final String DEFAULT_AUDIO_FILE_EXTENSION = ".wav";

    // ================== 缓冲区相关常量 ==================

    /** 音频缓冲区大小 */
    private static final int BUFFER_SIZE = 4096;

    // ================== 方法实现 ==================

    /**
     * Description: 启动默认录音界面。 显示一个简单的录音控制窗口，用户可点击按钮开始或停止录音。<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    public static void recorder() {
        RecorderGUI gui = new RecorderGUI();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        gui.setResizable(false);
        gui.setTitle("录音小工具");
        gui.setLocationRelativeTo(null);
        gui.setVisible(true);
    }

    /**
     * Description: 启动录音界面并设置回调。 当用户点击"停止录音"后，会通过指定的 Callback 返回录音数据 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param callback 录音完成后的回调函数<br>
     */
    public static void recorder(final Callback callback) {
        RecorderGUI gui = new RecorderGUI();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        gui.setResizable(false);
        gui.setTitle("录音小工具");
        gui.setLocationRelativeTo(null);
        gui.setVisible(true);
        gui.callback = callback;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param audioData
     * @param format    <br>
     */
    public static void playFromMemory(final byte[] audioData, final AudioFormat format) {
        SourceDataLine line = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream ais = new AudioInputStream(bais, format, audioData.length / format.getFrameSize());

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = ais.read(buffer)) != -1) {
                line.write(buffer, 0, bytesRead);
            }

            line.drain();
        }
        catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
        finally {
            if (line != null) {
                line.stop();
                line.close();
            }
        }
    }

    /**
     * <Description> 回调函数，用于在录音完成后返回录音数据。<br>
     * 
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2025年5月19日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.ai.demo.util <br>
     */
    @FunctionalInterface
    public interface Callback {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param data <br>
         */
        void onRecord(byte[] data);
    }

    /**
     * <Description> 录音GUI界面，提供开始/停止录音按钮和状态提示。<br>
     * 
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2025年5月19日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.ai.demo.util <br>
     */
    public static class RecorderGUI extends JFrame {

        /**
         * serialVersionUID <br>
         */
        private static final long serialVersionUID = -1712731921815229606L;

        /** 开始录音按钮 */
        private JButton recordBtn = new JButton("开始录音");

        /** 状态提示标签 */
        private JLabel statusLabel = new JLabel("就绪");

        /** 录音器对象 */
        private Recorder recorder;

        /** 回调函数 */
        private Callback callback;

        /**
         * 构造录音界面，初始化布局与事件绑定。
         */
        public RecorderGUI() {
            setLayout(new BorderLayout());
            JPanel controlPanel = new JPanel(new FlowLayout());
            controlPanel.add(recordBtn);
            add(controlPanel, BorderLayout.NORTH);
            add(statusLabel, BorderLayout.SOUTH);

            recordBtn.addActionListener(e -> {
                if ("开始录音".equals(recordBtn.getText())) {
                    if (recorder != null) {
                        recorder.stop();
                    }
                    recorder = new Recorder();
                    Thread recorderThread = new Thread(recorder);
                    recorderThread.setDaemon(true);
                    recorderThread.start();
                    recordBtn.setText("停止录音");
                    statusLabel.setText("录音中...");
                } 
                else {
                    if (recorder != null) {
                        recorder.stop();
                        if (callback != null) {
                            callback.onRecord(recorder.getOutStream().toByteArray());
                        } 
                        else {
                            saveToWAV(recorder.getOutStream().toByteArray());
                        }
                    }
                    recordBtn.setText("开始录音");
                    statusLabel.setText("录音结束");
                }
            });
        }

        /**
         * Description: WAV 文件保存<br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param data 录音数据字节数组 <br>
         */
        private void saveToWAV(final byte[] data) {
            JFileChooser fileChooser = new JFileChooser();

            String defaultName = DEFAULT_AUDIO_FILENAME_PREFIX 
                + DateUtil.getCurrentTimestamp()
                + DEFAULT_AUDIO_FILE_EXTENSION;
            fileChooser.setSelectedFile(new File(defaultName));

            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                        AudioInputStream ais = new AudioInputStream(bais, AUDIO_FORMAT, data.length / FRAME_SIZE)) {
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, fileChooser.getSelectedFile());
                    statusLabel.setText("保存成功: " + fileChooser.getSelectedFile().getName());
                }
                catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "文件保存失败");
                }
            }
        }
    }

    /**
     * <Description> 录音线程实现<br>
     * 
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2025年5月19日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.ai.demo.util <br>
     */
    public static class Recorder implements Runnable {

        /** 录音数据 */
        private ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        /** targetDataLine */
        private TargetDataLine targetDataLine;

        /** 是否录音 */
        private volatile boolean recording = true;

        @Override
        public void run() {
            try {
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT);
                targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
                targetDataLine.open(AUDIO_FORMAT);
                targetDataLine.start();

                byte[] buffer = new byte[BUFFER_SIZE];
                while (recording) {
                    int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                }

                // 停止并关闭设备
                targetDataLine.stop();
                targetDataLine.close();

            }
            catch (LineUnavailableException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "麦克风不可用");
            }
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @return <br>
         */
        public ByteArrayOutputStream getOutStream() {
            return outStream;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         *         <br>
         */
        public void stop() {
            recording = false;
            if (targetDataLine != null && targetDataLine.isOpen()) {
                targetDataLine.stop();
                targetDataLine.close();
            }
            IOUtils.closeQuietly(outStream);
        }
    }
}
