package com.ztsdk.lib.gradletwo

import android.os.Process
import android.util.Log
import java.io.File
import java.io.RandomAccessFile

class AdbUtils {

    companion object {

        const val TAG = "AdbUtils"
        var pid = -1
        private fun getAppPId(): Int {
            if (pid == -1) {
                pid = Process.myPid()
            }
            return pid
        }

        fun getFileCount(dir: File?): Int {
            return if (dir != null && dir.exists()) {
                if (!dir.isDirectory) {
                    1
                } else {
                    val children = dir.listFiles()
                    var count = 0
                    val size = children?.size
                    if (size != null) {
                        for (i in 0 until size) {
                            val child = children[i]
                            count += getFileCount(child)
                        }
                    }
                    count
                }
            } else {
                0
            }
        }


        fun getThreads() {

//            var process = Runtime.getRuntime().exec("/proc/${getAppPId()}/task")

//
            val file = File("/proc/${getAppPId()}/task")
            Log.d(TAG, "getThreads: file : $file ")
            if (file.exists()) {
                val listFiles = file.listFiles()

                Log.d(TAG, "getThreads: fileCount : ${listFiles?.size} ")


                listFiles?.forEach {

                    Log.d(TAG, "getThreads: file : ${it.name} ")
                    val procTaskFile = RandomAccessFile("/proc/${getAppPId()}/task${it.name}/stat", "r")

                    val readLine = procTaskFile.readLine()
                    Log.d(TAG, "getThreads: readLine : ${readLine} ")
                }
            }


//            /proc/%d/task/%s/stat
            // 如果是文件夹不能这么读取。
////            val procThreads = procTaskFile.readLine()
//            Log.d(TAG, "getThreads: procThreads : $process ")

        }

        fun getAppCpuRate(): Double {
            val start = System.currentTimeMillis()
            var cpuTime = 0L
            var appTime = 0L
            var cpuRate = 0.0
            var procStatFile: RandomAccessFile? = null
            var appStatFile: RandomAccessFile? = null
            try {
                procStatFile = RandomAccessFile("/proc/stat", "r")
                val procStatString = procStatFile.readLine()
                val procStats = procStatString.split(" ".toRegex()).toTypedArray()
                cpuTime =
                    procStats[2].toLong() + procStats[3].toLong() + procStats[4].toLong() + procStats[5].toLong() + procStats[6].toLong() + procStats[7].toLong() + procStats[8].toLong()
            } catch (e: Exception) {
//                MatrixLog.i(com.tencent.matrix.util.DeviceUtil.TAG, "RandomAccessFile(Process Stat) reader fail, error: %s", e.toString())
            } finally {
                try {
                    procStatFile?.close()
                } catch (e: Exception) {
//                    MatrixLog.i(com.tencent.matrix.util.DeviceUtil.TAG, "close process reader %s", e.toString())
                }
            }
            try {
                appStatFile = RandomAccessFile("/proc/" + getAppPId() + "/stat", "r")
                val appStatString = appStatFile.readLine()
                val appStats = appStatString.split(" ".toRegex()).toTypedArray()
                appTime = appStats[13].toLong() + appStats[14].toLong()
            } catch (e: Exception) {
//                MatrixLog.i(com.tencent.matrix.util.DeviceUtil.TAG, "RandomAccessFile(App Stat) reader fail, error: %s", e.toString())
            } finally {
                try {
                    appStatFile?.close()
                } catch (e: Exception) {
//                    MatrixLog.i(com.tencent.matrix.util.DeviceUtil.TAG, "close app reader %s", e.toString())
                }
            }
            if (0L != cpuTime) {
                cpuRate = appTime.toDouble() / cpuTime.toDouble() * 100.0
            }
//            MatrixLog.i(com.tencent.matrix.util.DeviceUtil.TAG, "getAppCpuRate cost:" + (System.currentTimeMillis() - start) + ",rate:" + cpuRate)
            return cpuRate
        }

    }

}