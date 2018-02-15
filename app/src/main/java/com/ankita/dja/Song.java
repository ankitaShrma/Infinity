package com.ankita.dja;

import javax.xml.datatype.Duration;

/**
 * Created by Ankita on 10-Mar-17.
 */

public class Song {

    private long id;
    private String title;
    private String artist;
    private Duration length;

    public Song(long songID, String songTitle, String songArtist) {
        id=songID;
        title=songTitle;
        artist=songArtist;
       // length = songLength;

    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
   // public Duration getLength(){return length;}

}
