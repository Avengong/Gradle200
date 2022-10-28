package com.ztsdk.lib.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.logging.Logging
import java.io.File


class ThreadTransform : Transform() {
    val _logger = Logging.getLogger(ThreadTransform::class.java)

    override fun getName(): String {

        return "threadPlugin"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {

//        如果返回true,则TransformInput会包含一份修改的文件列表
//        如果是false,则进行全量编译,删除上一次输出内容

        return false
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        //从理论上分析，既然是转换，那么转换完成后，肯定要输出到自己intermediates/transforms/threadPlugin/xxx/ 文件夹下
        val outputProvider = transformInvocation?.outputProvider
        val inputs = transformInvocation?.inputs
        // 1. 遍历所有的输入源：
        // 有可能是我们自己的源码(文件夹形式存在);
        // 也有可能是第三方的库(以jar的形式存在)
        inputs?.forEach { input ->
            // 1.1 得到所有文件夹的输入
            val directoryInputs = input.directoryInputs
            //遍历文件夹，得到每一个目录
            directoryInputs.forEach { dirInput ->


                // TODO 对于增量更新，input 会返回一份修改列表,每次编译只会编译自己的代码，因此jarInput是不会有增量更新
                //TODO 的概念。所以，只需要处理directory的逻辑即可
                // Map<File, Status> changedFiles;
                val changedFiles = dirInput.changedFiles

                //遍历每个目录下的所有文件
                val allFiles = FileUtils.getAllFiles(dirInput.file)
                allFiles.forEach { file ->
                    // 得到单个文件 从 前一个插件处理后的文件而来
                    println("ASM---自己的源码 文件name：${file.name}, absolutePath: ${file.absolutePath}")
//                   MainActivity.class,
//                   absolutePath: /Users/avengong/Desktop/demos/Gradle200/app/build/intermediates/transforms/booster/debug/47/com/ztsdk/lib/gradletwo/MainActivity.class

                    // 为啥？？？ 因为如果在这里修改了class，那么就必须要覆盖到原来的class 文件，否则改了无效。
//                   contentLocationOld: /Users/avengong/Desktop/demos/Gradle200/app/build/intermediates/transforms/threadPlugin/debug/0
//                   val contentLocationOld = outputProvider?.getContentLocation(
//                       file.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY
//                   )

                    // 获取文件的开始路径，后面我还要写回去呢
//                   contentLocation： /Users/avengong/Desktop/demos/Gradle200/app/build/intermediates/transforms/threadPlugin/debug/1,
                    val contentLocation = outputProvider?.getContentLocation(
                        dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY
                    )
                    // todo 这一步至关重要！！
                    contentLocation?.mkdirs()

                    if (file.name.endsWith(".class")) {
//                       println("ASM --- .class的类文件： ${file.name}，contentLocation： " +
//                               "${contentLocation?.absolutePath},\n contentLocationOld: ${contentLocationOld}")
                        _logger.debug(".class的类文件： ${file.name}")
                    }
                    // ---------------------开始处理
                    processDirectoryClass(file)

                    // ---------------------开始处理
                    // 将每个文件拷贝到目标目录
                    FileUtils.copyFileToDirectory(file, contentLocation)

                }
            }

            // 1.2 所有的jar
            val jarInputs = input.jarInputs
            jarInputs.forEach { jarinput ->


                val allFiles = FileUtils.getAllFiles(jarinput.file)

                allFiles.forEach { file ->
                    println("ASM---第三方lib 文件name：${file.name}")
                    // 获取目标文件
                    val contentLocation = outputProvider?.getContentLocation(
                        jarinput.name, jarinput.contentTypes, jarinput.scopes, Format.JAR
                    )
                    // 拷贝到目标文件，生改动生效

                    // ---------------------开始处理
                    processJarClass(file)

                    // ---------------------开始处理
                    FileUtils.copyFile(file, contentLocation)
                }

            }
        }

        // 还有个问题： 我拿到每个class后，怎么去访问里面的成员？
        // 方法：
        // 变量：

        // 怎么插入我们自己的代码。

    }

    private fun processJarClass(file: File?): File {

        // todo
        return File("")
    }

    private fun processDirectoryClass(file: File?): File {

        // todo
        return File("")
    }

    fun log(content: String) {
        Logging.getLogger(ThreadTransform::class.java).lifecycle(content)
    }
}