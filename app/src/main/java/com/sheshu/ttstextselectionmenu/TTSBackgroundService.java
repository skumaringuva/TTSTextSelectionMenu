package com.sheshu.ttstextselectionmenu;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Sheshu on 4/14/17.
 */

public class TTSBackgroundService extends Service implements TextToSpeech.OnInitListener {

    static final String ACTION_SPEAK = "com.sheshu.ttstextselectionmenu.speak";
    static final String DATA_TEXT = "com.sheshu.ttstextselectionmenu.data.text";
    static final String ACTION_STOP = "com.sheshu.ttstextselectionmenu.stop";
    static final String COMMAND = "com.sheshu.ttstextselectionmenu.command";
    private static final String TAG = "TTSService";
    private String str;
    private TextToSpeech mTts;
    private ClipboardManager mClipboard;
    private ClipboardManager.OnPrimaryClipChangedListener mClipBoardListener;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        mTts = new TextToSpeech(this,
                this  // OnInitListener
        );
        mTts.setSpeechRate(0.5f);
        setClipboardListener();
        Log.v(TAG, "oncreate_service");
        // str ="turn left please ";
        super.onCreate();
    }

    private void setClipboardListener() {
        mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        mClipBoardListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                if (mClipboard.hasPrimaryClip()) {
                    String contents = mClipboard.getPrimaryClip().getItemAt(0).getText().toString();
                    //ClipData newCopy = ClipData.newPlainText("copiedText", TextUtils.noCharWrap(contents));
                    Log.v(TAG,"TEXT: "+contents);
                    if(!TextUtils.isEmpty(contents))
                        SpeakText(contents);
                }
            }
        };

        mClipboard.addPrimaryClipChangedListener(mClipBoardListener);


    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        if(mClipboard!=null && mClipBoardListener!=null)
            mClipboard.removePrimaryClipChangedListener(mClipBoardListener);
        mClipboard = null;
        mClipBoardListener = null;
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //   SpeakText(str);
        Log.v(TAG, "onstart_service");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null && intent.getAction().equals(ACTION_SPEAK)) {
            String text = intent.getExtras().getString(DATA_TEXT);
            Log.v(TAG, "text to speak: " + text);
            str = text;
            SpeakText(text);
        }
        Log.v(TAG, "onStartCommand");
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onInit(int status) {
        Log.v(TAG, "oninit");
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.v(TAG, "Please download the tts language from settings.");
            } else {
                SpeakText(str);
            }
        } else {
            Log.v(TAG, "Could not initialize TextToSpeech.");
        }
    }

    private void SpeakText(String str) {
        mTts.speak(str,
                TextToSpeech.QUEUE_FLUSH,
                null);
    }
}