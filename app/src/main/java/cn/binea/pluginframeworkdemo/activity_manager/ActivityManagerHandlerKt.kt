package cn.binea.pluginframeworkdemo.activity_manager

import android.content.ComponentName
import android.content.Intent
import android.util.Log
import cn.binea.pluginframeworkdemo.AMSHookHelper
import cn.binea.pluginframeworkdemo.InvocationHandlerBase
import java.lang.reflect.Method

/**
 * Created by binea on 5/10/2017.
 */
class ActivityManagerHandlerKt(base: Any?) : InvocationHandlerBase(base) {

    companion object {
        val TAG = ActivityManagerHandlerKt::class.java.canonicalName
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<Any>?): Any {
        if ("startActivity" == method!!.name) {
            val index = args!!.indices.firstOrNull { args[it] is Intent }
                    ?: -1

            if (index != -1) {
                val intent = args[index] as Intent

                val newIntent = Intent()
                val stubPackage = "cn.binea.pluginframeworkdemo"

                val componentName = ComponentName(stubPackage, StubActivity::class.java.name)
                newIntent.component = componentName
                newIntent.putExtra(AMSHookHelper.EXTRA_TARGET_INTENT, intent)

                args[index] = newIntent

                Log.d(TAG, "hook success")
            }
        }
        return method.invoke(base, args)
    }
}