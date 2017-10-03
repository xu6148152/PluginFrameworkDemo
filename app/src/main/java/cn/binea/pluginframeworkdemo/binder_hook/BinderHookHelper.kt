package cn.binea.pluginframeworkdemo.binder_hook

import android.annotation.SuppressLint
import android.os.IBinder
import java.lang.reflect.Proxy

/**
 * Created by binea on 3/10/2017.
 */
@SuppressLint("PrivateApi")
class BinderHookHelper {
    companion object {

        @Throws(Exception::class)
        fun hookClipboardService() {
            val CLIPBOARD_SERVICE = "clipboard"
            val serviceManager = Class.forName("android.os.ServiceManager")
            val getService = serviceManager.getDeclaredMethod("getService", String::class.java)
            val rawBinder = getService.invoke(null, CLIPBOARD_SERVICE)
            val hookedBinder = Proxy.newProxyInstance(serviceManager.classLoader, arrayOf(IBinder::class.java), BinderProxyHookHandlerKt(rawBinder as IBinder))
            val cacheField = serviceManager.getDeclaredField("sCache")
            cacheField.isAccessible = true
            val cache = cacheField.get(null) as HashMap<String, IBinder>
            cache.put(CLIPBOARD_SERVICE, hookedBinder as IBinder)
        }
    }
}