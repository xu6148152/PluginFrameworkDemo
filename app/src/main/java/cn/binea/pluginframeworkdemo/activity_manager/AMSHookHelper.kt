package cn.binea.pluginframeworkdemo.activity_manager

import android.os.Handler
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy

/**
 * Created by binea on 5/10/2017.
 */
object AMSHookHelper {
    val EXTRA_TARGET_INTENT = "extra_target_intent"

    @Throws(ClassNotFoundException::class, NoSuchMethodException::class,
            InvocationTargetException::class, IllegalAccessException::class,
            NoSuchFieldException::class)
    fun hookActivityManager() {

    }

    @Throws(Exception::class)
    fun hookActivityThreadHandler() {
        val activityThreadClazz = Class.forName("android.app.ActivityThread")
        val currentActivityField = activityThreadClazz.getDeclaredField("sCurrentActivityThread")
        currentActivityField.isAccessible = true
        val currentActivityThread = currentActivityField.get(null)

        val mHField = activityThreadClazz.getDeclaredField("mH")
        mHField.isAccessible = true
        val mH = mHField.get(currentActivityThread) as Handler

        val mCallbackField = Handler::class.java.getDeclaredField("mCallback")
        mCallbackField.isAccessible = true

        mCallbackField.set(mH, ActivityThreadHandlerCallbackKt(mH))
    }
}