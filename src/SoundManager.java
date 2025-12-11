import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private Map<String, Clip> soundCache;
    private boolean soundEnabled = true;

    private SoundManager() {
        soundCache = new HashMap<>();
        generateSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void generateSounds() {
        try {
            // Generate dice roll sound
            generateDiceRollSound();
            // Generate move sound
            generateMoveSound();
            // Generate ladder sound
            generateLadderSound();
            // Generate win sound
            generateWinSound();
        } catch (Exception e) {
            System.err.println("Error generating sounds: " + e.getMessage());
        }
    }

    private void generateDiceRollSound() throws Exception {
        // Create a short rattling sound for dice
        float sampleRate = 44100;
        int duration = 300; // milliseconds
        int numSamples = (int)(sampleRate * duration / 1000);
        byte[] buffer = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double angle = i / (sampleRate / 440) * 2.0 * Math.PI;
            double noise = Math.random() * 2 - 1;
            short val = (short)(Math.sin(angle) * 3000 * Math.exp(-i * 3.0 / numSamples) + noise * 1000 * Math.exp(-i * 5.0 / numSamples));
            buffer[i * 2] = (byte)(val & 0xff);
            buffer[i * 2 + 1] = (byte)((val >> 8) & 0xff);
        }

        createClip("dice_roll", buffer, sampleRate);
    }

    private void generateMoveSound() throws Exception {
        // Create a soft pop sound
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

    private void generateLadderSound() throws Exception {
        // Create an ascending tone for ladder
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
        // Create a victory fanfare
        float sampleRate = 44100;
        int duration = 800;
        int numSamples = (int)(sampleRate * duration / 1000);
        byte[] buffer = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double t = i / sampleRate;
            double val = Math.sin(2 * Math.PI * 523.25 * t) * Math.exp(-t * 2); // C
            val += Math.sin(2 * Math.PI * 659.25 * t) * Math.exp(-(t - 0.15) * 2); // E
            val += Math.sin(2 * Math.PI * 783.99 * t) * Math.exp(-(t - 0.3) * 2); // G
            short sample = (short)(val * 3000);
            buffer[i * 2] = (byte)(sample & 0xff);
            buffer[i * 2 + 1] = (byte)((sample >> 8) & 0xff);
        }

        createClip("win", buffer, sampleRate);
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

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }
}