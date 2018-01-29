package com.example.dai.wificlient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private TextView editText_1;
    private EditText editText_ip,editText_data;
    private OutputStream outputStream = null;
    private Socket socket = null;
    private String ip;
    private String data;
    private boolean socketStatus = false;
    StringBuffer stringBuffer = new StringBuffer();

    private String textToShow = "待支付金额：";

    public Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 1:
                    textToShow+="\n";
                    textToShow+=msg.obj.toString();
                    editText_1.setText(textToShow);
                    break;
                case 2:
                    editText_1.setText(msg.obj.toString());
                    stringBuffer.setLength(0);
                    break;

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText_ip = (EditText) findViewById(R.id.et_ip);
        editText_data = (EditText) findViewById(R.id.et_data);
        editText_1 = (TextView) findViewById(R.id.text1);

    }


    public void connect(View view){

        ip = editText_ip.getText().toString();
        if(ip == null){
            Toast.makeText(MainActivity.this,"please input Server IP",Toast.LENGTH_SHORT).show();
        }

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();

                if (!socketStatus) {

                    try {
                        socket = new Socket(ip,53553);
                        if(socket == null){
                        }else {
                            socketStatus = true;
                        }
                        outputStream = socket.getOutputStream();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this,"socket connect failed",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }

            }
        };
        thread.start();

    }


    public void send(View view){

        data = editText_data.getText().toString();
        if(data == null){
            Toast.makeText(MainActivity.this,"please input Sending Data",Toast.LENGTH_SHORT).show();
        }else {
            //在后面加上 '\0' ,是为了在服务端方便我们去解析；
            data = data + '\0';
        }

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                if(socketStatus){
                    try {
                        outputStream.write(data.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //接受返回数据
                int len;
                byte[] bytes = new byte[20];
                InputStream inputStream;
                StringBuffer stringBuffer = new StringBuffer();

                try{
                    inputStream = socket.getInputStream();
                    while ((len = inputStream.read(bytes)) != -1) {
                        for(int i=0; i<len; i++){
                            stringBuffer.append((char)bytes[i]);
                        }
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        message.obj = stringBuffer;
                        handler.sendMessage(message);
                        break;
                    }

                } catch (Exception e){ }
            }
        };
        thread.start();
    }

    public void pay(View view){
        final String tempdata = "pay ok!\0";
        Thread thread = new Thread(){
            @Override
            public void run(){
                super.run();
                if (socketStatus){
                    try{
                        outputStream.write(tempdata.getBytes());
                    } catch (Exception e){}
                }
                //接受返回数据
                int len;
                byte[] bytes = new byte[20];
                InputStream inputStream;
                StringBuffer stringBuffer = new StringBuffer();

                try{
                    inputStream = socket.getInputStream();
                    while ((len = inputStream.read(bytes)) != -1) {
                        for(int i=0; i<len; i++){
                            stringBuffer.append((char)bytes[i]);
                        }
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        message.obj = stringBuffer;
                        handler.sendMessage(message);
                        break;
                    }

                } catch (Exception e){ }
            }
        };
        thread.start();
    }

    public void payException(View view){
        final String tempdata = "0 pay with exception!\0";
        Thread thread = new Thread(){
            @Override
            public void run(){
                super.run();
                if (socketStatus){
                    try{
                        outputStream.write(tempdata.getBytes());
                    } catch (Exception e){}
                }
                //接受返回数据
                int len;
                byte[] bytes = new byte[20];
                InputStream inputStream;
                StringBuffer stringBuffer = new StringBuffer();

                try{
                    inputStream = socket.getInputStream();
                    while ((len = inputStream.read(bytes)) != -1) {
                        for(int i=0; i<len; i++){
                            stringBuffer.append((char)bytes[i]);
                        }
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        message.obj = stringBuffer;
                        handler.sendMessage(message);
                        break;
                    }

                } catch (Exception e){ }
            }
        };
        thread.start();
    }

    /*当客户端界面返回时，关闭相应的socket资源*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*关闭相应的资源*/
        try {
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
