Thread.getAllStackTraces()打印的线程包含了：

1. 自己源码
2. 第三方lib
3. 系统创建的线程

2022-11-09 14:06:12.223 4179-4179/com.ztsdk.lib.gradletwo D/booster: thread capture run！threadsSize :33 2022-11-09 14:06:12.224
4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：​com.android.volley.NetworkDispatcher,id=18903,state=WAITING 2022-11-09 14:06:12.224
4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：​com.android.volley.NetworkDispatcher,id=18905,state=WAITING 2022-11-09 14:06:12.224
4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：​com.alibaba.android.arouter.thread.DefaultThreadFactory#ARouter task pool No.1, thread
No.2,id=18911,state=WAITING 2022-11-09 14:06:12.225 4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：RenderThread,id=18916,state=RUNNABLE 2022-11-09
14:06:12.225 4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：​com.android.volley.NetworkDispatcher,id=18904,state=WAITING 2022-11-09 14:06:12.225
4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：​com.android.volley.NetworkDispatcher,id=18900,state=WAITING 2022-11-09 14:06:12.225
4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：​com.android.volley.NetworkDispatcher,id=18901,state=WAITING 2022-11-09 14:06:12.226
4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：Binder:4179_3,id=18893,state=RUNNABLE 2022-11-09 14:06:12.226 4179-4179/com.ztsdk.lib.gradletwo
D/booster: 线程：​com.alibaba.android.arouter.thread.DefaultThreadFactory#ARouter task pool No.1, thread No.1,id=18908,state=WAITING 2022-11-09 14:06:
12.226 4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：Jit thread pool worker thread 0,id=18884,state=RUNNABLE 2022-11-09 14:06:12.227
4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：queued-work-looper,id=18910,state=RUNNABLE 2022-11-09 14:06:12.227 4179-4179/com.ztsdk.lib.gradletwo
D/booster: 线程：​com.tcloud.core.service.ServiceCenter#ServiceThread,id=18912,state=RUNNABLE 2022-11-09 14:06:12.227 4179-4179/com.ztsdk.lib.gradletwo
D/booster: 线程：FinalizerDaemon,id=18889,state=WAITING 2022-11-09 14:06:12.227 4179-4179/com.ztsdk.lib.gradletwo D/booster:
线程：​com.android.volley.NetworkDispatcher,id=18907,state=WAITING 2022-11-09 14:06:12.228 4179-4179/com.ztsdk.lib.gradletwo D/booster:
线程：​com.tcloud.core.connect.TransmitService#TransmitThread,id=18914,state=RUNNABLE 2022-11-09 14:06:12.228 4179-4179/com.ztsdk.lib.gradletwo
D/booster: 线程：ReferenceQueueDaemon,id=18888,state=WAITING 2022-11-09 14:06:12.228 4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：Binder:
4179_2,id=18892,state=RUNNABLE 2022-11-09 14:06:12.229 4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：Profile Saver,id=18894,state=RUNNABLE
2022-11-09 14:06:12.229 4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：​com.android.volley.NetworkDispatcher,id=18906,state=WAITING 2022-11-09 14:06:
12.229 4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：ConnectivityThread,id=18913,state=RUNNABLE 2022-11-09 14:06:12.229
4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：​com.tcloud.core.log.LogProxy#LogThread,id=18896,state=RUNNABLE 2022-11-09 14:06:12.230
4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：​com.tcloud.core.connect.Sender#Sender,id=18915,state=WAITING 2022-11-09 14:06:12.230
4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：Binder:4179_1,id=18891,state=RUNNABLE 2022-11-09 14:06:12.230 4179-4179/com.ztsdk.lib.gradletwo
D/booster: 线程：HeapTaskDaemon,id=18887,state=WAITING 2022-11-09 14:06:12.231 4179-4179/com.ztsdk.lib.gradletwo D/booster:
线程：​com.zygote.lib.insight.util.ThreadPool#insight,id=18918,state=RUNNABLE 2022-11-09 14:06:12.231 4179-4179/com.ztsdk.lib.gradletwo D/booster:
线程：​com.tcloud.core.http.HttpClient#HttpFunction,id=18898,state=RUNNABLE 2022-11-09 14:06:12.231 4179-4179/com.ztsdk.lib.gradletwo D/booster:
线程：FinalizerWatchdogDaemon,id=18890,state=WAITING 2022-11-09 14:06:12.231 4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：main,id=2,state=RUNNABLE
2022-11-09 14:06:12.232 4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：​com.android.volley.NetworkDispatcher,id=18902,state=WAITING 2022-11-09 14:06:
12.232 4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：​com.android.volley.CacheDispatcher,id=18899,state=WAITING 2022-11-09 14:06:12.232
4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：​com.tcloud.core.thread.pool.Pools$DefaultThreadFactory#self-executor1-thread-2,id=18897,state=WAITING
2022-11-09 14:06:12.232 4179-4179/com.ztsdk.lib.gradletwo D/booster:
线程：​com.tcloud.core.thread.pool.Pools$DefaultThreadFactory#self-executor1-thread-1,id=18895,state=WAITING 2022-11-09 14:06:12.233
4179-4179/com.ztsdk.lib.gradletwo D/booster: 线程：Signal Catcher,id=18885,state=WAITING



