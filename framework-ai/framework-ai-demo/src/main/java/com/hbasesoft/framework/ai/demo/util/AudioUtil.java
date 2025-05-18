package com.hbasesoft.framework.ai.demo.util;

import com.hbasesoft.framework.common.utils.date.DateUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * <Description> 声音工具<br>
 *
 * @author 王伟
 * @version 1.0
 * @date 2021/11/13 21:09
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AudioUtil {

    /** 音频格式 */
    private static final AudioFormat AUDIO_FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, // 编码格式
        44100.0F,   // 采样率
        16,         // 采样位数
        1,          // 单声道
        2,          // 帧大小
        44100.0F,   // 帧速率
        false       // 小端字节序
    );

    public static void recorder() {
        RecorderGUI gui = new RecorderGUI();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(400, 100);
        gui.setResizable(false);
        gui.setTitle("录音小工具");
        gui.setLocationRelativeTo(null);
        gui.setVisible(true);
    }

    public static void recorder(Callback callback) {
        RecorderGUI gui = new RecorderGUI();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(400, 100);
        gui.setResizable(false);
        gui.setTitle("录音小工具");
        gui.setLocationRelativeTo(null);
        gui.setVisible(true);
        gui.callback = callback;
    }

    @FunctionalInterface
    public static interface Callback {
        void onRecord(byte[] data);
    }

    /**
     * 录音GUI
     */
    public static class RecorderGUI extends JFrame {

        private JButton recordBtn = new JButton("开始录音");

        private JLabel statusLabel = new JLabel("就绪");

        private Recorder recorder;

        private Callback callback;

        public RecorderGUI() {
            setLayout(new BorderLayout());
            JPanel controlPanel = new JPanel(new FlowLayout());
            controlPanel.add(recordBtn);
            add(controlPanel, BorderLayout.NORTH);
            add(statusLabel, BorderLayout.SOUTH);

            // 按钮事件绑定
            recordBtn.addActionListener(e -> {
                if (recordBtn.getText().equals("开始录音")) {
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

        // WAV文件保存（核心代码来自网页1）
        private void saveToWAV(byte[] data) {
            JFileChooser fileChooser = new JFileChooser();
            // 1. 生成带时间戳和后缀的默认文件名
            String defaultName = "audio_" + DateUtil.getCurrentTimestamp() + ".wav";
            fileChooser.setSelectedFile(new File(defaultName));

            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                    AudioInputStream ais = new AudioInputStream(bais, AUDIO_FORMAT,
                        data.length / AUDIO_FORMAT.getFrameSize())) {

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
     * 录音线程实现
     */
    public static class Recorder implements Runnable {
        /** 录音数据 */
        private ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        /** 是否录音 */
        private volatile boolean recording = true;

        /**
         * 录音
         */
        @Override
        public void run() {
            try {
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT);
                TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
                targetDataLine.open(AUDIO_FORMAT);
                targetDataLine.start();

                byte[] buffer = new byte[4096];
                while (recording) { // 通过标志位控制录音
                    int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {  // 确保有效数据写入[6](@ref)
                        outStream.write(buffer, 0, bytesRead);  // 实时写入内存流[5,8](@ref)
                    }
                }

            }
            catch (LineUnavailableException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "麦克风不可用");
            }
        }

        public ByteArrayOutputStream getOutStream() {
            return outStream;
        }

        public void stop() {
            recording = false;
        }
    }
}
