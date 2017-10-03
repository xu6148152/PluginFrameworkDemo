package cn.binea.pluginframeworkdemo.binder_hook

import android.content.ClipData
import android.os.IBinder
import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Created by binea on 3/10/2017.
 */
class BinderHookHandlerKt(base: IBinder, stubClass: Class<Any>) : InvocationHandler {
    companion object {
        val TAG = BinderHookHandlerKt::class.java.canonicalName
    }

    var obj = Any()

    init {
        try {
            val asInterfaceMethod: Method = stubClass.getDeclaredMethod("asInterface", IBinder::class.java)
            obj = asInterfaceMethod.invoke(null, base)
        } catch (e: Exception) {
            throw RuntimeException("hooked failed")
        }
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
        if ("getPrimaryClip" == method!!.name) {
            Log.d(TAG, "hook getPrimaryClip")
            return ClipData.newPlainText(null, "you have been hooked")
        }

        if ("hasPrimaryClip" == method.name) {
            return true
        }

        return method.invoke(obj, args)
    }
}