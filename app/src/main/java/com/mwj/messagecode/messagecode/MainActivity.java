package com.mwj.messagecode.messagecode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private MessageCodeLayout msgcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msgcode = findViewById(R.id.msgcode);

        //监听事件
        msgcode.setMessageCodeChangeListener(new MessageCodeLayout.MessageCodeChangeListener() {
            @Override
            public void onChange(String pwd) {//改变

            }

            @Override
            public void onNull() {//null

            }

            @Override
            public void onFinished(String code) {//完成

            }
        });

    }

    public void onAddClick(View view) {
        msgcode.addCodes("123456");
    }

    public void onDeleteClick(View view) {
        msgcode.removeAllCode();
    }

}
