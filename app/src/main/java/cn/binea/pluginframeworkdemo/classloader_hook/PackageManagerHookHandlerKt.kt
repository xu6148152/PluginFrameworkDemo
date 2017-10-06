package cn.binea.pluginframeworkdemo.classloader_hook

import android.content.pm.PackageInfo
import cn.binea.pluginframeworkdemo.InvocationHandlerBase
import java.lang.reflect.Method

/**
 * Created by binea on 6/10/2017.
 */
class PackageManagerHookHandlerKt(base: Any?) : InvocationHandlerBase(base) {
    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
        if (method!!.name.equals("getPackageInfo")) {
            return PackageInfo()
        }
        return method.invoke(base, args)
    }
}