package cn.binea.pluginframeworkdemo.binder_hook

import android.os.IBinder
import android.os.IInterface
import android.support.v4.os.IResultReceiver
import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Created by binea on 3/10/2017.
 */
class BinderProxyHookHandlerKt(val base: IBinder) : InvocationHandler {

    companion object {
        val TAG = BinderProxyHookHandlerKt::class.java.canonicalName
    }

    var stub = Any()
    var iinterface = Any()

    init {
        try {
            stub = Class.forName("android.content.IClipboard\$Stub")
            iinterface = Class.forName("android.content.IClipboard")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
        if ("queryLocalInterface" == method!!.name) {
            Log.d(TAG, "hook queryLocalInterface")
            val classArray = arrayOf(IBinder::class.java, IInterface::class.java)
            return Proxy.newProxyInstance((proxy as Any).javaClass.classLoader, classArray, BinderHookHandlerKt(base, stub as Class<Any>))
        }

        Log.d(TAG, "method: " + method.name)
        return method.invoke(base, args)
    }
}