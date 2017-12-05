package com.songa.mandoline.audio.player;

public final class PlaybackMode
{
    private PlaybackMode() {}

    public enum PlaylistMode { ALL_TRACKS, ALBUM, ARTIST }

    public enum RepeatMode { NONE, PLAYLIST, TRACK }

}
