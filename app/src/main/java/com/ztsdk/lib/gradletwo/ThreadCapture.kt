package com.ztsdk.lib.gradletwo

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi

class ThreadCapture {

    companion object {
        val handler = Handler(Looper.getMainLooper())
        var runnable = object : Runnable {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun run() {
                // 通过这个获取的是主进程的线程
                val allStackTraces = Thread.getAllStackTraces()
                Log.d("booster", "thread capture run！threadsSize :${allStackTraces.size}");
                allStackTraces.forEach { thread, stack ->
                    Log.d("booster", "线程：" + thread.getName() + ",id=" + thread.getId() + ",state=" + thread.getState());

                }

//                Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
//                Log.d(TAG, "线程总数：" + allStackTraces.size());
//                for (Map.Entry<Thread, StackTraceElement[]> stackTrace : allStackTraces.entrySet()) {
//                    Thread thread = (Thread) stackTrace.getKey();
//                    Log.d(TAG, "线程：" + thread.getName() + ",id=" + thread.getId() + ",state=" + thread.getState());
//                    StackTraceElement[] stack = (StackTraceElement[]) stackTrace.getValue();
//                    String strStackTrace = "堆栈：";
//                    for (StackTraceElement stackTraceElement : stack) {
//                    strStackTrace += stackTraceElement.toString() + "\n";
//                }
//                    Log.d(TAG, strStackTrace);
//                }

//                Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
//                Log.d(TAG, "线程总数：" + allStackTraces.size());
//                for (Map.Entry<Thread, StackTraceElement[]> stackTrace : allStackTraces.entrySet()) {
//                    Thread thread = (Thread) stackTrace.getKey();
//                    Log.d(TAG, "线程：" + thread.getName() + ",id=" + thread.getId() + ",state=" + thread.getState());
//                    StackTraceElement[] stack = (StackTraceElement[]) stackTrace.getValue();
//                    String strStackTrace = "堆栈：";
//                    for (StackTraceElement stackTraceElement : stack) {
//                    strStackTrace += stackTraceElement.toString() + "\n";
//                }
//                    Log.d(TAG, strStackTrace);
//                }

                handler.postDelayed(this, 20 * 1000)
            }
        }

        fun collectThreads() {

//            Log.d("booster", " collectThreads run！");
            handler.postDelayed(runnable, 0)

        }

    }
}