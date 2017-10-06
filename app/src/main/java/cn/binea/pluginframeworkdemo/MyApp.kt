package cn.binea.pluginframeworkdemo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cn.binea.pluginframeworkdemo.activity_manager.ActivityManagerHandler
import cn.binea.pluginframeworkdemo.activity_manager.ActivityManagerHandlerKt
import cn.binea.pluginframeworkdemo.activity_manager.ActivityThreadHandlerCallbackKt
import cn.binea.pluginframeworkdemo.binder_hook.BinderHookHelperKt
import cn.binea.pluginframeworkdemo.ams_pms_hook.HookHandler
import cn.binea.pluginframeworkdemo.classloader_hook.ActivityInfoHandlerCallbackKt

/**
 * Created by binea on 2/10/2017.
 */
@SuppressLint("StaticFieldLeak")
class MyApp : Application() {

    companion object {

        var sContext: Context? = null

        fun getContext(): Context? {
            return sContext
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        sContext = base
        try {
//            HookHelper.attachContext()
            BinderHookHelperKt.hookClipboardService()
            AmsPmsHookHelperKt.hookActivityManager(HookHandler(null))
            AmsPmsHookHelperKt.hookPackageManager(base!!, HookHandler(null))

            AMSHookHelper.hookActivityManager(ActivityManagerHandler(null))
            AMSHookHelper.hookActivityThreadHandler(ActivityInfoHandlerCallbackKt(null))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}