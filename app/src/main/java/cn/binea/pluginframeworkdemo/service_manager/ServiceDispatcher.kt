package cn.binea.pluginframeworkdemo.service_manager

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Binder
import android.util.Log
import cn.binea.pluginframeworkdemo.AMSHookHelper
import cn.binea.pluginframeworkdemo.MyApp
import java.io.File

/**
 * Created by binea on 7/10/2017.
 */
object ServiceDispatcher {
    val TAG = ServiceDispatcher::class.java.canonicalName

    val mServiceMap = HashMap<String, Service>()

    val mServiceInfoMap = HashMap<ComponentName, ServiceInfo>()

    fun onStartCommand(proxyIntent: Intent?, flags: Int, startId: Int) {
        val targetIntent = proxyIntent?.getParcelableArrayExtra(AMSHookHelper.EXTRA_TARGET_INTENT) as Intent
        val serviceInfo = selectPluginService(targetIntent)

        if (serviceInfo == null) {
            Log.e(TAG, "can't find service: " + targetIntent.component)
            return
        }

        try {
            if (!mServiceMap.containsKey(serviceInfo.name)) {
                proxyCreateService(serviceInfo)
            }

            val service = mServiceMap[serviceInfo.name]
            service!!.onStartCommand(targetIntent, flags, startId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun proxyCreateService(serviceInfo: ServiceInfo) {
        val token = Binder()

        val createServiceDataClazz = Class.forName("android.app.ThreadActivity\$CreateServiceData")
        val constructor = createServiceDataClazz.getDeclaredConstructor()
        constructor.isAccessible = true
        val createServiceObj = constructor.newInstance()

        val tokenField = createServiceDataClazz.getDeclaredField("token")
        tokenField.isAccessible = true
        tokenField.set(createServiceObj, token)

        serviceInfo.applicationInfo.packageName = MyApp.getContext()!!.packageName
        val infoField = createServiceDataClazz.getDeclaredField("info")
        infoField.set(createServiceObj, serviceInfo)

        val compatibilityClazz = Class.forName("android.content.res.CompatibilityInfo")
        val defaultCompatibilityField = compatibilityClazz.getDeclaredField("DEFAULT_COMPATIBILITY_INFO")
        val defaultCompatibilityObj = defaultCompatibilityField.get(null)
        val compatInfoField = createServiceDataClazz.getDeclaredField("compatInfo")
        compatInfoField.isAccessible = true
        compatInfoField.set(createServiceObj, defaultCompatibilityObj)

        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread")
        currentActivityThreadMethod.isAccessible = true
        val currentActivityThreadObj = currentActivityThreadMethod.invoke(null)

        val handleCreateServiceMethod = activityThreadClass.getDeclaredMethod("handleCreateService", createServiceDataClazz)
        handleCreateServiceMethod.isAccessible = true
        handleCreateServiceMethod.invoke(currentActivityThreadObj, createServiceObj)

        val servicesField = activityThreadClass.getDeclaredField("mServices")
        servicesField.isAccessible = true
        val services = servicesField.get(currentActivityThreadObj) as HashMap<*, *>
        val service = services[token] as Service
        services.remove(token)

        mServiceMap.put(serviceInfo.name, service)
    }

    fun selectPluginService(intent: Intent): ServiceInfo? {
        return mServiceInfoMap.keys
                .firstOrNull { it == intent.component }
                ?.let { mServiceInfoMap[it] }
    }

    @Throws(Exception::class)
    fun preLoadService(apkFile: File) {
        val packageParserClazz = Class.forName("android.content.PackageParser")
        val parsePackageMethod = packageParserClazz.getDeclaredMethod("parsePackage", File::class.java, Int::class.java)

        val packageParser = packageParserClazz.newInstance()

        val packageObj = parsePackageMethod.invoke(packageParser, apkFile, PackageManager.GET_SERVICES)

        val servicesField = packageObj.javaClass.getDeclaredField("services")
        val services = servicesField.get(packageObj) as List<*>

        val packageParserServiceClazz = Class.forName("android.content.pm.PackageParser\$Service")
        val packageUserStateClazz = Class.forName("android.content.pm.PackageUserState")
        val userHandler = Class.forName("android.os.UserHandle")
        val getCallingUserIdMethod = userHandler.getDeclaredMethod("getCallingUserId")
        val userId = getCallingUserIdMethod.invoke(null) as Int
        val defaultUserState = packageUserStateClazz.newInstance()

        val generateReceiverInfo = packageParserClazz.getDeclaredMethod("generateServiceInfo", packageParserServiceClazz, Int::class.java, packageUserStateClazz, Int::class.java)

        services
                .map { generateReceiverInfo.invoke(packageParser, it, defaultUserState, userId) as ServiceInfo }
                .forEach { mServiceInfoMap.put(ComponentName(it.packageName, it.name), it) }
    }

    fun stopService(intent: Intent): Int {
        val serviceInfo = selectPluginService(intent)
        if (serviceInfo == null) {
            Log.e(TAG, "can't find service: " + intent.component)
            return 0
        }

        val service = mServiceMap[serviceInfo.name]
        if (service == null) {
            Log.e(TAG, "service has been stopped, please don't stop again")
            return 0
        }

        service.onDestroy()
        mServiceMap.remove(serviceInfo.name)
        if (mServiceMap.isEmpty()) {
            Log.d(TAG, "all service have been stopped, stop proxy service")
            val context = MyApp.getContext()!!
            context.stopService(Intent().setComponent(ComponentName(context.packageName, ProxyService::class.java.name)))
        }

        return 1
    }
}