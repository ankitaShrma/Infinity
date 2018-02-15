package com.ankita.dja;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.MediaController;

// to control the media attributes
// A view containing controls for a MediaPlayer.
// Typically contains the buttons like "Play/Pause", "Rewind", "Fast Forward" and a progress slider.
// to use this class, we need to instantiate programmatically
// Can also instantiate as follows:
// MediaController mediaController = new MediaController(getActivity());

public class MusicController extends MediaController {

    public MusicController(Context c){
        super(c);
    }
    /*@Override
    public void hide(){
        Log.d(this.getClass().getName(),"Hide");
        super.show();
    }*/


    @Override
        public void hide() {
      //      this.show(0);

        }

     /*   @Override
        public void setMediaPlayer(MediaPlayerControl player) {
            super.setMediaPlayer(player);
            this.show();
        }*/

    /*@Override
    public void show(int timeout) {
        Log.d(this.getClass().getName(),"Show");
        super.show(0);
    }*/



    @Override                                     ///backpressed on
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        int keyCode = event.getKeyCode();
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {

            Log.d(this.getClass().getName(),"DispACH");

            Context c = getContext();

            ((Activity) c).onBackPressed();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}