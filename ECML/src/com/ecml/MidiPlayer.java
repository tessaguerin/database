/*
 * Copyright (c) 2007-2012 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */

package com.ecml;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


/** @class MidiPlayer
 *
 * The MidiPlayer is the panel at the top used to play the sound
 * of the midi file.  It consists of:
 *
 * - The Rewind button
 * - The Play/Pause button
 * - The Stop button
 * - The Fast Forward button
 * - The Playback speed bar
 *
 * The sound of the midi file depends on
 * - The MidiOptions (taken from the menus)
 *   Which tracks are selected
 *   How much to transpose the keys by
 *   What instruments to use per track
 * - The tempo (from the Speed bar)
 * - The volume
 *
 * The MidiFile.ChangeSound() method is used to create a new midi file
 * with these options.  The mciSendString() function is used for 
 * playing, pausing, and stopping the sound.
 *
 * For shading the notes during playback, the method
 * SheetMusic.ShadeNotes() is used.  It takes the current 'pulse time',
 * and determines which notes to shade.
 */
public class MidiPlayer extends LinearLayout {
    static Bitmap rewindImage;           /** The rewind image */
    static Bitmap playImage;             /** The play image */
    static Bitmap pauseImage;            /** The pause image */
    static Bitmap stopImage;             /** The stop image */
    static Bitmap fastFwdImage;          /** The fast forward image */
    static Bitmap volumeImage;           /** The volume image */
    static Bitmap muteOnImage;			 /** The mute image */
    static Bitmap muteOffImage;			 /** The unmute image */
    static Bitmap pianoImage;			 /** The piano image */
    static Bitmap playAndRecordImage;	 /** The rec and play image */
    static Bitmap playRecordImage;		 /** The replay record image */
    static Bitmap plusImage;			 /** The + image for the speed bar */
    static Bitmap minusImage;			 /** The - image for the speed bar */
    
    private ImageButton rewindButton;    /** The rewind button */
    private ImageButton playButton;      /** The play/pause button */
    private ImageButton stopButton;      /** The stop button */
    private ImageButton fastFwdButton;   /** The fast forward button */
    private ImageButton muteButton;      /** The mute button */
    ImageButton pianoButton;	 		 /** The piano button */
    ImageButton playAndRecordButton;	 /** The play and record button (mutes aswell) */
    ImageButton playRecordButton;		 /** The replay record button */
    private ImageButton plusButton;		 /** The + button for the speed bar */
    private ImageButton minusButton;	 /** The - button for the speed bar */
    private TextView speedText;          /** The "Speed %" label */
    private SeekBar speedBar;    		 /** The seekbar for controlling the playback speed */
    
    boolean mute; 				 /** Tell whether or not the volume is mute */
    int volume;			 		 /** Used to set the volume to zero */
    AudioManager audioManager;   /** AudioManager used to mute and unmute music sound */
    
    int playstate;               /** The playing state of the Midi Player */
    final int stopped   = 1;     /** Currently stopped */
    final int playing   = 2;     /** Currently playing music */
    final int paused    = 3;     /** Currently paused */
    final int initStop  = 4;     /** Transitioning from playing to stop */
    final int initPause = 5;     /** Transitioning from playing to pause */
    int delay = 1000;			 /** Delay before playing the music */

    final String tempSoundFile = "playing.mid"; /** The filename to play sound from */

    MediaPlayer player;         /** For playing the audio */
    MidiFile midifile;          /** The midi file to play */
    MidiOptions options;        /** The sound options for playing the midi file */
    double pulsesPerMsec;       /** The number of pulses per millisec */
    SheetMusic sheet;           /** The sheet music to shade while playing */
    Piano piano;                /** The piano to shade while playing */
    Handler timer;              /** Timer used to update the sheet music while playing */
    long startTime;             /** Absolute time when music started playing (msec) */
    double startPulseTime;      /** Time (in pulses) when music started playing */
    double currentPulseTime;    /** Time (in pulses) music is currently at */
    double prevPulseTime;       /** Time (in pulses) music was last at */
    Activity activity;          /** The parent activity. */

    
    /** Load the play/pause/stop button images */
    public static void LoadImages(Context context) {
        if (rewindImage != null) {
            return;
        }
        Resources res = context.getResources();
        rewindImage = BitmapFactory.decodeResource(res, R.drawable.rewind);
        playImage = BitmapFactory.decodeResource(res, R.drawable.play);
        pauseImage = BitmapFactory.decodeResource(res, R.drawable.pause);
        stopImage = BitmapFactory.decodeResource(res, R.drawable.stop);
        fastFwdImage = BitmapFactory.decodeResource(res, R.drawable.fastforward);
        muteOnImage = BitmapFactory.decodeResource(res, R.drawable.mute_on);
        muteOffImage = BitmapFactory.decodeResource(res, R.drawable.mute_off);
        pianoImage = BitmapFactory.decodeResource(res, R.drawable.piano_icon);
        plusImage = BitmapFactory.decodeResource(res, R.drawable.plus);
        minusImage = BitmapFactory.decodeResource(res, R.drawable.minus);
        playAndRecordImage = BitmapFactory.decodeResource(res, R.drawable.record);
        playRecordImage = BitmapFactory.decodeResource(res, R.drawable.recordplay);
    }


    /** Create a new MidiPlayer, displaying the play/stop buttons, and the
     *  speed bar.  The midifile and sheetmusic are initially null.
     */
    public MidiPlayer(Activity activity) {
        super(activity);
        LoadImages(activity);
        this.activity = activity;
        this.midifile = null;
        this.options = null;
        this.sheet = null;
        playstate = stopped;
        startTime = SystemClock.uptimeMillis();
        startPulseTime = 0;
        currentPulseTime = 0;
        prevPulseTime = -10;
        this.setPadding(0, 0, 0, 0);
        
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mute = (volume == 0);
        
        CreateButtons();

        int screenwidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        int screenheight = activity.getWindowManager().getDefaultDisplay().getHeight();
        Point newsize = MidiPlayer.getPreferredSize(screenwidth, screenheight);
        resizeButtons(newsize.x, newsize.y);
        player = new MediaPlayer();
        setBackgroundColor(getResources().getColor(R.color.orange));
    }

    /** Get the preferred width/height given the screen width/height */
    public static Point getPreferredSize(int screenwidth, int screenheight) {
    	int height = (int) (5.0 * screenwidth / ( 2 + Piano.KeysPerOctave * Piano.MaxOctave));
    	height = height * 2/3 ;
    	Point result = new Point(screenwidth, height);
        return result;
    }

    /** Determine the measured width and height.
     *  Resize the individual buttons according to the new width/height.
     */
    @Override
    protected void onMeasure(int widthspec, int heightspec) {
        super.onMeasure(widthspec, heightspec);
        int screenwidth = MeasureSpec.getSize(widthspec);
        int screenheight = MeasureSpec.getSize(heightspec);

        /* Make the button height 2/3 the piano WhiteKeyHeight */
        int width = screenwidth;
        int height = (int) (5.0 * screenwidth / ( 2 + Piano.KeysPerOctave * Piano.MaxOctave));
        height = height * 2/3 * 2/3;
        setMeasuredDimension(width, height);
    }

    /** When this view is resized, adjust the button sizes */
    @Override
    protected void 
    onSizeChanged(int newwidth, int newheight, int oldwidth, int oldheight) {
        resizeButtons(newwidth, newheight);
        super.onSizeChanged(newwidth, newheight, oldwidth, oldheight);
    }
     

    /** Create the rewind, play, stop, fast forward,mute and piano buttons */
    void CreateButtons() {
        this.setOrientation(LinearLayout.HORIZONTAL);

        /* Create the rewind button */
        rewindButton = new ImageButton(activity);
        rewindButton.setBackgroundColor(getResources().getColor(R.color.orange));
        rewindButton.setImageBitmap(rewindImage);
        rewindButton.setScaleType(ImageView.ScaleType.FIT_XY);
        rewindButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Rewind();
            }
        });
        this.addView(rewindButton);

        /* Create the stop button */
        stopButton = new ImageButton(activity);
        stopButton.setBackgroundColor(getResources().getColor(R.color.orange));
        stopButton.setImageBitmap(stopImage);
        stopButton.setScaleType(ImageView.ScaleType.FIT_XY);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Stop();
            }
        });
        this.addView(stopButton);

        
        /* Create the play button */
        playButton = new ImageButton(activity);
        playButton.setBackgroundColor(getResources().getColor(R.color.orange));
        playButton.setImageBitmap(playImage);
        playButton.setScaleType(ImageView.ScaleType.FIT_XY);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Play();
            }
        });
        this.addView(playButton);        
        
        /* Create the fastFwd button */        
        fastFwdButton = new ImageButton(activity);
        fastFwdButton.setBackgroundColor(getResources().getColor(R.color.orange));
        fastFwdButton.setImageBitmap(fastFwdImage);
        fastFwdButton.setScaleType(ImageView.ScaleType.FIT_XY);
        fastFwdButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FastForward();
            }
        });
        this.addView(fastFwdButton);
        
        
        /* Create the text before the speed bar */
        speedText = new TextView(activity);
        speedText.setText("  Speed: 100% ");
        speedText.setTextColor(Color.parseColor("#FFFFFF"));
        speedText.setGravity(Gravity.CENTER);
        speedText.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				backTo100();
			}
        });
        this.addView(speedText);
        
        
        /* Create the - button for the speed bar */        
        minusButton = new ImageButton(activity);
        minusButton.setBackgroundColor(getResources().getColor(R.color.orange));
        minusButton.setImageBitmap(minusImage);
        minusButton.setScaleType(ImageView.ScaleType.FIT_XY);
        minusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                minus();
            }
        });
        this.addView(minusButton);


        /* Create the Speed bar */
        speedBar = new SeekBar(activity);
        speedBar.setMax(180-30); //added later
        speedBar.setProgress(100-30); //added later
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
            	// we add 30 to avoid reaching values under 30
                speedText.setText("  Speed: " + String.format(Locale.US, "%03d", progress + 30 /* removed earlier */) + "% ");
            }
            public void onStartTrackingTouch(SeekBar bar) {
            }
            public void onStopTrackingTouch(SeekBar bar) {
            }
        });
        this.addView(speedBar);
                
        /* Create the + button for the speed bar */        
        plusButton = new ImageButton(activity);
        plusButton.setBackgroundColor(getResources().getColor(R.color.orange));
        plusButton.setImageBitmap(plusImage);
        plusButton.setScaleType(ImageView.ScaleType.FIT_XY);
        plusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                plus();
            }
        });
        this.addView(plusButton);

        
        /* Create the mute button */        
        muteButton = new ImageButton(activity);
        muteButton.setBackgroundColor(getResources().getColor(R.color.orange));
        if (mute) {
        	muteButton.setImageBitmap(muteOnImage);
        } else {
        	muteButton.setImageBitmap(muteOffImage);
        }
        muteButton.setScaleType(ImageView.ScaleType.FIT_XY);
        muteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (mute) {
            		unmute();
            	} else {
            		mute();
            	}
            }
        });
        this.addView(muteButton);
        
        /* Create the piano button */        
        pianoButton = new ImageButton(activity);
        pianoButton.setBackgroundColor(getResources().getColor(R.color.orange));
        pianoButton.setImageBitmap(pianoImage);
        pianoButton.setScaleType(ImageView.ScaleType.FIT_XY);
        this.addView(pianoButton);
        
        /* Create the Play and Record button */
        playAndRecordButton = new ImageButton(activity);
        playAndRecordButton.setBackgroundColor(getResources().getColor(R.color.orange));
        playAndRecordButton.setImageBitmap(playAndRecordImage);
        playAndRecordButton.setScaleType(ImageView.ScaleType.FIT_XY);
        this.addView(playAndRecordButton);
        
        /* Create the Play button for the record button */
        playRecordButton = new ImageButton(activity);
        playRecordButton.setBackgroundColor(getResources().getColor(R.color.orange));
        playRecordButton.setImageBitmap(playRecordImage);
        playRecordButton.setScaleType(ImageView.ScaleType.FIT_XY);
        this.addView(playRecordButton);
        
        /* Initialize the timer used for playback, but don't start
         * the timer yet (enabled = false).
         */
        timer = new Handler();
    }

    void resizeButtons(int newwidth, int newheight) {
        int buttonheight = newheight;
        int pad = buttonheight/6;
        rewindButton.setPadding(pad, pad, pad, pad);
        stopButton.setPadding(pad, pad, pad, pad);
        playButton.setPadding(pad, pad, pad, pad);
        fastFwdButton.setPadding(pad, pad, pad, pad);
        muteButton.setPadding(pad, pad, pad, pad);
        plusButton.setPadding(pad, pad, pad, pad);
        minusButton.setPadding(pad, pad, pad, pad);

        LinearLayout.LayoutParams params;
        
        params = new LinearLayout.LayoutParams(buttonheight, buttonheight);
        params.width = buttonheight;
        params.height = buttonheight;
        params.bottomMargin = 0;
        params.topMargin = 0;
        params.rightMargin = 0;
        params.leftMargin = buttonheight/6;

        rewindButton.setLayoutParams(params);
        
        params = new LinearLayout.LayoutParams(buttonheight, buttonheight);
        params.bottomMargin = 0;
        params.topMargin = 0;
        params.rightMargin = 0;
        params.leftMargin = 0;

        playButton.setLayoutParams(params);
        stopButton.setLayoutParams(params);
        fastFwdButton.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) speedText.getLayoutParams();
        params.height = buttonheight;
        speedText.setLayoutParams(params);
        
        params = new LinearLayout.LayoutParams(buttonheight, buttonheight);
        params.bottomMargin = 0;
        params.topMargin = 0;
        params.rightMargin = 0;
        params.leftMargin = 0;
        
        minusButton.setLayoutParams(params);
        
        params = new LinearLayout.LayoutParams(buttonheight * 2, buttonheight);
        params.width = buttonheight * 2;
        params.bottomMargin = 0;
        params.leftMargin = 0;
        params.topMargin = 0;
        params.rightMargin = 0;
        speedBar.setLayoutParams(params);
        speedBar.setPadding(pad, pad, pad, pad);

        params = new LinearLayout.LayoutParams(buttonheight, buttonheight);
        params.bottomMargin = 0;
        params.topMargin = 0;
        params.rightMargin = 0;
        params.leftMargin = 0;
        plusButton.setLayoutParams(params);
        muteButton.setLayoutParams(params);
        pianoButton.setLayoutParams(params);
        playAndRecordButton.setLayoutParams(params);
        playRecordButton.setLayoutParams(params);
        playRecordButton.setVisibility(View.GONE);
    }
    
    public void SetPiano(Piano p, MidiOptions options) {
        piano = p;
        if (!options.showPiano) {
			piano.setVisibility(View.GONE);
		} else {
			piano.setVisibility(View.VISIBLE);
		}
    }

    /** The MidiFile and/or SheetMusic has changed. Stop any playback sound,
     *  and store the current midifile and sheet music.
     */
    public void SetMidiFile(MidiFile file, MidiOptions opt, SheetMusic s) {

        /* If we're paused, and using the same midi file, redraw the
         * highlighted notes.
         */
        if ((file == midifile && midifile != null && playstate == paused)) {
            options = opt;
            sheet = s;
            sheet.ShadeNotes((int)currentPulseTime, (int)-1, SheetMusic.DontScroll);

            /* We have to wait some time (200 msec) for the sheet music
             * to scroll and redraw, before we can re-shade.
             */
            timer.removeCallbacks(TimerCallback);
            timer.postDelayed(ReShade, 500);
        }
        else {
            Stop();
            midifile = file;
            options = opt;
            sheet = s;
        }
    }

    /** If we're paused, reshade the sheet music and piano. */
    Runnable ReShade = new Runnable() {
      public void run() {
        if (playstate == paused || playstate == stopped) {
            sheet.ShadeNotes((int)currentPulseTime, (int)-10, SheetMusic.ImmediateScroll);
            piano.ShadeNotes((int)currentPulseTime, (int)prevPulseTime);
        }
      }
    };


    /** Return the number of tracks selected in the MidiOptions.
     *  If the number of tracks is 0, there is no sound to play.
     */
    private int numberTracks() {
        int count = 0;
        for (int i = 0; i < options.tracks.length; i++) {
            if (!options.mute[i]) {
                count += 1;
            }
        }
        return count;
    }

    /** Create a new midi file with all the MidiOptions incorporated.
     *  Save the new file to playing.mid, and store
     *  this temporary filename in tempSoundFile.
     */ 
    private void CreateMidiFile() {
        double inverse_tempo = 1.0 / midifile.getTime().getTempo();
        // we add 30 to avoid reaching values under 30
        double inverse_tempo_scaled = inverse_tempo * (speedBar.getProgress() + 30.0) / 100.0;
        // double inverse_tempo_scaled = inverse_tempo * 100.0 / 100.0;
        options.tempo = (int)(1.0 / inverse_tempo_scaled);
        pulsesPerMsec = midifile.getTime().getQuarter() * (1000.0 / options.tempo);

        try {
            FileOutputStream dest = activity.openFileOutput(tempSoundFile, Context.MODE_PRIVATE);
            midifile.ChangeSound(dest, options);
            dest.close();
            // checkFile(tempSoundFile);
        }
        catch (IOException e) {
            Toast toast = Toast.makeText(activity, "Error: Unable to create MIDI file for playing.", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void checkFile(String name) {
        try {
            FileInputStream in = activity.openFileInput(name);
            byte[] data = new byte[4096];
            int total = 0, len = 0;
            while (true) {
                len = in.read(data, 0, 4096);
                if (len > 0)
                    total += len;
                else
                    break;
            } 
            in.close();
            data = new byte[total];
            in = activity.openFileInput(name);
            int offset = 0;
            while (offset < total) {
                len = in.read(data, offset, total - offset);
                if (len > 0)
                    offset += len;
            }
            in.close();
            MidiFile testmidi = new MidiFile(data, name); 
        }
        catch (IOException e) {
            Toast toast = Toast.makeText(activity, "CheckFile: " + e.toString(), Toast.LENGTH_LONG);
            toast.show();
        }
        catch (MidiFileException e) {
            Toast toast = Toast.makeText(activity, "CheckFile midi: " + e.toString(), Toast.LENGTH_LONG);
            toast.show();
        } 
    }


    /** Play the sound for the given MIDI file */
    private void PlaySound(String filename) {
        if (player == null)
            return;
        try {
            FileInputStream input = activity.openFileInput(filename);
            player.reset();
            player.setDataSource(input.getFD());
            input.close();
            player.prepare();
            player.start();
        }
        catch (IOException e) {
            Toast toast = Toast.makeText(activity, "Error: Unable to play MIDI sound", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /** Stop playing the MIDI music */
    private void StopSound() {
        if (player == null)
            return;
        player.stop();
        player.reset();
    }


    /** The callback for the play button.
     *  If we're stopped or pause, then play the midi file.
     */
    private void Play() {
        if (midifile == null || sheet == null || numberTracks() == 0) {
            return;
        }
        else if (playstate == initStop || playstate == initPause || playstate == playing) {
            return;
        }
        // playstate is stopped or paused

        // Hide the midi player, wait a little for the view
        // to refresh, and then start playing
        timer.removeCallbacks(TimerCallback);
        timer.postDelayed(DoPlay, options.delay);
    }

    Runnable DoPlay = new Runnable() {
      public void run() {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /* The startPulseTime is the pulse time of the midi file when
         * we first start playing the music.  It's used during shading.
         */
        if (options.playMeasuresInLoop) {
            /* If we're playing measures in a loop, make sure the
             * currentPulseTime is somewhere inside the loop measures.
             */
            int measure = (int)(currentPulseTime / midifile.getTime().getMeasure());
            if ((measure < options.playMeasuresInLoopStart) ||
                (measure > options.playMeasuresInLoopEnd)) {
                currentPulseTime = options.playMeasuresInLoopStart * midifile.getTime().getMeasure();
            }
            startPulseTime = currentPulseTime;
            options.pauseTime = (int)(currentPulseTime - options.shifttime);
        }
        else if (playstate == paused) {
            startPulseTime = currentPulseTime;
            options.pauseTime = (int)(currentPulseTime - options.shifttime);
        }
        else {
            options.pauseTime = 0;
            startPulseTime = options.shifttime;
            currentPulseTime = options.shifttime;
            prevPulseTime = options.shifttime - midifile.getTime().getQuarter();
        }

        CreateMidiFile();
        playstate = playing;
        PlaySound(tempSoundFile);
        startTime = SystemClock.uptimeMillis();

        timer.removeCallbacks(TimerCallback);
        timer.removeCallbacks(ReShade);
        timer.postDelayed(TimerCallback, 100);

        sheet.ShadeNotes((int)currentPulseTime, (int)prevPulseTime, SheetMusic.GradualScroll);
        piano.ShadeNotes((int)currentPulseTime, (int)prevPulseTime);
        return;
      }
    };


    /** The callback for pausing playback.
     *  If we're currently playing, pause the music.
     *  The actual pause is done when the timer is invoked.
     */
    public void Pause() {
        this.setVisibility(View.VISIBLE);
        LinearLayout layout = (LinearLayout)this.getParent();
        layout.requestLayout();
        this.requestLayout();
        this.invalidate();

        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        if (midifile == null || sheet == null || numberTracks() == 0) {
            return;
        }
        else if (playstate == playing) {
            playstate = initPause;
            return;
        }
    }


    /** The callback for the Stop button.
     *  If playing, initiate a stop and wait for the timer to finish.
     *  Then do the actual stop.
     */
    void Stop() {
        this.setVisibility(View.VISIBLE);
        if (midifile == null || sheet == null || playstate == stopped) {
            return;
        }

        if (playstate == initPause || playstate == initStop || playstate == playing) {
            /* Wait for timer to finish */
            playstate = initStop;
            DoStop();
        }
        else if (playstate == paused) {
            DoStop();
        }
    }

    /** Perform the actual stop, by stopping the sound,
     *  removing any shading, and clearing the state.
     */
    void DoStop() { 
        playstate = stopped;
        timer.removeCallbacks(TimerCallback);
        sheet.ShadeNotes(-10, (int)prevPulseTime, SheetMusic.DontScroll);
        sheet.ShadeNotes(-10, (int)currentPulseTime, SheetMusic.DontScroll);
        piano.ShadeNotes(-10, (int)prevPulseTime);
        piano.ShadeNotes(-10, (int)currentPulseTime);
        startPulseTime = 0;
        currentPulseTime = 0;
        prevPulseTime = 0;
        setVisibility(View.VISIBLE);
        StopSound();
    }

    /** Rewind the midi music back one measure.
     *  The music must be in the paused state.
     *  When we resume in playPause, we start at the currentPulseTime.
     *  So to rewind, just decrease the currentPulseTime,
     *  and re-shade the sheet music.
     */
    void Rewind() {
        if (midifile == null || sheet == null || playstate != paused) {
            return;
        }

        /* Remove any highlighted notes */
        sheet.ShadeNotes(-10, (int)currentPulseTime, SheetMusic.DontScroll);
        piano.ShadeNotes(-10, (int)currentPulseTime);
   
        prevPulseTime = currentPulseTime; 
        currentPulseTime -= midifile.getTime().getMeasure();
        if (currentPulseTime < options.shifttime) {
            currentPulseTime = options.shifttime;
        }
        sheet.ShadeNotes((int)currentPulseTime, (int)prevPulseTime, SheetMusic.ImmediateScroll);
        piano.ShadeNotes((int)currentPulseTime, (int)prevPulseTime);
    }
    
    /** Fast forward the midi music by one measure.
     *  The music must be in the paused/stopped state.
     *  When we resume in playPause, we start at the currentPulseTime.
     *  So to fast forward, just increase the currentPulseTime,
     *  and re-shade the sheet music.
     */
    void FastForward() {
        if (midifile == null || sheet == null) {
            return;
        }
        if (playstate != paused && playstate != stopped) {
            return;
        }
        playstate = paused;

        /* Remove any highlighted notes */
        sheet.ShadeNotes(-10, (int)currentPulseTime, SheetMusic.DontScroll);
        piano.ShadeNotes(-10, (int)currentPulseTime);
   
        prevPulseTime = currentPulseTime; 
        currentPulseTime += midifile.getTime().getMeasure();
        if (currentPulseTime > midifile.getTotalPulses()) {
            currentPulseTime -= midifile.getTime().getMeasure();
        }
        sheet.ShadeNotes((int)currentPulseTime, (int)prevPulseTime, SheetMusic.ImmediateScroll);
        piano.ShadeNotes((int)currentPulseTime, (int)prevPulseTime);
    }
    
    /** Set the speed bar back to 100% */
    void backTo100() {
    	speedBar.setProgress(100-30 /* added later */);
    }
    
    
    /** Plus 1 in the speed bar */
    void plus() {
    	speedBar.setProgress(speedBar.getProgress() + 1);    	
     }
    
    /** Minus 1 in the speed bar */
    void minus() {
    	speedBar.setProgress(speedBar.getProgress() - 1);  
    }
    
    /** Mutes the player and saves the current volume */
    public void mute() {
    	mute = true;
    	muteButton.setImageBitmap(muteOnImage);
    	if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
    		volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    	}
    	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }
    
    /** Unmutes the player and set the last volume saved back */
    public void unmute() {
    	mute = false ;
    	muteButton.setImageBitmap(muteOffImage);
    	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }


    /** Move the current position to the location clicked.
     *  The music must be in the paused/stopped state.
     *  When we resume in playPause, we start at the currentPulseTime.
     *  So, set the currentPulseTime to the position clicked.
     */
    public void MoveToClicked(int x, int y) {
        if (midifile == null || sheet == null) {
            return;
        }
        if (playstate != paused && playstate != stopped) {
            return;
        }
        playstate = paused;

        /* Remove any highlighted notes */
        sheet.ShadeNotes(-10, (int)currentPulseTime, SheetMusic.DontScroll);
        piano.ShadeNotes(-10, (int)currentPulseTime);

        currentPulseTime = sheet.PulseTimeForPoint(new Point(x, y));
        prevPulseTime = currentPulseTime - midifile.getTime().getMeasure();
        if (currentPulseTime > midifile.getTotalPulses()) {
            currentPulseTime -= midifile.getTime().getMeasure();
        }
        sheet.ShadeNotes((int)currentPulseTime, (int)prevPulseTime, SheetMusic.DontScroll);
        piano.ShadeNotes((int)currentPulseTime, (int)prevPulseTime);
    }


    /** The callback for the timer. If the midi is still playing, 
     *  update the currentPulseTime and shade the sheet music.  
     *  If a stop or pause has been initiated (by someone clicking
     *  the stop or pause button), then stop the timer.
     */
    Runnable TimerCallback = new Runnable() {
      public void run() {
        if (midifile == null || sheet == null) {
            playstate = stopped;
            return;
        }
        else if (playstate == stopped || playstate == paused) {
            /* This case should never happen */
            return;
        }
        else if (playstate == initStop) {
            return;
        }
        else if (playstate == playing) {
            long msec = SystemClock.uptimeMillis() - startTime;
            prevPulseTime = currentPulseTime;
            currentPulseTime = startPulseTime + msec * pulsesPerMsec;

            /* If we're playing in a loop, stop and restart */
            if (options.playMeasuresInLoop) {
                double nearEndTime = currentPulseTime + pulsesPerMsec*10;
                int measure = (int)(nearEndTime / midifile.getTime().getMeasure());
                if (measure > options.playMeasuresInLoopEnd) {
                    RestartPlayMeasuresInLoop();
                    return;
                }
            }

            /* Stop if we've reached the end of the song */
            if (currentPulseTime > midifile.getTotalPulses()) {
                DoStop();
                return;
            }
            sheet.ShadeNotes((int)currentPulseTime, (int)prevPulseTime, SheetMusic.GradualScroll);
            piano.ShadeNotes((int)currentPulseTime, (int)prevPulseTime);
            timer.postDelayed(TimerCallback, 100);
            return;
        }
        else if (playstate == initPause) {
            long msec = SystemClock.uptimeMillis() - startTime;
            StopSound();

            prevPulseTime = currentPulseTime;
            currentPulseTime = startPulseTime + msec * pulsesPerMsec;
            sheet.ShadeNotes((int)currentPulseTime, (int)prevPulseTime, SheetMusic.ImmediateScroll);
            piano.ShadeNotes((int)currentPulseTime, (int)prevPulseTime);
            playstate = paused;
            timer.postDelayed(ReShade, 1000);
            return;
        }
      }
    };


    /** The "Play Measures in a Loop" feature is enabled, and we've reached
     *  the last measure. Stop the sound, unshade the music, and then
     *  start playing again.
     */
    private void RestartPlayMeasuresInLoop() {
        playstate = stopped;
        piano.ShadeNotes(-10, (int)prevPulseTime);
        sheet.ShadeNotes(-10, (int)prevPulseTime, SheetMusic.DontScroll);
        currentPulseTime = 0;
        prevPulseTime = -1;
        StopSound();
        timer.postDelayed(DoPlay, 300);
    }
    
}


