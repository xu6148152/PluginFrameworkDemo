package cn.binea.pluginframeworkdemo.classloader_hook

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Message
import cn.binea.pluginframeworkdemo.AMSHookHelper
import cn.binea.pluginframeworkdemo.AmsPmsHookHelperKt
import cn.binea.pluginframeworkdemo.MyApp
import cn.binea.pluginframeworkdemo.activity_manager.ActivityThreadHandlerCallbackKt
import java.lang.reflect.Field

/**
 * Created by binea on 6/10/2017.
 */
class ActivityInfoHandlerCallbackKt(base: Handler?) : ActivityThreadHandlerCallbackKt(base) {

    override fun handleLaunchActivity(message: Message) {
        val obj = message.obj

        var intentField: Field?
        try {
            intentField = obj.javaClass.getDeclaredField("intent")

            intentField!!.isAccessible = true
            val intent = intentField.get(obj) as Intent

            val target = intent.getParcelableExtra<Intent>(AMSHookHelper.EXTRA_TARGET_INTENT)
            if (target != null) {
                intent.component = target.component
            }

            val activityInfoField = obj.javaClass.getDeclaredField("activityInfo")
            activityInfoField.isAccessible = true
            val activityInfo = activityInfoField.get(obj) as ActivityInfo

            var packageName: String?
            if (target != null) {
                if (target.`package` == null) {
                    packageName = target.component.packageName
                } else {
                    packageName = target.`package`
                }
                activityInfo.applicationInfo.packageName = packageName
                hookPackageManager()
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    private fun hookPackageManager() {
        AmsPmsHookHelperKt.hookPackageManager(MyApp.getContext()!!, PackageManagerHookHandler(null))
    }
}