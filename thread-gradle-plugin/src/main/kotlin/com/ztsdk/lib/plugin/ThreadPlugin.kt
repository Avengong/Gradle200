package com.ztsdk.lib.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logging


// 为啥不用()，因为它不是一个类，是一个接口，不能被new对象。
class ThreadPlugin : Plugin<Project> {

    private val _logger = Logging.getLogger(ThreadPlugin::class.java)
    override fun apply(project: Project) {
        println("各种是一个接口测试")
        _logger.debug("aabb 各种是一个接口测试 ")
        val extension = project.extensions.findByType(AppExtension::class.java)
        extension?.registerTransform(ThreadTransform())

        //注册方式2
//        project.android.registerTransform(new MethodTimeTransform())

    }
}