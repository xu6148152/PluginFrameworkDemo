package cn.binea.pluginframeworkdemo

import android.app.Application
import android.content.Context
import cn.binea.pluginframeworkdemo.dynamic_proxy_hook.hook.HookHelper

/**
 * Created by binea on 2/10/2017.
 */
class MyApp : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        try {
            HookHelper.attachContext()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}