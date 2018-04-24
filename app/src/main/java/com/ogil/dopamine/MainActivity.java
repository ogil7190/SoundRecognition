package com.ogil.dopamine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_CODE = 7190;
    public static final int MAX_RESULT = 3;
    private SpeechRecognizer recognizer;
    private Button button;
    private TextView text, indicator;
    private MediaPlayer player;
    private AssetFileDescriptor afd;
    private int count = 0;
    private boolean start = false;
    public static final String[] words = {"age","air","low","six","boot","born","draw","free","test","nice","nose","wait","cough","metro","order","small","trade","little","island"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            requestPermission();
        }
        player = new MediaPlayer();
        recognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        recognizer.setRecognitionListener(new listener());
        button = findViewById(R.id.speak);
        text = findViewById(R.id.text);
        indicator = findViewById(R.id.indicator);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!start) {
                    start = true;
                    button.setText("SPEAK");
                    speak();
                }
                else{
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getCallingPackage());
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_RESULT);
                    recognizer.startListening(intent);
                }
            }
        });
    }

    private int REPEAT_COUNT = 3;
    private void speak(){
        REPEAT_COUNT = 3; //reset
        text.setText(words[count].toUpperCase());
        indicator.setText("Please Listen");
        button.setText("...");
        button.setBackground(getDrawable(R.drawable.back_button_dis));
        button.setEnabled(false);
        player.reset();
        afd = getApplicationContext().getResources().openRawResourceFd(getResId(words[count], R.raw.class));
        try {
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d("App","Completed!");
                    REPEAT_COUNT--;
                    if(REPEAT_COUNT>0){
                        player.start();
                    }
                    else {
                        count++;
                        if(count == words.length){
                            count = 0;
                            button.setText("START");
                        }
                        button.setText("SPEAK");
                        indicator.setText("Speak Now");
                        button.setBackground(getDrawable(R.drawable.back_button));
                        button.setEnabled(true);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    requestPermission();
                }
                return;
            }
        }
    }

    class listener implements RecognitionListener{
        private int flag = 0;
        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d("App","Ready");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d("App","Begin");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.d("App","Changed");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.d("App","BufferFound");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d("App","End");
        }

        @Override
        public void onError(int error) {
            Log.d("App","Error");
            Toasty.warning(getApplicationContext(),"Try Again!").show();
        }

        @Override
        public void onResults(Bundle results) {
            flag = 0;
            Log.d("App","Results");
            String match;
            try {
                match = words[count - 1];
            } catch(Exception e){
                match = words[words.length-1];
            }

            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (int i = 0; i < data.size(); i++) {
                String res = ((String) data.get(i)).toLowerCase();
                Log.d("App","Result:"+res);
                if (match.equals(res)) {
                    flag = 1;
                    Toasty.success(getApplicationContext(), "Very Good").show();
                }
            }
            if(flag == 0){
                Toasty.error(getApplicationContext(),"Try Again!").show();
            } else{
                button.setText("NEXT");
                start = false;
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d("App","Partial");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.d("App","Event");
        }
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (player != null) {
                player.setVolume(0, 0);
                player.release();
            }
        } catch (Exception e){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (player != null) {
                player.setVolume(0, 0);
                player.release();
            }
        } catch (Exception e){

        }
    }
}
