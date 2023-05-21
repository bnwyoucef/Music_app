package com.bounoua.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Environment;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ListView listView;
    private List<String> songList = new ArrayList<>();
    private ArrayList displaylist = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        listView = findViewById(R.id.listView);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            loadSongs();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MusicActivity.this,SingleMusic.class);
                intent.putStringArrayListExtra("allPathes", (ArrayList<String>) songList);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

    }

    private void loadSongs() {
        File musicDirectory = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath());
        File[] files = musicDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".mp3")) {
                    songList.add(file.getAbsolutePath());
                }
            }
        }
        for (int i =0; i< songList.size(); i++) {
            displaylist.add(songList.get(i).subSequence(66,songList.get(i).length() - 1));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displaylist);
        listView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            }
        }
    }
}