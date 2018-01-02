package com.example.vertika.smartvib;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.util.Log;

public class Sender extends AppCompatActivity {

    static int[] DecToBinary(int x){
        int i = 6;
        int[] answer = new int[7];

        while(x > 0){
            answer[i] = x%2;
            x = x/2;
            i--;
        }
        return answer;
        //MSB is answer[0], LSB is answer[6]
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);

        // Keep the screen on forever.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button buttonSend = (Button) findViewById(R.id.buttonSend);
        Button buttonBack = (Button)findViewById(R.id.buttonBack);

        final EditText textToSend = (EditText) findViewById(R.id.textToSend);
        final TextView  stringSent = (TextView)findViewById(R.id.stringSent);

        buttonBack.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        Intent intent;
                        intent = new Intent(getApplicationContext(), ActionSelect.class);
                        startActivity(intent);
                    }
                }
        );

        buttonSend.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        String content = textToSend.getText().toString();
                        int[] bitPattern = new int[content.length() * 11]; //the superframe bit pattern
                        long[] vibrationPattern = new long[(bitPattern.length * 4) + 1];
                        int[] answer = new int[7];
                        for(int i = 0; i < content.length(); i++) {
                            char a = content.charAt(i);
                            int x = (int) a;
                            answer = DecToBinary(x);

                            bitPattern[i * 11 + 0] = 1;
                            bitPattern[i * 11 + 1] = answer[0];
                            bitPattern[i * 11 + 2] = answer[1];
                            bitPattern[i * 11 + 3] = answer[2];
                            bitPattern[i * 11 + 4] = answer[3];
                            bitPattern[i * 11 + 5] = answer[4];
                            bitPattern[i * 11 + 6] = answer[5];
                            bitPattern[i * 11 + 7] = answer[6];
                            bitPattern[i * 11 + 8] = 0;
                            bitPattern[i * 11 + 9] = 0;
                            bitPattern[i * 11 + 10] = 0;
                            /*Log.i("debug print :::: ","word = "+ i + " :: " + bitPattern[i*11 + 0] + " " + bitPattern[i*11+1] + " " + bitPattern[2] + " " +
                                    bitPattern[i*11+3] + " " +
                                    bitPattern[i*11+4] + " " +
                                    bitPattern[i*11+5] + " " +
                                    bitPattern[i*11+6] + " " +
                                    bitPattern[i*11+7] + " " +
                                    bitPattern[i*11+8] + " " +
                                    bitPattern[i*11+9] + " " +
                                    bitPattern[i*11+10] );*/

                        }
                        int l = 0;
                        vibrationPattern[0] = 0;
                        for(int k = 0; k < bitPattern.length ; k++){
                            if(bitPattern[k] == 1){
                                vibrationPattern[++l] = 0;
                                vibrationPattern[++l] = 300;
                                vibrationPattern[++l] = 400;
                                vibrationPattern[++l] = 300;
                            } else{
                                vibrationPattern[++l] = 0;
                                vibrationPattern[++l] = 500;
                                vibrationPattern[++l] = 0;
                                vibrationPattern[++l] = 500;
                            }
                            /*Log.i("debug print :::: ","bit = "+ bitPattern[k] + " :: " +
                                    vibrationPattern[k*4+1] + " " +
                                    vibrationPattern[k*4+2] + " " +
                                    vibrationPattern[k*4+3] + " " +
                                    vibrationPattern[k*4+4]);*/
                        }
                       //VIBRATOR
                        Vibrator vib = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
                        vib.vibrate(vibrationPattern, -1);
                        stringSent.setText(Integer.toString(content.length()) + " length string sent");
                    }
                }
        );
    }
}
