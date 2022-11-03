package com.ztsdk.lib.gradletwo

import android.util.Log

public class ThreadUtils {
    companion object {
        const val TAG = "ThreadUtils"

        @kotlin.jvm.JvmStatic
        fun test() {
            Log.d(TAG, "ThreadUtils-test go : name")
//            for (i in 0..10) {
//                Thread {
//                    Log.d(TAG, "test: go!  name: ${Thread.currentThread().name}")
//                }.start()
//            }
        }

        @JvmStatic
        fun printName(MainActivity: String) {
            Log.d(TAG, "ThreadUtils-printName go ")
        }
    }


}