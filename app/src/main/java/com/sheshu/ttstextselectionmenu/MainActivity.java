package com.sheshu.ttstextselectionmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    CharSequence textToSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textToSpeak = getIntent()
                .getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        Intent intent = new Intent(this, TTSBackgroundService.class);
        if (!TextUtils.isEmpty(textToSpeak)) {
            Bundle data = new Bundle();
            intent.setAction(TTSBackgroundService.ACTION_SPEAK);
            data.putString(TTSBackgroundService.COMMAND, TTSBackgroundService.ACTION_SPEAK);
            data.putString(TTSBackgroundService.DATA_TEXT, textToSpeak.toString());
            intent.putExtras(data);
        }
        startService(intent);
        finish();
    }
}
