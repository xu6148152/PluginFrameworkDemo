package cn.binea.pluginframeworkdemo.dynamic_proxy_hook.hook

import android.annotation.SuppressLint
import android.app.Instrumentation

/**
 * Created by binea on 2/10/2017.
 */
@SuppressLint("PrivateApi")
class HookHelper {
    companion object {

        @Throws(Exception::class)
        fun attachContext() {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread")
            currentActivityThreadMethod.isAccessible = true
            val currentActivityThread = currentActivityThreadMethod.invoke(null)
            val instrumentationField = activityThreadClass.getDeclaredField("mInstrumentation")
            instrumentationField.isAccessible = true
            val instrumentation = instrumentationField.get(currentActivityThread) as Instrumentation

            //代理instrumentation
            val proxyInstrumentation = ProxyInstrumentationKt(instrumentation)
            instrumentationField.set(currentActivityThread, proxyInstrumentation)
        }
    }
}