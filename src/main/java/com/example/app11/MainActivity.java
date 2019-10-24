package com.example.app11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
private Button btn_skip;
private TextView skip_time;
private Timer timer;
private  int i=3;
private   Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();


    }
    public void initView(){
        btn_skip =findViewById(R.id.btn_skip_start);
        skip_time = findViewById(R.id.skip_time);
        timer =new Timer();

        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toHome();
            }
        });
        handler =new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what==1){

                    skip_time.setText(i+"");
                    if (i==0){

                        toHome();
                    }
                }
                return false;
            }
        });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message =new Message();
                message.what=1;
                message.obj=i;
                handler.sendMessage(message);
                i--;
                Log.i("sasa",i+"");
            }
        },0,1000);


    }

    public void toHome(){
        timer.cancel();
        Intent intent =new Intent(MainActivity.this,Main2Activity.class);
        startActivity(intent);
        finish();
    }
}
