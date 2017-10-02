package cn.binea.pluginframeworkdemo.dynamic_proxy_hook.hook

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log

/**
 * Created by binea on 2/10/2017.
 */
class ProxyInstrumentationKt(val instrumentation: Instrumentation) : Instrumentation() {
    companion object {
        val TAG = ProxyInstrumentationKt::class.java.canonicalName
    }

    fun execStartActivity(
            who: Context, contextThread: IBinder?, token: IBinder?, target: Activity?,
            intent: Intent, requestCode: Int, options: Bundle?): Instrumentation.ActivityResult? {
        Log.d(TAG, "\nstartActivity, who = [" + who + "], " +
                "\ncontextThread = [" + contextThread + "], \ntoken = [" + token + "], " +
                "\ntarget = [" + target + "], \nintent = [" + intent +
                "], \nrequestCode = [" + requestCode + "], \noptions = [" + options + "]")

        try {
            val execStartActivity = Instrumentation::class.java.getDeclaredMethod("execStartActivity", Context::class.java, IBinder::class.java, IBinder::class.java, Activity::class.java, Intent::class.java, Int::class.javaPrimitiveType, Bundle::class.java)
            execStartActivity.isAccessible = true
            return execStartActivity.invoke(instrumentation, who, contextThread, token, target, intent, requestCode, options) as Instrumentation.ActivityResult?
        } catch (e: Exception) {
            throw RuntimeException("don't support hook")
        }

    }
}