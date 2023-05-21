package com.bounoua.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.ArrayList;

public class SingleMusic extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Thread seekThread;
    private ImageButton previousBtn, playStopBtn, nextBtn;
    private int position;
    ArrayList songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_music);
        seekBar = findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();
        previousBtn = findViewById(R.id.previousBtn);
        playStopBtn = findViewById(R.id.stopPlayBtn);
        nextBtn = findViewById(R.id.nextBtn);

        Intent intent = getIntent();
        if (intent != null) {
            songList = intent.getStringArrayListExtra("allPathes");
            position = intent.getIntExtra("position",0);
            playSong((String) songList.get(position));
        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(songList.size() - 1 > position) {
                    position++;
                }else {
                    position = 0;
                }
                if (seekThread != null) {
                    seekThread.interrupt();
                    seekThread = null;
                }
                playSong((String) songList.get(position));
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position > 0) {
                    position--;
                }else {
                    position = songList.size() - 1;
                }
                if (seekThread != null) {
                    seekThread.interrupt();
                    seekThread = null;
                }
                playSong((String) songList.get(position));
            }
        });

        playStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playStopBtn.setImageResource(R.drawable.play_music);
                }else {
                    if (seekThread != null) {
                        seekThread.interrupt();
                        seekThread = null;
                    }
                    seekThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (mediaPlayer != null) {
                                try {
                                    runOnUiThread(() -> seekBar.setProgress(mediaPlayer.getCurrentPosition()));
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    mediaPlayer.start();
                    seekThread.start();
                    playStopBtn.setImageResource(R.drawable.stop_music);
                }

            }
        });

    }

    private void playSong(String songPath) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(songPath);
            mediaPlayer.prepare();
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setProgress(0);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        mediaPlayer.seekTo(i);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            if (seekThread != null) {
                seekThread.interrupt();
                seekThread = null;
            }
            seekThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mediaPlayer != null) {
                        try {
                            if(mediaPlayer != null)
                            runOnUiThread(() -> seekBar.setProgress(mediaPlayer.getCurrentPosition()));
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            mediaPlayer.start();
            seekThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}