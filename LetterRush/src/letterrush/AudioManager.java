/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package letterrush;

import java.io.File;
import javax.sound.sampled.*;

public class AudioManager {
    private static AudioManager instance;
    private SourceDataLine audioLine;
    private FloatControl volumeControl;
    private Thread audioThread;
    private boolean isPlaying = false;

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void playAudio(File audioFile) {
        if (isPlaying) return; // Don't play again if already playing

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Audio line not supported");
                return;
            }

            audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(format);
            volumeControl = (FloatControl) audioLine.getControl(FloatControl.Type.MASTER_GAIN);
            audioLine.start();
            isPlaying = true;

            audioThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = audioStream.read(buffer)) != -1) {
                        audioLine.write(buffer, 0, bytesRead);
                    }
                    stopAudio();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            audioThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAudio() {
        isPlaying = false;
        if (audioLine != null) {
            audioLine.drain();
            audioLine.stop();
            audioLine.close();
        }
    }

    public FloatControl getVolumeControl() {
        return volumeControl;
    }
}
