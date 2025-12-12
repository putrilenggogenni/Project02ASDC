import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private Map<String, Clip> soundCache;
    private Clip backgroundMusic;
    private boolean soundEnabled = true;
    private boolean musicEnabled = true;
    private float musicVolume = 0.3f; // 30% volume for background music

    private SoundManager() {
        soundCache = new HashMap<>();
        generateSounds();
        generateBackgroundMusic();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void generateSounds() {
        try {
            generateDiceRollSound();
            generateMoveSound();
            generateStepSound();
            generateLadderSound();
            generateWinSound();
            generateBonusSound();
        } catch (Exception e) {
            System.err.println("Error generating sounds: " + e.getMessage());
        }
    }

    private void generateDiceRollSound() throws Exception {
        float sampleRate = 44100;
        int duration = 300;
        int numSamples = (int)(sampleRate * duration / 1000);
        byte[] buffer = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double angle = i / (sampleRate / 440) * 2.0 * Math.PI;
            double noise = Math.random() * 2 - 1;
            short val = (short)(Math.sin(angle) * 3000 * Math.exp(-i * 3.0 / numSamples) +
                    noise * 1000 * Math.exp(-i * 5.0 / numSamples));
            buffer[i * 2] = (byte)(val & 0xff);
            buffer[i * 2 + 1] = (byte)((val >> 8) & 0xff);
        }

        createClip("dice_roll", buffer, sampleRate);
    }

    private void generateMoveSound() throws Exception {
        float sampleRate = 44100;
        int duration = 150;
        int numSamples = (int)(sampleRate * duration / 1000);
        byte[] buffer = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double angle = i / (sampleRate / 600) * 2.0 * Math.PI;
            short val = (short)(Math.sin(angle) * 5000 * Math.exp(-i * 8.0 / numSamples));
            buffer[i * 2] = (byte)(val & 0xff);
            buffer[i * 2 + 1] = (byte)((val >> 8) & 0xff);
        }

        createClip("move", buffer, sampleRate);
    }

    private void generateStepSound() throws Exception {
        // Soft tick sound for step-by-step movement
        float sampleRate = 44100;
        int duration = 80;
        int numSamples = (int)(sampleRate * duration / 1000);
        byte[] buffer = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double angle = i / (sampleRate / 800) * 2.0 * Math.PI;
            short val = (short)(Math.sin(angle) * 2000 * Math.exp(-i * 15.0 / numSamples));
            buffer[i * 2] = (byte)(val & 0xff);
            buffer[i * 2 + 1] = (byte)((val >> 8) & 0xff);
        }

        createClip("step", buffer, sampleRate);
    }

    private void generateLadderSound() throws Exception {
        float sampleRate = 44100;
        int duration = 500;
        int numSamples = (int)(sampleRate * duration / 1000);
        byte[] buffer = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double freq = 400 + (i * 400.0 / numSamples);
            double angle = i / (sampleRate / freq) * 2.0 * Math.PI;
            short val = (short)(Math.sin(angle) * 4000 * Math.exp(-i * 2.0 / numSamples));
            buffer[i * 2] = (byte)(val & 0xff);
            buffer[i * 2 + 1] = (byte)((val >> 8) & 0xff);
        }

        createClip("ladder", buffer, sampleRate);
    }

    private void generateWinSound() throws Exception {
        float sampleRate = 44100;
        int duration = 800;
        int numSamples = (int)(sampleRate * duration / 1000);
        byte[] buffer = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double t = i / sampleRate;
            double val = Math.sin(2 * Math.PI * 523.25 * t) * Math.exp(-t * 2);
            val += Math.sin(2 * Math.PI * 659.25 * t) * Math.exp(-(t - 0.15) * 2);
            val += Math.sin(2 * Math.PI * 783.99 * t) * Math.exp(-(t - 0.3) * 2);
            short sample = (short)(val * 3000);
            buffer[i * 2] = (byte)(sample & 0xff);
            buffer[i * 2 + 1] = (byte)((sample >> 8) & 0xff);
        }

        createClip("win", buffer, sampleRate);
    }

    private void generateBonusSound() throws Exception {
        // Sparkle sound for bonuses
        float sampleRate = 44100;
        int duration = 400;
        int numSamples = (int)(sampleRate * duration / 1000);
        byte[] buffer = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double t = i / sampleRate;
            double val = Math.sin(2 * Math.PI * 1000 * t) * Math.exp(-t * 5);
            val += Math.sin(2 * Math.PI * 1500 * t) * Math.exp(-(t - 0.1) * 5);
            short sample = (short)(val * 4000);
            buffer[i * 2] = (byte)(sample & 0xff);
            buffer[i * 2 + 1] = (byte)((sample >> 8) & 0xff);
        }

        createClip("bonus", buffer, sampleRate);
    }

    private void generateBackgroundMusic() {
        try {
            float sampleRate = 44100;
            int duration = 10000; // 10 seconds loop
            int numSamples = (int)(sampleRate * duration / 1000);
            byte[] buffer = new byte[numSamples * 2];

            // Simple melody with chords
            double[] melody = {261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25};

            for (int i = 0; i < numSamples; i++) {
                double t = i / sampleRate;
                int noteIndex = (int)((t * 2) % melody.length);
                double freq = melody[noteIndex];

                double val = Math.sin(2 * Math.PI * freq * t) * 0.3;
                val += Math.sin(2 * Math.PI * freq * 1.5 * t) * 0.2; // Harmony

                short sample = (short)(val * 3000);
                buffer[i * 2] = (byte)(sample & 0xff);
                buffer[i * 2 + 1] = (byte)((sample >> 8) & 0xff);
            }

            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            AudioInputStream ais = new AudioInputStream(bais, format, buffer.length / 2);

            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(ais);

            // Set volume
            setMusicVolume(musicVolume);

        } catch (Exception e) {
            System.err.println("Error generating background music: " + e.getMessage());
        }
    }

    private void createClip(String name, byte[] buffer, float sampleRate) throws Exception {
        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        AudioInputStream ais = new AudioInputStream(bais, format, buffer.length / 2);

        Clip clip = AudioSystem.getClip();
        clip.open(ais);
        soundCache.put(name, clip);
    }

    public void playSound(String soundName) {
        if (!soundEnabled) return;

        try {
            Clip clip = soundCache.get(soundName);
            if (clip != null) {
                clip.setFramePosition(0);
                clip.start();
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    public void startBackgroundMusic() {
        if (!musicEnabled || backgroundMusic == null) return;

        try {
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Error starting background music: " + e.getMessage());
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    private void setMusicVolume(float volume) {
        if (backgroundMusic != null) {
            try {
                FloatControl volumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float)(Math.log(volume) / Math.log(10.0) * 20.0);
                volumeControl.setValue(dB);
            } catch (Exception e) {
                // Volume control not available
            }
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
        } else {
            startBackgroundMusic();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }
}