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
    private Clip setupMusic;
    private Clip buttonClickClip;
    private Clip buttonHoverClip;

    private SoundManager() {
        soundCache = new HashMap<>();
        generateSounds();
        generateButtonSounds(); // Generate button sounds
        // Background music will be loaded/generated when startBackgroundMusic() is called
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

    /**
     * Generate button click and hover sounds
     */
    private void generateButtonSounds() {
        try {
            // Try to load custom button sounds first
            tryLoadButtonClickSound();
            tryLoadButtonHoverSound();

            // If custom sounds don't exist, generate default sounds
            if (buttonClickClip == null) {
                generateButtonClickSound();
            }
            if (buttonHoverClip == null) {
                generateButtonHoverSound();
            }
        } catch (Exception e) {
            System.err.println("Error generating button sounds: " + e.getMessage());
        }
    }

    /**
     * Try to load custom button click sound from file
     */
    private void tryLoadButtonClickSound() {
        try {
            File soundFile = new File("sounds/button_click.wav");
            if (soundFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                buttonClickClip = AudioSystem.getClip();
                buttonClickClip.open(audioStream);
                System.out.println("Loaded custom button click sound");
            }
        } catch (Exception e) {
            // Will use generated sound instead
        }
    }

    /**
     * Try to load custom button hover sound from file
     */
    private void tryLoadButtonHoverSound() {
        try {
            File soundFile = new File("sounds/button_hover.wav");
            if (soundFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                buttonHoverClip = AudioSystem.getClip();
                buttonHoverClip.open(audioStream);
                System.out.println("Loaded custom button hover sound");
            }
        } catch (Exception e) {
            // Will use generated sound instead
        }
    }

    /**
     * Generate a pleasant button click sound (short, crisp)
     */
    private void generateButtonClickSound() throws Exception {
        float sampleRate = 44100;
        int duration = 100; // Short click
        int numSamples = (int)(sampleRate * duration / 1000);
        byte[] buffer = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double t = i / sampleRate;
            // Two-tone click for a more satisfying sound
            double val = Math.sin(2 * Math.PI * 800 * t) * Math.exp(-t * 25);
            val += Math.sin(2 * Math.PI * 1200 * t) * Math.exp(-t * 30) * 0.5;
            short sample = (short)(val * 6000);
            buffer[i * 2] = (byte)(sample & 0xff);
            buffer[i * 2 + 1] = (byte)((sample >> 8) & 0xff);
        }

        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        AudioInputStream ais = new AudioInputStream(bais, format, buffer.length / 2);

        buttonClickClip = AudioSystem.getClip();
        buttonClickClip.open(ais);
    }

    /**
     * Generate a subtle button hover sound
     */
    private void generateButtonHoverSound() throws Exception {
        float sampleRate = 44100;
        int duration = 60; // Very short
        int numSamples = (int)(sampleRate * duration / 1000);
        byte[] buffer = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double t = i / sampleRate;
            // Soft, high-pitched tick
            double val = Math.sin(2 * Math.PI * 1500 * t) * Math.exp(-t * 40);
            short sample = (short)(val * 2000); // Quieter than click
            buffer[i * 2] = (byte)(sample & 0xff);
            buffer[i * 2 + 1] = (byte)((sample >> 8) & 0xff);
        }

        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        AudioInputStream ais = new AudioInputStream(bais, format, buffer.length / 2);

        buttonHoverClip = AudioSystem.getClip();
        buttonHoverClip.open(ais);
    }

    /**
     * Play button click sound
     */
    public void playButtonClick() {
        if (!soundEnabled || buttonClickClip == null) return;

        try {
            if (buttonClickClip.isRunning()) {
                buttonClickClip.stop();
            }
            buttonClickClip.setFramePosition(0);
            buttonClickClip.start();
        } catch (Exception e) {
            System.err.println("Error playing button click: " + e.getMessage());
        }
    }

    /**
     * Play button hover sound
     */
    public void playButtonHover() {
        if (!soundEnabled || buttonHoverClip == null) return;

        try {
            if (buttonHoverClip.isRunning()) {
                buttonHoverClip.stop();
            }
            buttonHoverClip.setFramePosition(0);
            buttonHoverClip.start();
        } catch (Exception e) {
            System.err.println("Error playing button hover: " + e.getMessage());
        }
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

    public void playSetupMusic() {
        if (!musicEnabled) return;

        try {
            // Load the setup music file
            File audioFile = new File("setup_music.wav"); // or .mp3, .au depending on your file
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            if (setupMusic != null && setupMusic.isOpen()) {
                setupMusic.close();
            }

            setupMusic = AudioSystem.getClip();
            setupMusic.open(audioStream);

            // Set volume
            setClipVolume(setupMusic, musicVolume);

            // Loop continuously
            setupMusic.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            System.err.println("Could not load setup music: " + e.getMessage());
            System.err.println("Make sure 'setup_music.wav' is in the same folder as your .java files");
        }
    }

    public void stopSetupMusic() {
        if (setupMusic != null && setupMusic.isRunning()) {
            setupMusic.stop();
        }
    }

    private void setClipVolume(Clip clip, float volume) {
        if (clip != null) {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float)(Math.log(volume) / Math.log(10.0) * 20.0);
                volumeControl.setValue(dB);
            } catch (Exception e) {
                // Volume control not available
            }
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
        if (!musicEnabled) return;

        // Try to load custom background music if not already loaded
        if (backgroundMusic == null || !backgroundMusic.isOpen()) {
            tryLoadCustomBackgroundMusic();
        }

        // If no custom music loaded, use generated music
        if (backgroundMusic == null) {
            generateBackgroundMusic();
        }

        try {
            if (backgroundMusic != null) {
                backgroundMusic.setFramePosition(0);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (Exception e) {
            System.err.println("Error starting background music: " + e.getMessage());
        }
    }

    /**
     * Try to load custom background music from file
     */
    private void tryLoadCustomBackgroundMusic() {
        try {
            File audioFile = new File("background_music.wav");
            if (audioFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

                if (backgroundMusic != null && backgroundMusic.isOpen()) {
                    backgroundMusic.close();
                }

                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioStream);

                // Set volume
                setMusicVolume(musicVolume);

                System.out.println("Loaded custom background music");
            }
        } catch (Exception e) {
            System.err.println("Could not load custom background music: " + e.getMessage());
            System.err.println("Will use generated background music instead");
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