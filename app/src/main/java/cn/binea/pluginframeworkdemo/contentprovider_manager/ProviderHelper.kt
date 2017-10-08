package cn.binea.pluginframeworkdemo.contentprovider_manager

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.content.pm.ServiceInfo
import cn.binea.pluginframeworkdemo.service_manager.ServiceDispatcher
import java.io.File

/**
 * Created by binea on 8/10/2017.
 */
object ProviderHelper {
    val TAG: String = ProviderHelper::class.java.canonicalName

    @Throws(Exception::class)
    fun installProviders(context: Context, apkFile: File) {
        val providerInfos = parseProvider(apkFile)

        providerInfos.forEach { it.applicationInfo.packageName = context.packageName }

        val activityThreadClazz = Class.forName("android.app.ActivityThread")
        val currentActivityThreadMethod = activityThreadClazz.getDeclaredMethod("currentActivityThread")
        val currentActivityThreadObj = currentActivityThreadMethod.invoke(null)
        val installProvidersMethod = activityThreadClazz.getDeclaredMethod("installContentProviders")
        installProvidersMethod.isAccessible = true
        installProvidersMethod.invoke(currentActivityThreadObj, context, providerInfos)
    }

    fun parseProvider(apkFile: File): List<ProviderInfo> {
        val packageParserClazz = Class.forName("android.content.PackageParser")
        val parsePackageMethod = packageParserClazz.getDeclaredMethod("parsePackage", File::class.java, Int::class.java)

        val packageParser = packageParserClazz.newInstance()

        val packageObj = parsePackageMethod.invoke(packageParser, apkFile, PackageManager.GET_PROVIDERS)

        val componentField = packageObj.javaClass.getDeclaredField("providers")
        val providers = componentField.get(packageObj) as List<*>

        val packageParserServiceClazz = Class.forName("android.content.pm.PackageParser\$Provider")
        val packageUserStateClazz = Class.forName("android.content.pm.PackageUserState")
        val userHandler = Class.forName("android.os.UserHandle")
        val getCallingUserIdMethod = userHandler.getDeclaredMethod("getCallingUserId")
        val userId = getCallingUserIdMethod.invoke(null) as Int
        val defaultUserState = packageUserStateClazz.newInstance()

        val generateProviderInfo = packageParserClazz.getDeclaredMethod("generateProviderInfo", packageParserServiceClazz, Int::class.java, packageUserStateClazz, Int::class.java)

        val ret = ArrayList<ProviderInfo>()

        providers.mapTo(ret) { generateProviderInfo.invoke(packageParser, it, 0, defaultUserState, userId) as ProviderInfo }
        return ret
    }
}