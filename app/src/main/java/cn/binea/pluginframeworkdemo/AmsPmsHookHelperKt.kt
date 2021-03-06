package cn.binea.pluginframeworkdemo

import android.content.Context
import java.lang.reflect.Proxy

/**
 * Created by binea on 4/10/2017.
 */
class AmsPmsHookHelperKt {
    companion object {
        fun hookActivityManager(invocationHandler: InvocationHandlerBase) {
            try {
                val activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative")

                val gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault")
                gDefaultField.isAccessible = true
                val gDefault = gDefaultField.get(null)

                val singletonClass = Class.forName("android.util.Singleton")
                val instanceField = singletonClass.getDeclaredField("mInstance")
                instanceField.isAccessible = true

                val rawIActivityManager = instanceField.get(gDefault)
                val activityManagerInterface = Class.forName("android.app.IActivityManager")
                invocationHandler.base = rawIActivityManager
                val proxy = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader, arrayOf(activityManagerInterface), invocationHandler)
                instanceField.set(gDefault, proxy)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun hookPackageManager(context: Context, invocationHandler: InvocationHandlerBase) {
            try {
                val activityThreadClazz = Class.forName("android.app.ActivityThread")
                val currentActivityThreadMethod = activityThreadClazz.getDeclaredMethod("currentActivityThread")
                val currentActivityThread = currentActivityThreadMethod.invoke(null)

                val packageManagerField = activityThreadClazz.getDeclaredField("sPackageManager")
                packageManagerField.isAccessible = true
                val packageManager = packageManagerField.get(currentActivityThread)
                invocationHandler.base = packageManager
                val packageManagerInterface = Class.forName("android.content.pm.IPackageManager")
                val proxy = Proxy.newProxyInstance(packageManagerInterface.classLoader, arrayOf(packageManagerInterface), invocationHandler)
                packageManagerField.set(currentActivityThread, proxy)

                val pm = context.packageManager
                val pmField = pm.javaClass.getDeclaredField("mPM")
                pmField.isAccessible = true
                pmField.set(pm, proxy)
            } catch (e: RuntimeException) {
                throw RuntimeException("hook failed")
            }
        }
    }
}