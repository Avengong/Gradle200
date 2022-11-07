package com.ztsdk.lib.gradletwo

import com.tcloud.core.app.AppConfig
import com.tcloud.core.app.BaseApp
import com.tcloud.core.app.IConfigProvider

class GApp : BaseApp() {
    override fun onAppParamInit() {

        AppConfig.setProvider(object : IConfigProvider {
            override fun initDefault() {
            }

            override fun initDebug() {
            }

            override fun initTest() {
            }

            override fun initProduct() {
            }

            override fun afterInit() {
            }

        })
    }
}