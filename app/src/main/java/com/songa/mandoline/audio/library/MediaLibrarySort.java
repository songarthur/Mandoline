package com.songa.mandoline.audio.library;

import com.songa.mandoline.audio.entity.Album;
import com.songa.mandoline.audio.entity.Artist;
import com.songa.mandoline.audio.entity.Track;

import java.util.Comparator;

public class MediaLibrarySort
{
    private MediaLibrarySort() {}

    public static Comparator<Track> trackComparatorByTrackNumber()
    {
        return new Comparator<Track>() {
            @Override
            public int compare(Track t1, Track t2) {
                if (t1==null && t2==null) {
                    return 0;
                } else if (t1==null) {
                    return -1;
                } else if (t2==null) {
                    return 1;
                }
                return (int)(t1.getTrackNumber()-t2.getTrackNumber());
            }
        };
    }

    public static Comparator<Track> trackComparatorByTitle()
    {
        return new Comparator<Track>() {
            @Override
            public int compare(Track t1, Track t2) {
                if (t1==null && t2==null) {
                    return 0;
                } else if (t1==null) {
                    return -1;
                } else if (t2==null) {
                    return 1;
                }
                return t1.getTrackTitle().compareTo(t2.getTrackTitle());
            }
        };
    }

    public static Comparator<Album> albumComparatorByYear()
    {
        // TODO
        return albumComparatorByTitle();
    }

    public static Comparator<Album> albumComparatorByTitle()
    {
        return new Comparator<Album>() {
            @Override
            public int compare(Album a1, Album a2) {
                if (a1==null && a2==null) {
                    return 0;
                } else if (a1==null) {
                    return -1;
                } else if (a2==null) {
                    return 1;
                }
                return a1.getAlbumName().compareTo(a2.getAlbumName());
            }
        };
    }

    public static Comparator<Artist> artistComparatorByName()
    {
        return new Comparator<Artist>() {
            @Override
            public int compare(Artist a1, Artist a2) {
                if (a1==null && a2==null) {
                    return 0;
                } else if (a1==null) {
                    return -1;
                } else if (a2==null) {
                    return 1;
                }
                return a1.getArtistName().compareTo(a2.getArtistName());
            }
        };
    }
}
