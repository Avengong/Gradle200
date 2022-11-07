package com.ztsdk.lib.gradletwo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


import com.zygote.lib.insight.api.Insight;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        name:onCreate , descriptor:(Landroid/os/Bundle;)V, signature:null
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ThreadUtils.test();
//        ThreadUtils.printName(this.getClass().getName());

//        Log.d("ThreadUtils", "debug--");
        Insight.getInstance().init(Insight.newConfig().setApplication(getApplication()));

    }
}