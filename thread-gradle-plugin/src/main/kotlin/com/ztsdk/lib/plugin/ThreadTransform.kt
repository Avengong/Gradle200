package com.ztsdk.lib.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.logging.Logging
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ThreadTransform : Transform() {
    val _logger = Logging.getLogger(ThreadTransform::class.java)


    override fun getName(): String {
//        app/build/intermediates/transforms目录下面.
        return "threadPlugin"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
//        CONTENT_CLASS：表示需要处理java的class文件
//        CONTENT_JARS：表示需要处理java的class与资源文件
//        CONTENT_RESOURCES：表示需要处理java的资源文件
//        CONTENT_NATIVE_LIBS：表示需要处理native库的代码  这个用来干什么？？
//        CONTENT_DEX：表示需要处理DEX文件
//        CONTENT_DEX_WITH_RESOURCES：表示需要处理DEX与java的资源文件
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
//        如果返回true,则TransformInput会包含一份修改的文件列表
//        如果是false,则进行全量编译,删除上一次输出内容

//        当然,开启了增量编译之后需要检查每个文件的Status,然后根据这个文件的Status进行不同的操作.
//
//        具体的Status如下:
//
//        NOTCHANGED: 当前文件不需要处理,连复制操作也不用
//        ADDED: 正常处理,输出给下一个任务
//        CHANGED: 正常处理,输出给下一个任务
//        REMOVED: 移除outputProvider获取路径对应的文件

        return true
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        //从理论上分析，既然是转换，那么转换完成后，肯定要输出到自己intermediates/transforms/threadPlugin/xxx/ 文件夹下
        val outputProvider = transformInvocation?.outputProvider
        val inputs = transformInvocation?.inputs

        val incremental = transformInvocation?.isIncremental
        if (incremental == true) {
            println("threadtransform start ，incremental is true! ")
        } else {
            // 不是增量编译则清空
            println("threadtransform start ，incremental is false! ")
            outputProvider?.deleteAll()
        }

        // 1. 遍历所有的输入源：
        inputs?.forEach { input ->
            // 1.1 得到源码中的 所有文件夹
            input.directoryInputs.forEach { dirInput ->

                processDirectoryInput(transformInvocation, dirInput, outputProvider)

            }

            // 1.2 所有的jar 库
            input.jarInputs.forEach { jarInput ->
                // 针对每一个第三方的jar进行处理
                // 还要根据这个status来决定是否启动增量更新
                processJarInput(transformInvocation, jarInput, outputProvider)
            }
        }
    }

    // 根据每一个jarinput 来进行处理
    private fun processJarInput(
        transformInvocation: TransformInvocation,
        jarInput: JarInput,
        outputProvider: TransformOutputProvider?
    ) {

        val destFile = outputProvider?.getContentLocation(
            jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR
        )
        // 不一定由isIncremental()方法决定，比如clean后的第一次编译，肯定是全量编译
        val incremental = transformInvocation.isIncremental
        if (incremental) {
            // 如果是增量编译，那么这个jar文件是否有改动？
            when (jarInput.status) {
                Status.NOTCHANGED -> {
                    // 啥都不做
                    println("processJarInput notchanged !!, name:${jarInput.name} ")
                }
                Status.ADDED -> {
                    // 可以修改

                    println("processJarInput added !!, name:${jarInput.name} ")
                    transformFromJar(jarInput, destFile)
                }
                Status.CHANGED -> {
                    println("processJarInput changed !!, name:${jarInput.name} ")
                }
                Status.REMOVED -> {

                    println("processJarInput removed !!, name:${jarInput.name} ")
                    // 删掉这个文件
                    if (destFile?.exists() == true) {
                        FileUtils.delete(destFile)
                    }
                }
                else -> {

                }
            }
        } else {
            // 因为这个 outputProvider路径和项目本身代码是存放在一起的，因此，自己的代码也被清掉了。
//            outputProvider?.deleteAll() 就是因为这个杀掉操作导致没有生成xxx/transform/threadplugin/xx.jar

            transformFromJar(jarInput, destFile)
        }
    }

    private fun transformFromJar(jarInput: JarInput, destFile: File?) {
        val allFiles = FileUtils.getAllFiles(jarInput.file)

        // 注意，在这行代码前，我们都是以jar为单位处理，从这里开始遍历是jar里面的所有class类文件
        allFiles.forEach { file ->
            println("ASM---第三方class文件 name：${file.name}")
            // 获取目标文件
            // ---------------------开始处理
            //
        }
        // 将处理后的jar copy到dest
        FileUtils.copyFile(jarInput.file, destFile)

    }

    private fun processDirectoryInput(
        transformInvocation: TransformInvocation,
        dirInput: DirectoryInput,
        outputProvider: TransformOutputProvider?
    ) {
        // 如果是增量更新，根据changedfiles来针对单个file进行更新
        if (transformInvocation.isIncremental) {
            println("processDirectoryInput  start incremental processing! ")
            val changedFiles = dirInput.changedFiles
            //改变的file，顾名思义就是只处理此次改变的部分，其他的部分就不用处理
            // 只有当增量编译的时候才会生效
            // 确定目标dest文件夹
            // 只验证这个是否可以？？？？？TODO 可以的
            val destDirectory = outputProvider?.getContentLocation(
                dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY
            )
            destDirectory?.mkdirs()
            changedFiles.forEach { file, status ->
                val destFile = destDirectory?.let { File(it, file.name) }
                when (status) {
                    Status.NOTCHANGED -> {
                        // do nothing
                        println("processDirectoryInput notchanged !!, name:${file.name} ")
                    }
                    Status.ADDED -> {

                        println("processDirectoryInput added !!, name:${file.name} ")
                        if (destFile != null) {
                            transformIncrementalSingleFile(file, destFile, destDirectory)
                        }
                    }
                    Status.CHANGED -> {
                        println("processDirectoryInput changed !!, name:${file.name}  ")
                        if (destFile != null) {
                            transformIncrementalSingleFile(file, destFile, destDirectory)
                        }
                    }
                    Status.REMOVED -> {
                        println("processDirectoryInput removed !!, name:${file.name} ")
                        if (file.exists())
                            FileUtils.delete(file)
                    }
                    else -> {

                    }

                }
            }
        } else {
            // 非增量更新，则全部进行更新
            println("processDirectoryInput  start full tranform! ")
            transformDirectory(dirInput, outputProvider)
        }
    }

    /**
     * 增量编译单个源码文件
     */
    private fun transformIncrementalSingleFile(inputFile: File, destFile: File, destDirectory: File?) {
        println("拷贝单个文件，name：${inputFile.name}, absolutePath:${inputFile?.absolutePath}")
        // 是否需要transform
        if (inputFile.name.endsWith(".class")) {
            if (destFile.exists())
                FileUtils.delete(destFile)
            copyFile(inputFile, destFile)
            FileUtils.copyFileToDirectory(destFile, destDirectory)
        } else {
            FileUtils.copyFile(inputFile, destFile) // 待验证,可行。
        }


        // 方式二 可行。
//        FileUtils.copyFileToDirectory(file, destDirectory)
    }

    /**
     * 全量编译所有源码目录
     */
    private fun transformDirectory(
        dirInput: DirectoryInput,
        outputProvider: TransformOutputProvider?
    ) {
        // 拷贝整个文件夹

        //遍历每个目录下的所有文件
        val allFiles = FileUtils.getAllFiles(dirInput.file)
        val destDirectory = outputProvider?.getContentLocation(
            dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY
        )
        destDirectory?.mkdirs()
        allFiles.forEach { file ->
            // 得到单个文件 从 前一个插件处理后的文件而来
            println("ASM---自己的源码 文件name：${file.name}, absolutePath: ${file.absolutePath}")
            var destFile = File(destDirectory, file.name)
            // ---------------------开始处理
            if (file.name.contains(".class")) {
                copyFile(file, destFile)
                FileUtils.copyFileToDirectory(destFile, destDirectory)
            } else {
                FileUtils.copyFileToDirectory(file, destDirectory)
            }

            // ---------------------开始处理
        }
//        FileUtils.copyDirectory(dirInput.file, destDirectory)

    }


    fun copyFile(inputFile: File, outputFile: File) {
        var inputStream = FileInputStream(inputFile)
        var outputStream = FileOutputStream(outputFile)
        // 用classReader读取class文件

        val classReader = ClassReader(inputStream)

        // 用classWriter写入修改后的内容到源文件

        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
        // 开始读取
        classReader.accept(HelloClassVisitor(classWriter), ClassReader.EXPAND_FRAMES)
        outputStream.write(classWriter.toByteArray())
        inputStream.close()
        outputStream.close()

    }


    fun log(content: String) {
        Logging.getLogger(ThreadTransform::class.java).lifecycle(content)
    }
}