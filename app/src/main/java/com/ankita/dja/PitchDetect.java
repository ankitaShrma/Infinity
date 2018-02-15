package com.ankita.dja;

//the quality of a sound governed by the rate of vibrations producing it; the degree of highness or lowness of a tone.
///Pitch detection is of interest whenever a single quasiÃ‚Â­periodic(irregular period) sound source is to be studied
//pitch detected when the freq is clear and stabble to distinguish from noise.
//beat is an interference pattern between two sounds of slightly different frequencies,
// perceived as a periodic variation in volume whose rate is the difference of the two frequencies.
//determined by how quickly the sound wave is making the air vibrate and has almost nothing to do with the intensity,
// or amplitude, of the wave.

//Digital signal processing
//TarsosDSP is a Java library for audio processing.
// Its aim is to provide an easy-to-use interface to practical music processing algorithms implemented,
// as simply as possible, in pure Java and without any other external dependencies.

//One simple approach would be to measure the distance between zero crossing points of the signal (i.e. the zero-crossing rate).
// However, this does not work well with complicated waveforms which are composed of multiple sine waves
// with differing periods or noisy data.
// Nevertheless, there are cases in which zero-crossing can be a useful measure,
// e.g. in some speech applications where a single source is assumed.

//In the time domain, a PDA typically estimates the period of a quasiperiodic signal, then inverts that value to give the frequency.
//More sophisticated approaches compare segments of the signal with other segments offset by a trial period to find a match.
// AMDF (average magnitude difference function),
// ASMDF (Average Squared Mean Difference Function), and other similar autocorrelation algorithms work this way.
// These algorithms can give quite accurate results for highly periodic signals.
// the YIN algorithm and the MPM algorithm are both based upon autocorrelation.
//The NSDF automatically generates an estimate of the clarity of the sound, describing how tone-like it is.

//In the frequency domain, polyphonic detection is possible,
// usually utilizing the periodogram to convert the signal to an estimate of the frequency spectrum.
// This requires more processing power as the desired accuracy increases, although the well-known efficiency of the FFT,(Fast Fourier Transform)
// a key part of the periodogram algorithm, makes it suitably efficient for many purposes.
//Fourier analysis converts a signal from its original domain (often time or space) to a representation in the frequency domain and vice versa.

// I strongly recommend using the MPM for any musical instrument pitch detection project. A problem with the MPM is the low pitch cutoff.

//A fast, accurate and robust method for finding the continuous pitch in monophonic musical sounds.
// [It uses] a special normalized version of the Squared Difference Function (SDF) coupled with a peak picking algorithm.

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;



import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.widget.MediaController.MediaPlayerControl;
import android.widget.ToggleButton;

import org.shokai.firmata.ArduinoFirmata;
import org.shokai.firmata.ArduinoFirmataEventHandler;


// Multithreading refers to two or more tasks executing concurrently within a single program.
// A thread is an independent path of execution within a program.
// Many threads can run concurrently within a program.
// Every thread in Java is created and controlled by the java.lang.Thread class.
// Looper is a class which is used to execute the Messages(Runnables) in a queue
// Android modifies the user interface and handles input events from one single thread, called the main thread.
// Android collects all events in this thread in a queue and processes this queue with an instance of the Looper class.
// Android supports the usage of the Thread class to perform asynchronous processing.
// Android also supplies the java.util.concurrent package to perform something in the background.
// For example, by using the ThreadPools and Executor classes.
// If you need to update the user interface from a new Thread, you need to synchronize with the main thread. Because of this restrictions, Android developer typically use Android specific code constructs.

// Android provides additional constructs to handle concurrently in comparison with standard Java.

// You can use the android.os.Handler class or the AsyncTasks classes.
// More sophisticated approaches are based on the Loader class, retained fragments and services.

////for pitch detection 2048 samples for buffersize is reasonable. Common - 1024, 2048
// Sample rate = when recording music or many types of acoustic events,
// audio waveforms are typically sampled at 44.1 kHz (CD), 48 kHz, 88.2 kHz, or 96 kHz.
// giving a 20 kHz maximum frequency. 20 kHz is the highest frequency generally audible by humans,
// so making 44.1 kHz the logical choice for most audio material.

//(samplerate    int audioBufferSize,        int bufferOverlap) ( samplerate size in overlap in samples)
// throws javax.sound.sampled.LineUnavailableException

// bufferOverlap: no overlap

// You have to use runOnUiThread() when you want to update your UI from a Non-UI Thread.
// For eg- If you want to update your UI from a background Thread.
// You can also use Handler for the same thing.
// Runs the specified action on the UI thread.
// If the current thread is the UI thread, then the action is executed immediately.
// If the current thread is not the UI thread, the action is posted to the event queue of the UI thread.


// If you need to update the user interface from a new Thread, you need to synchronize with the main thread.
// used in this library

/* Activity.runOnUiThread() is a special case of more generic Handlers.
 With Handler you can create your own event query within your own thread.
 Using Handlers instantiated with default constructor doesn't mean "code will run on UI thread" in general.
 By default, handlers binded to Thread from which they was instantiated from.

 To create Handler that is guaranteed to bind to UI (main) thread
 you should create Handler object binded to Main Looper like this:

 Handler mHandler = new Handler(Looper.getMainLooper());  */

// The amount of time allotted for processing is called the Buffer Size.
// Often times a smaller Buffer Size is desirable, however, not one that is too small. Here's why:
// If you have a very large Buffer Size, you will notice a lag between when you speak in to the Mic,
// and when the sound of your voice comes out of your speakers. While this can be very annoying,
// a large Buffer Size also makes recording audio less demanding on your computer.
// If you have a very small Buffer Size, you will notice little to no lag at all between speaking into the Mic
// and the audio coming out of the speakers. This makes recording and hearing your own singing much easier,
// however this can also place more strain on your computer, as it has very little time to process the audio.
import org.shokai.firmata.ArduinoFirmata;
import org.shokai.firmata.ArduinoFirmataEventHandler;


// Multithreading refers to two or more tasks executing concurrently within a single program.
// A thread is an independent path of execution within a program.
// Many threads can run concurrently within a program.
// Every thread in Java is created and controlled by the java.lang.Thread class.
// Looper is a class which is used to execute the Messages(Runnables) in a queue
// Android modifies the user interface and handles input events from one single thread, called the main thread.
// Android collects all events in this thread in a queue and processes this queue with an instance of the Looper class.
// Android supports the usage of the Thread class to perform asynchronous processing.
// Android also supplies the java.util.concurrent package to perform something in the background.
// For example, by using the ThreadPools and Executor classes.
// If you need to update the user interface from a new Thread, you need to synchronize with the main thread. Because of this restrictions, Android developer typically use Android specific code constructs.

// Android provides additional constructs to handle concurrently in comparison with standard Java.

// You can use the android.os.Handler class or the AsyncTasks classes.
// More sophisticated approaches are based on the Loader class, retained fragments and services.

////for pitch detection 2048 samples for buffersize is reasonable. Common - 1024, 2048
// Sample rate = when recording music or many types of acoustic events,
// audio waveforms are typically sampled at 44.1 kHz (CD), 48 kHz, 88.2 kHz, or 96 kHz.
// giving a 20 kHz maximum frequency. 20 kHz is the highest frequency generally audible by humans,
// so making 44.1 kHz the logical choice for most audio material.

//(samplerate    int audioBufferSize,        int bufferOverlap) ( samplerate size in overlap in samples)
// throws javax.sound.sampled.LineUnavailableException

// bufferOverlap: no overlap

// You have to use runOnUiThread() when you want to update your UI from a Non-UI Thread.
// For eg- If you want to update your UI from a background Thread.
// You can also use Handler for the same thing.
// Runs the specified action on the UI thread.
// If the current thread is the UI thread, then the action is executed immediately.
// If the current thread is not the UI thread, the action is posted to the event queue of the UI thread.


// If you need to update the user interface from a new Thread, you need to synchronize with the main thread.
// used in this library

/* Activity.runOnUiThread() is a special case of more generic Handlers.
 With Handler you can create your own event query within your own thread.
 Using Handlers instantiated with default constructor doesn't mean "code will run on UI thread" in general.
 By default, handlers binded to Thread from which they was instantiated from.

 To create Handler that is guaranteed to bind to UI (main) thread
 you should create Handler object binded to Main Looper like this:

 Handler mHandler = new Handler(Looper.getMainLooper());  */

// The amount of time allotted for processing is called the Buffer Size.
// Often times a smaller Buffer Size is desirable, however, not one that is too small. Here's why:
// If you have a very large Buffer Size, you will notice a lag between when you speak in to the Mic,
// and when the sound of your voice comes out of your speakers. While this can be very annoying,
// a large Buffer Size also makes recording audio less demanding on your computer.
// If you have a very small Buffer Size, you will notice little to no lag at all between speaking into the Mic
// and the audio coming out of the speakers. This makes recording and hearing your own singing much easier,
// however this can also place more strain on your computer, as it has very little time to process the audio.




public class PitchDetect extends AppCompatActivity
        implements MusicController.MediaPlayerControl {


    //We are going to play the music in the Service class,
    // but control it from the Activity class, where the application's user interface operates.
    // To accomplish this, we will have to bind to the Service class.

    /*************************************************************************/

    private ArrayList<Song> songList;
    private ListView songView;

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    /*************************************************************************/

    private MusicController controller;


    private boolean paused=false, playbackPaused=false;






    /**********************************************************************************/

    /////////////////////////////////////////////////////////////////////////////////////////

    String TAG = "AndroidFirmata";

      private Handler handler;
    private ArduinoFirmata arduino;
    private ToggleButton btnDigitalWrite;
    private Button btnCheck;

    //////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_detect);

        songView = (ListView)findViewById(R.id.song_list);

        songList = new ArrayList<Song>();

        final TextView textView = (TextView) findViewById(R.id.textView1);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

      //  actionBar.setDisplayHomeAsUpEnabled(true);

        // SongAdapter songAdt = new SongAdapter(this, songList);
//        songView.setAdapter(songAdt);

        getSongList();

        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        //////////          //////////          //////////          //////////          //////////

        handler=new Handler(this.getMainLooper());
       // final TextView textView =(TextView) findViewById(R.id.textView1);

      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22100, 1024, 0);
                dispatcher.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.MPM, 22100, 1024, 0, new PitchDetectionHandler() {
                    @Override
                    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                           try{

                        final float pitchInHz = pitchDetectionResult.getPitch();
                            processPitch(pitchInHz);
                           } catch (Exception e){

                               Log.e(TAG, "You have an error");
                         }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                           //     processPitch(pitchInHz);
                                textView.setText(" " + pitchInHz);
                            }
                        });

                    }
                }));
            }
        }).start();*/

      /*  AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100, 2048, 0);

        dispatcher.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.FFT_YIN, 44100, 2048, new PitchDetectionHandler() {
            //http://miracle.otago.ac.nz/tartini/papers/A_Smarter_Way_to_Find_Pitch.pdf
            //A fast, accurate and robust method for finding the continuous pitch in monophonic musical sounds.
            //https://0110.be/releases/TarsosDSP/TarsosDSP-1.6/TarsosDSP-1.6-Documentation/be/hogent/tarsos/dsp/pitch/PitchProcessor.PitchEstimationAlgorithm.html

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                final float pitchInHz = pitchDetectionResult.getPitch();

                runOnUiThread(new Runnable() {
               // Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        //  }
                        //   }
                        //@Override
                        //  public void run() {
                        TextView text = (TextView) findViewById(R.id.textView1);

                        text.setText(" " + pitchInHz);
                        processPitch(pitchInHz);


                    }
                     });
                };


               // handler.post(runnable);
            }
       }));

        new Thread(dispatcher, "Audio Dispatcher").start();*/







        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22100,1024,0);


        dispatcher.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.MPM, 22100, 1024, new PitchDetectionHandler() {
            //http://miracle.otago.ac.nz/tartini/papers/A_Smarter_Way_to_Find_Pitch.pdf
            //A fast, accurate and robust method for finding the continuous pitch in monophonic musical sounds.
            //https://0110.be/releases/TarsosDSP/TarsosDSP-1.6/TarsosDSP-1.6-Documentation/be/hogent/tarsos/dsp/pitch/PitchProcessor.PitchEstimationAlgorithm.html

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                final float pitchInHz = pitchDetectionResult.getPitch();


//////////////////////////////////////////////////////////////////////

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView text = (TextView) findViewById(R.id.textView1);


                        processPitch(pitchInHz);


                    }
                });
               /* new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        processPitch(pitchInHz);
                        // code goes here
                    }
                });*/


            }
        }));
        new Thread(dispatcher,"Audio Dispatcher").start();



        //////////          //////////          //////////          //////////          //////////

        /**************************************/
        setController();
        /**************************************/

        ////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////ARDUINO////////////////////////////////////////////

        this.btnDigitalWrite = (ToggleButton) findViewById(R.id.btn_digital_write);



        Log.v(TAG, "start");

        //this.setTitle(this.getTitle() + " v" + ArduinoFirmata.VERSION);

        this.arduino = new ArduinoFirmata(this);
        final Activity self = this;
        arduino.setEventHandler(new ArduinoFirmataEventHandler() {
            public void onError(String errorMessage) {
                Log.e(TAG, errorMessage);
            }

            public void onClose() {
                Log.v(TAG, "arduino closed");
                self.finish();
            }
        });


        //to connect the arduino..... needs exception handling

        try {
            arduino.connect();
            Log.v(TAG, "Board Version : " + arduino.getBoardVersion());
            // Log.d(TAG, "No Connection");

        } catch (IOException e) {
            e.printStackTrace();

            // finish();         //so as the app wouldn't close itself
        } catch (InterruptedException e) {
            e.printStackTrace();
            //   finish();
        }

        ////////////////////////////////////////////////////////////////////////////////////////////


        btnDigitalWrite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton btn, final boolean isChecked) {

                Log.v(TAG, isChecked ? "LED on" : "LED off");
                //  arduino.digitalWrite(10, isChecked);


            }
        });




    }

    //////////////////////connect to the service////////////////////////////

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("MAIN ACT", "Inside disconnection");
            musicBound = false;
            musicSrv=null;
        }
    };
    ////////////////////////////////////////////////////////////////////////


    /*************************************************************************/

    @Override
    protected void onStart() {
        // setController();
        super.onStart();
        //  setController();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
            Log.e("MAIN ACT", "Inside onstart" + musicBound);
        }
        // setController();
    }

    /*************************************************************************/

    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        // Set up receiver for media player onPrepared broadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(onPrepareReceiver,
                new IntentFilter("MEDIA_PLAYER_PREPARED"));
        if(paused){
            setController();
            paused=false;
        }

    }
   /* @Override
    protected void onStop() {
        Log.e("MAIN ACT", "Inside onstop");
        if (playIntent != null) {
            Log.e("MAIN ACT", "Inside onstop1");
            unbindService(musicConnection);
            musicBound = false;
            boolean flagservice = stopService(playIntent);
            Log.d("MAIN ACT", "Inside onstop1" + flagservice);

        }
        controller.hide();
        super.onStop();
    }*/

    boolean twice;
    @Override
    public void onBackPressed() {

       // Intent intent_pitch = new Intent(PitchDetect.this, MainActivity.class);
        //startActivity(intent_pitch);
          System.exit(0);
    //    finish();



    }


       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if(twice == true) {
          //  if (musicSrv != null) {
                super.onBackPressed();
                Log.e("MAIN ACT", "Inside onbackpress");
          //  }

        }
        twice = true;

        Toast.makeText(MainActivity.this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                twice = false;

            }
        }, 3000);*/





    //  @Override
    //  protected void onDestroy() {
    //    stopService(playIntent);
    //  musicSrv=null;
    //    super.onDestroy();
    //   Log.e("MAIN ACT", "Inside onDestroy");
    //  musicSrv=null;
    //  }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
     /*   int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        switch (item.getItemId()) {

            case R.id.home:
               System.exit(0);

                break;

            case R.id.action_end:
                //  stopService(playIntent);
                //  musicSrv=null;
                System.exit(0);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    ////////////////////////////////////////////////////////////////////////////

    public void processPitch(float pitchInHz) {

        TextView text = (TextView) findViewById(R.id.textView1);
        TextView noteText = (TextView) findViewById(R.id.textView2);
        text.setText("" + pitchInHz);

        if (btnDigitalWrite.isChecked() == true) {

            if (pitchInHz == -1) {
                arduino.digitalWrite(13, true);
                //  arduino.digitalWrite(13,true);
                arduino.digitalWrite(12, false);
               // arduino.digitalWrite(13, false);

                arduino.digitalWrite(10, false);
                arduino.digitalWrite(8, true);
                arduino.digitalWrite(6, false);
              arduino.digitalWrite(2, true);


            } else if (pitchInHz < 100) {
                noteText.setText("Y");
                arduino.digitalWrite(12, true);
                arduino.digitalWrite(13, false);
                arduino.digitalWrite(10, false);
                arduino.digitalWrite(8, true);
                arduino.digitalWrite(6, true);
               arduino.digitalWrite(2, false);

            } else if (pitchInHz > 100 && pitchInHz < 170) {
                noteText.setText("A");
                arduino.digitalWrite(13, false);
                arduino.digitalWrite(12, true);
                arduino.digitalWrite(8, false);
                arduino.digitalWrite(10, true);
                arduino.digitalWrite(2, false);
            } else if (pitchInHz > 170 && pitchInHz < 230) {
                noteText.setText("B");
                arduino.digitalWrite(13, true);
                arduino.digitalWrite(12, false);
                arduino.digitalWrite(8, true);
                arduino.digitalWrite(10, false);
                arduino.digitalWrite(6, false);
                arduino.digitalWrite(2, true);

            } else if (pitchInHz > 230 && pitchInHz < 257) {
                noteText.setText("C");
               arduino.digitalWrite(13, false);
                arduino.digitalWrite(12, true);
                arduino.digitalWrite(8, false);
                arduino.digitalWrite(10, false);
                arduino.digitalWrite(6, true);
                arduino.digitalWrite(2, true);
            } else if (pitchInHz > 257 && pitchInHz < 270) {
                noteText.setText("D");
                arduino.digitalWrite(13, true);
                arduino.digitalWrite(12, true);
                arduino.digitalWrite(8, false);
                arduino.digitalWrite(6, true);
                arduino.digitalWrite(10, false);
                arduino.digitalWrite(2, true);
                //  arduino.digitalWrite(13, false);
            } else if (pitchInHz > 270 && pitchInHz < 320) {
                noteText.setText("E");
                arduino.digitalWrite(13, false);
                arduino.digitalWrite(12, false);
                arduino.digitalWrite(8, true);
                arduino.digitalWrite(6, true);
                arduino.digitalWrite(10, false);
                arduino.digitalWrite(2, true);
            } else if (pitchInHz > 320 && pitchInHz < 470) {
                noteText.setText("F");
                arduino.digitalWrite(13, true);
                arduino.digitalWrite(12, false);
                arduino.digitalWrite(8, false);
                arduino.digitalWrite(6, true);
                arduino.digitalWrite(10, true);
                arduino.digitalWrite(2, false);
            } else if (pitchInHz > 470 && pitchInHz < 570) {
                noteText.setText("G");
            //    arduino.digitalWrite(13, false);
                arduino.digitalWrite(12, true);
                arduino.digitalWrite(8, false);
                arduino.digitalWrite(10, true);
               // arduino.digitalWrite(2, false);
                //  arduino.digitalWrite(13, false);
            } else if (pitchInHz > 570 && pitchInHz < 650) {
                noteText.setText("H");
                arduino.digitalWrite(13, true);
                arduino.digitalWrite(12, false);
                arduino.digitalWrite(8, true);
                arduino.digitalWrite(10, true);
                arduino.digitalWrite(6, false);
                arduino.digitalWrite(2, true);
                //  arduino.digitalWrite(13, false);
            } else if (pitchInHz > 650 && pitchInHz < 750) {
                noteText.setText("I");
                arduino.digitalWrite(13, false);
                arduino.digitalWrite(12, true);
                arduino.digitalWrite(8, false);
                arduino.digitalWrite(10, true);
                arduino.digitalWrite(6, true);
                arduino.digitalWrite(2, false);
                //  arduino.digitalWrite(13, false);
            } else if (pitchInHz > 750 && pitchInHz < 900) {
                noteText.setText("J");
                arduino.digitalWrite(13, false);
                arduino.digitalWrite(12, true);
                arduino.digitalWrite(8, true);
                arduino.digitalWrite(10, false);
                arduino.digitalWrite(6, false);
                arduino.digitalWrite(2, true);
                //  arduino.digitalWrite(13, false);
            } else if (pitchInHz > 900 && pitchInHz < 1100) {
                noteText.setText("J");
                arduino.digitalWrite(13, true);
                arduino.digitalWrite(12, false);
                arduino.digitalWrite(8, false);
                arduino.digitalWrite(10, true);
                arduino.digitalWrite(2, false);
                //  arduino.digitalWrite(13, false);
            } else if (pitchInHz > 1100 && pitchInHz < 1700) {
                noteText.setText("K");
                arduino.digitalWrite(13, false);
                arduino.digitalWrite(12, true);
                arduino.digitalWrite(8, false);
                arduino.digitalWrite(6, true);
                arduino.digitalWrite(10, true);
                arduino.digitalWrite(2, false);
                //  arduino.digitalWrite(13, false);
            } else if (pitchInHz > 1700 && pitchInHz < 2500) {
                noteText.setText("L");
                arduino.digitalWrite(13, true);
                arduino.digitalWrite(12, false);
                arduino.digitalWrite(8, true);
                arduino.digitalWrite(10, false);
                arduino.digitalWrite(6, true);
                arduino.digitalWrite(2, false);
                //  arduino.digitalWrite(13, false);
            } else if (pitchInHz > 2500 && pitchInHz < 3300) {
                noteText.setText("M");
                arduino.digitalWrite(13, false);
                arduino.digitalWrite(12, true);
                arduino.digitalWrite(8, false);
                arduino.digitalWrite(10, true);
                arduino.digitalWrite(6, true);
                arduino.digitalWrite(2, false);
                //  arduino.digitalWrite(13, false);
            } else {
                noteText.setText("X");
               arduino.digitalWrite(13, false);
                arduino.digitalWrite(12, true);
                arduino.digitalWrite(8, true);
                arduino.digitalWrite(10, false);
                arduino.digitalWrite(6, true);
                arduino.digitalWrite(2, false);
                // arduino.digitalWrite(13, true);*/


            }
        }
          //  else {
            //    arduino.close();
            //}

    }


    ////////////////////////////////////////////////////////////////////////////

    /*************************************************************************/

    public void getSongList() {
        //retrieve song info
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);


        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);

                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }

    public void songPicked(View view){
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
        Log.d(this.getClass().getName(), "Inside song picked");
    }


    //onDestroy()

    /*************************************************************************/


    /////////////////////////   CONTROLLER  ////////////////////////


    private void setController(){
        //set the controller up
        //  controller = null;
        // controller.show(0);
        if (controller == null) controller = new MusicController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.view));
        controller.setEnabled(true);
        //controller.show();
    }
    @Override
    public void onAttachedToWindow() {          //badtoken error
        super.onAttachedToWindow();
        try{
            controller.show(0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //// Broadcast receiver to determine when music player has been prepared

    private BroadcastReceiver onPrepareReceiver = new BroadcastReceiver() {
        // @Override
        public void onReceive(Context c, Intent i) {
            // When music player has been prepared, show controller
            controller.show(0);
        }
    };

    //play next
    private void playNext(){
        musicSrv.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    //play previous
    private void playPrev(){
        musicSrv.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }



    @Override
    public void start() {
        setController();
        controller.show(0);

        musicSrv.go();
    }

    @Override
    public void pause() {

        playbackPaused=true;
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
        // controller.show(0);
    }

    @Override
    public int getCurrentPosition() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);


    }

    @Override
    public boolean isPlaying() {
        if (musicSrv != null && musicBound) {
            Log.e("MAIN ACT", "Inside isplaying");
            boolean value = musicSrv.isPng();
            return value;
        } else
            return false;

    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    /////////////////////////////////////////////////////////////////////////

}


