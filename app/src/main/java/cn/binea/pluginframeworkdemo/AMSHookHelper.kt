package cn.binea.pluginframeworkdemo

import android.os.Handler
import cn.binea.pluginframeworkdemo.activity_manager.ActivityThreadHandlerCallbackKt
import java.lang.reflect.InvocationTargetException

/**
 * Created by binea on 5/10/2017.
 */
object AMSHookHelper {
    val EXTRA_TARGET_INTENT = "extra_target_intent"

    @Throws(ClassNotFoundException::class, NoSuchMethodException::class,
            InvocationTargetException::class, IllegalAccessException::class,
            NoSuchFieldException::class)
    fun hookActivityManager(invocationHandlerBase: InvocationHandlerBase) {
        AmsPmsHookHelperKt.hookActivityManager(invocationHandlerBase)
    }

    @Throws(Exception::class)
    fun hookActivityThreadHandler(callbackKt: ActivityThreadHandlerCallbackKt) {
        val activityThreadClazz = Class.forName("android.app.ActivityThread")
        val currentActivityField = activityThreadClazz.getDeclaredField("sCurrentActivityThread")
        currentActivityField.isAccessible = true
        val currentActivityThread = currentActivityField.get(null)

        val mHField = activityThreadClazz.getDeclaredField("mH")
        mHField.isAccessible = true
        val mH = mHField.get(currentActivityThread) as Handler

        val mCallbackField = Handler::class.java.getDeclaredField("mCallback")
        mCallbackField.isAccessible = true
        callbackKt.base = mH
        mCallbackField.set(mH, callbackKt)
    }
}