package com.twirling.sdk.sdk_test;

import androidx.appcompat.app.AppCompatActivity;
import com.twirling.sdk.wakeup;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    private Button testButton;
    private TextView textView;

    private wakeup wakeup = new wakeup();

    private int FRAMELEN = 160; // 10ms * 16000 / 1000

    private long wakeup_obj;

    private float MAX_SHORT = 32768.f;

    int wakeup_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textview);
        testButton = (Button) findViewById(R.id.button);

        wakeup_obj = wakeup.WakeupInit(FRAMELEN, "/sdcard/res/2020072016425225.dat",
                "/sdcard/res/model.bin","c8c29e4a4ffbf080cd41f6712c8aefb9","twirling");


        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        readAudioFile();


                    }
                }).start();
            }
        });



    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        wakeup.WakeupRelease(wakeup_obj);
    }



    private void show(String str)
    {
        textView.setText(str);
    }

    private void readAudioFile()
    {
        FileInputStream inputStream = null;
        File file = new File("/sdcard/audio/test1.raw");

        try{
            inputStream = new FileInputStream(file);
             byte buffer[] = new byte[FRAMELEN*2];
            short number[] = new short[FRAMELEN];
            float buf[] = new float[FRAMELEN];

            int len = 0;
            while(true)
            {
                len = inputStream.read(buffer,0,buffer.length);
                if (len <= 0)
                    break;

                ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(number);

                for(int i=0;i<FRAMELEN;i++)
                {
                    buf[i] = number[i] / MAX_SHORT;
                }
                int ret = wakeup.WakeupProcess(wakeup_obj,buf);

                if (ret > 0)
                {
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }

                try {
                    Thread.sleep(10);
                }catch(InterruptedException e)
                {}
            }
        }catch(IOException e)
        {
            e.printStackTrace();
            show("read error");
        } finally {
            if (inputStream != null)
            {
                try{
                    inputStream.close();
                }catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    wakeup_time ++;
                    show("wakeup " + String.valueOf(wakeup_time) );
                    break;
                default:
                    break;
            }
        }
    };
}
