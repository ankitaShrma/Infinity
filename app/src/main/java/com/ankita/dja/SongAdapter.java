package com.ankita.dja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ankita on 10-Mar-17.
 */

public class SongAdapter extends BaseAdapter{

    private ArrayList<Song> songs;
    // private LayoutInflater songInf;
    private Context songInf;

    public SongAdapter(Context c, ArrayList<Song> theSongs){
        songs=theSongs;
        //songInf=LayoutInflater.from(c);
        songInf = c;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        // LinearLayout songLay = (LinearLayout)songInf.inflate
        //       (R.layout.song, parent, false);
        View songLay =View.inflate(songInf, R.layout.song, null);
        ImageView image_songs = (ImageView) songLay.findViewById(R.id.song_image);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
        image_songs.setImageResource(R.drawable.musiclogofinalsize);
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }

}
