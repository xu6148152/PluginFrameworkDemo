package cn.binea.pluginframeworkdemo

import cn.binea.pluginframeworkdemo.activity_manager.ActivityManagerHandler
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

/**
 * Created by binea on 5/10/2017.
 */
class HookUtilsKt<T> {
    fun hookActivityManager(handler: InvocationHandler) {
        val activityManagerNativeClazz = Class.forName("android.app.ActivityManagerNative")
        val gDefaultField = activityManagerNativeClazz.getDeclaredField("gDefault")
        gDefaultField.isAccessible = true
        val gDefault = gDefaultField.get(null)

        val singletonClazz = Class.forName("android.util.Singleton")
        val instanceField = singletonClazz.getDeclaredField("mInstance")
        instanceField.isAccessible = true

        val rawIActivityManager = instanceField.get(gDefault)
        val iActivityManagerInterface = Class.forName("android.app.IActivityManager")
        val proxy = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader, arrayOf(iActivityManagerInterface), ActivityManagerHandler(rawIActivityManager))
        instanceField.set(gDefault, proxy)
    }
}