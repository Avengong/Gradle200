package com.ztsdk.lib.gradletwo

import android.util.Log

class ThreadUtils {
    companion object {
        const val TAG = "ThreadUtils"

        @kotlin.jvm.JvmStatic
        fun test() {
            for (i in 0..10) {
                Thread {
                    Log.d(TAG, "test: go!  name: ${Thread.currentThread().name}")
                }.start()
            }
        }
    }


}