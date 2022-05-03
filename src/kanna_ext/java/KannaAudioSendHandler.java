package kanna_ext.java;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import java.nio.ByteBuffer;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class KannaAudioSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;
    
    public KannaAudioSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    public boolean canProvide() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }
        return lastFrame != null;
    }

    public ByteBuffer provide20MsAudio() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }

        byte[] data = lastFrame != null ? lastFrame.getData() : null;
        lastFrame = null;

        return ByteBuffer.wrap(data);
    }

    public boolean isOpus() {
        return true;
    }
}