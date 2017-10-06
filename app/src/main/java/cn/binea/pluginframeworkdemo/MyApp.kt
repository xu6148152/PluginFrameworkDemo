package cn.binea.pluginframeworkdemo

import android.app.Application
import android.content.Context
import cn.binea.pluginframeworkdemo.activity_manager.ActivityManagerHandler
import cn.binea.pluginframeworkdemo.activity_manager.ActivityManagerHandlerKt
import cn.binea.pluginframeworkdemo.binder_hook.BinderHookHelperKt
import cn.binea.pluginframeworkdemo.ams_pms_hook.HookHandler

/**
 * Created by binea on 2/10/2017.
 */
class MyApp : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        try {
//            HookHelper.attachContext()
            BinderHookHelperKt.hookClipboardService()
            AmsPmsHookHelperKt.hookActivityManager(HookHandler(null))
            AmsPmsHookHelperKt.hookPackageManager(base!!)

            AMSHookHelper.hookActivityManager(ActivityManagerHandler(null))
            AMSHookHelper.hookActivityThreadHandler()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}