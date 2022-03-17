native hook

原理？？

想要知道进程中每个动态库分配内存的大小，是要hook每个动态库还是只需要hook进程的？ 我的理解： 1 所有动态库都会调用其中某个动态库，比如glibc.so，而glibc.so
动态库内部封装了内存分配malloc、线程创建等具体实现，因此
只要hook住该动态库的got/plt即可。因此，是对具体so库的hook。因为每个动态库都会在装载时或者运行时动态加载到进程的虚拟内存中，因此，我们能够访问到 libc.so的plt。

2 hook某个so库的plt，替换调用函数的地址。

plt：

在可执行文件的虚拟地址空间中， ELF文件中

inline：

分类？ 有哪些方式？ plt hook inline hook

怎么去实践？？

hook 线程创建方法 


















