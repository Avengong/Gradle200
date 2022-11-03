package com.ztsdk.lib.gradletwo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        name:onCreate , descriptor:(Landroid/os/Bundle;)V, signature:null
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ThreadUtils.test();
        ThreadUtils.printName("MainActivity");
    }
}