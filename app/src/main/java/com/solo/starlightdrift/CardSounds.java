package com.solo.starlightdrift;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import java.util.Random;

/** Short, locally synthesized PCM effects; follows the phone's media volume. */
final class CardSounds {
    private static final int RATE = 22050;
    private final AudioTrack[] tracks = new AudioTrack[3];

    CardSounds() {
        for (int effect = 0; effect < tracks.length; effect++) {
            AudioTrack track = null;
            try {
                short[] pcm = synthesize(effect);
                track = new AudioTrack.Builder()
                        .setAudioAttributes(new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_GAME)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
                        .setAudioFormat(new AudioFormat.Builder().setSampleRate(RATE)
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                        .setTransferMode(AudioTrack.MODE_STATIC)
                        .setBufferSizeInBytes(pcm.length * 2).build();
                if (track.write(pcm, 0, pcm.length) != pcm.length) {
                    track.release();
                    continue;
                }
                track.setVolume(0.65f);
                tracks[effect] = track;
            } catch (RuntimeException unavailable) {
                if (track != null) track.release();
                // Audio device failures must never interrupt a card game.
            }
        }
    }

    void pickUp() { play(0); }
    void place() { play(1); }
    void complete() { play(2); }

    private void play(int effect) {
        AudioTrack track = tracks[effect];
        if (track == null) return;
        try {
            track.stop();
            track.setPlaybackHeadPosition(0);
            track.play();
        } catch (IllegalStateException ignored) { }
    }

    void stop() {
        for (AudioTrack track : tracks) if (track != null) {
            try { track.stop(); } catch (IllegalStateException ignored) { }
        }
    }

    void release() {
        stop();
        for (int i = 0; i < tracks.length; i++) {
            if (tracks[i] != null) tracks[i].release();
            tracks[i] = null;
        }
    }

    private static short[] synthesize(int effect) {
        double duration = effect == 2 ? 0.95 : effect == 0 ? 0.09 : 0.13;
        short[] pcm = new short[(int) (RATE * duration)];
        Random random = new Random(31 + effect);
        double previousNoise = 0;
        double[] notes = {523.25, 659.25, 783.99, 1046.5};
        for (int i = 0; i < pcm.length; i++) {
            double t = i / (double) RATE;
            double sample = 0;
            if (effect == 2) {
                for (int n = 0; n < notes.length; n++) {
                    double age = t - n * 0.115;
                    if (age < 0) continue;
                    double envelope = Math.min(1, age / 0.008) * Math.exp(-age * 6);
                    sample += 0.24 * envelope * (Math.sin(2 * Math.PI * notes[n] * age)
                            + 0.2 * Math.sin(4 * Math.PI * notes[n] * age));
                }
            } else {
                double noise = random.nextDouble() * 2 - 1;
                double rustle = noise - previousNoise;
                previousNoise = noise;
                double envelope = Math.min(1, t / 0.003) * Math.exp(-t * (effect == 0 ? 50 : 38));
                // Light paper flick on pickup, lower padded tap on placement.
                sample = envelope * (0.24 * rustle + (effect == 0 ? 0.12 : 0.42)
                        * Math.sin(2 * Math.PI * (effect == 0 ? 1500 : 240) * t));
            }
            sample *= Math.min(1, (duration - t) / 0.02);
            pcm[i] = (short) (Math.max(-0.9, Math.min(0.9, sample)) * 32767);
        }
        return pcm;
    }
}
