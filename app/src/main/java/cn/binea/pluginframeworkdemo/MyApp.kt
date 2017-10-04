package cn.binea.pluginframeworkdemo

import android.app.Application
import android.content.Context
import cn.binea.pluginframeworkdemo.binder_hook.BinderHookHelperKt
import cn.binea.pluginframeworkdemo.dynamic_proxy_hook.hook.HookHelper
import cn.binea.pluginframeworkdemo.hook_ams_pms.AmsPmsHookHelperKt

/**
 * Created by binea on 2/10/2017.
 */
class MyApp : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        try {
            HookHelper.attachContext()
            BinderHookHelperKt.hookClipboardService()
            AmsPmsHookHelperKt.hookActivityManager()
            AmsPmsHookHelperKt.hookPackageManager(base!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}