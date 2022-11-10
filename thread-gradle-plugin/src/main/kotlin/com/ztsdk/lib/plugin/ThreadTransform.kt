package com.ztsdk.lib.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.logging.Logging
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.awt.SystemColor
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

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

    val NCPU = Runtime.getRuntime().availableProcessors()

    // 这是一个扩展的静态方法
    private fun QualifiedContent.transform(output: File) = this.file.transform(output)
    private val QualifiedContent.id: String
        get() = DigestUtils.md5Hex(file.absolutePath)

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

        val executor = Executors.newFixedThreadPool(NCPU)
        try {
            inputs?.map {
                //用it的属性在组合成新的collection集合，并返回
                it.directoryInputs + it.jarInputs
                // flattern是展开的意思
            }?.flatten()?.map { input ->
                executor.submit {
                    val format = if (input is DirectoryInput) Format.DIRECTORY else Format.JAR
                    outputProvider?.let { provider ->
                        input.transform(provider.getContentLocation(input.id, input.contentTypes, input.scopes, format))
                    }
                }
//            var format=if (input is DirectoryInput) Format.DIRECTORY else Format.JAR
//            outputProvider?.let {
//                // 调用transform的扩展方法
//                // id 也是扩展属性
//                input.transform(it.getContentLocation(input.id, input.contentTypes, input.scopes, format)) }


            }
        } finally {
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.HOURS)
        }
        var start = System.currentTimeMillis()


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

        var end = System.currentTimeMillis()
        println("ThreadTransform cost : (${end - start}) ms")
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
                Status.ADDED, Status.CHANGED -> {
                    // 可以修改

                    println("processJarInput added !!, name:${jarInput.name} ")
                    transformFromJar(jarInput, destFile)
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
//        val allFiles = FileUtils.getAllFiles(jarInput.file)
        // 第二种方式  其实size=1，也就是一个东西，草! 因此，直接处理就好了！
//        allFiles.forEach { file ->
        /**
         * jarName: ${jarInput.file.name},
         * name：${file.name}
         * 这两个是一样的。 xx.jar。
         */
//            println("ASM---第三方class文件 jarInputName: ${jarInput.name}，jarName: ${jarInput.file.name}, name：${file.name} ，size:${allFiles.size()}")
        // 获取目标文件 38.jar，因此要处理jar文件，怎么搞？
//        }

        // 注意，在这行代码前，我们都是以jar为单位处理，从这里开始遍历是jar里面的所有class类文件
        val jarInFile = jarInput.file

        var jarFile = JarFile(jarInFile)

        // 构建一个temp.jar 文件
        val tmpFile = File(jarInFile.parent + File.separator + "${jarInFile.name}_classes_temp.jar")
        // 避免上次的缓存被重复插入
        if (tmpFile.exists()) {
            tmpFile.delete()
        }
        // 建立输出流，注意是 JarOutput
        val jarOutputStream = JarOutputStream(FileOutputStream(tmpFile))

        val enumeration = jarFile.entries()
        while (enumeration.hasMoreElements()) {
            val jarEntry = enumeration.nextElement()
            // jar里每个元素的名称，对于java来说，就是类名(文件名)
            val entryName = jarEntry.name

//            entryName：
//            com/alibaba/sdk/android/utils/crashdefend/c.class
//            com/alibaba/sdk/android/utils/AMSDevReporter$AMSReportStatusEnum.class
//            org/greenrobot/eventbus/EventBus.class,
//            com/google/gson/internal/bind/TimeTypeAdapter$1.class
//            kotlin/sequences/_SequencesKt.kotlin_metadata
//            com/bumptech/glide/load/engine/OriginalKey.class,
//            androidx/appcompat/R$bool.class,
//            pub/devrel/easypermissions/R$integer.class
//            com/ztsdk/lib/gradletwo/R$dimen.class,

//            jarInFile.name： 45.jar
//            jarInFile.parent: :/Users/avengong/Desktop/demos/Gradle200/app/build/intermediates/transforms/booster/debug

            println("ASM---第三方lib entryName：${entryName},jarInFileName:${jarInFile.name}")

            // 封装成zipEntry why？ jar本身就是可以改成zip格式，然后解压的
            val zipEntry = ZipEntry(entryName)
            // 针对jarEntry构建输入流
            val inputStream = jarFile.getInputStream(jarEntry)

//            if (shouldProcessClass(entryName, extension.blackList)) { TODO 外部配置的东西

            if (shouldProcessClass(entryName, null)) {
//                project.logger.info("deal with jar file is: $file.absolutePath entryName is $entryName")
                jarOutputStream.putNextEntry(zipEntry)
                // 使用 ASM 对 class 文件进行操控
                processASMJarFile(inputStream)?.let { jarOutputStream.write(it) }
                inputStream.close()
//              jarOutputStream.write(runAsm(inputStream, project)) todo

            } else {
//                project.logger.info("undeal with jar file is: $file.absolutePath entryName is $entryName")
                // 如果命中黑名单，不做处理，直接输入
                jarOutputStream.putNextEntry(zipEntry)

                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }

            jarOutputStream.closeEntry()

        }
        jarOutputStream.close()
        jarFile.close()

        if (jarInFile.exists()) {
            jarInFile.delete()
        }
        // 要把临时文件重命名成源文件的名称
        tmpFile.renameTo(jarInFile)
        // 将处理后的jar copy到dest
        FileUtils.copyFile(jarInFile, destFile)

    }

    private fun processASMJarFile(inputStream: InputStream?): ByteArray? {

        // 用classReader读取class文件
        val classReader = ClassReader(inputStream)

        // 用classWriter写入修改后的内容到源文件
//        val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
        // 对于jar，需要用   COMPUTE_MAXS，上面的会报 java.lang.TypeNotPresentException:错误
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
        // 开始读取
        classReader.accept(HelloClassVisitor(classWriter), ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
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
            processASMClassFile(inputFile, destFile)
            FileUtils.copyFileToDirectory(destFile, destDirectory)
//
//            FileUtils.copyFileToDirectory(inputFile, destDirectory)

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
                processASMClassFile(file, destFile)
                FileUtils.copyFileToDirectory(destFile, destDirectory)

//                FileUtils.copyFileToDirectory(file, destDirectory)
            } else {
                FileUtils.copyFileToDirectory(file, destDirectory)
            }

            // ---------------------开始处理
        }
//        FileUtils.copyDirectory(dirInput.file, destDirectory)

    }


    /**
     * 处理源码的 class文件
     */
    fun processASMClassFile(inputFile: File, outputFile: File) {
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


    private fun shouldProcessClass(entryName: String, blackList: Set<String>?): Boolean {
//        val replaceEntryName = entryName.replace("/",".")
//        blackList?.forEach{
//            if (replaceEntryName.contains(it))
//                return false
//        }
        if (!entryName.endsWith(".class")
//                || entryName.contains("$") // kotlin object编译后都是内部类，因此这里要放开
            || entryName.endsWith("R.class")
            || entryName.endsWith("BuildConfig.class")
            || entryName.contains("android/support/")
            || entryName.contains("android/arch/") // 过滤一些系统的类
            || entryName.contains("android/app/")
            || entryName.contains("android/material")
            || entryName.contains("androidx")
            // 过滤协议文件
            || entryName.contains("com/ztsdk/lib/http/rpc/")
            || entryName.contains("com/ztsdk/lib/http/function/")
            // 过滤掉库本身
//                || entryName.contains("com/dysdk/lib/sentry/hook")
            || entryName.contains("com/dysdk/lib/privacy_annotation")
        ) {
            print("checkClassFile className is $entryName false")
            return false
        }
        print("checkClassFile className is $entryName true")
        return true
//    }
    }

    fun log(content: String) {
        Logging.getLogger(ThreadTransform::class.java).lifecycle(content)
    }
}