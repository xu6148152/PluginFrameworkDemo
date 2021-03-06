package cn.binea.pluginframeworkdemo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import cn.binea.pluginframeworkdemo.activity_manager.ActivityManagerHandler
import cn.binea.pluginframeworkdemo.binder_hook.BinderHookHelperKt
import cn.binea.pluginframeworkdemo.ams_pms_hook.HookHandler
import cn.binea.pluginframeworkdemo.classloader_hook.ActivityInfoHandlerCallbackKt
import cn.binea.pluginframeworkdemo.classloader_hook.BaseDexClassLoaderHookHelper
import cn.binea.pluginframeworkdemo.contentprovider_manager.ProviderHelper
import cn.binea.pluginframeworkdemo.receiver_manager.ReceiverHelperKt
import cn.binea.pluginframeworkdemo.service_manager.ServiceDispatcher

/**
 * Created by binea on 2/10/2017.
 */
@SuppressLint("StaticFieldLeak")
class MyApp : Application() {

    companion object {

        var sContext: Context? = null

        fun getContext(): Context? {
            return sContext
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        sContext = base
        try {
//            HookHelper.attachContext()
            BinderHookHelperKt.hookClipboardService()
            AmsPmsHookHelperKt.hookActivityManager(HookHandler(null))
            AmsPmsHookHelperKt.hookPackageManager(base!!, HookHandler(null))

            AMSHookHelper.hookActivityManager(ActivityManagerHandler(null))
            AMSHookHelper.hookActivityThreadHandler(ActivityInfoHandlerCallbackKt(null))

            hookReceiverComponent()

            handleServiceComponent()

            handleProviderComponent()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hookReceiverComponent() {
        CommonUtils.extractAssets(this, "test.jar")
        val testPlugin = getFileStreamPath("test.jar")
        try {
            ReceiverHelperKt.preLoadReceiver(this, testPlugin)
            Log.d(javaClass.simpleName, "hook success")
        } catch (e: Exception) {
            throw RuntimeException("hook receiver failed", e)
        }
    }

    private fun handleServiceComponent() {
        try {
            CommonUtils.extractAssets(sContext!!, "test_service.jar")
            val apkFile = getFileStreamPath("test_service.jar")
            val odexFile = getFileStreamPath("test_service.odex")

            BaseDexClassLoaderHookHelper.patchClassLoader(classLoader, apkFile, odexFile)
            ServiceDispatcher.preLoadService(apkFile)
        } catch (e: Exception) {
            throw RuntimeException("hook service failed")
        }
    }

    private fun handleProviderComponent() {
        try {
            val apkFile = getFileStreamPath("TestContentProvider.java")
            if (!apkFile.exists()) {
                CommonUtils.extractAssets(sContext!!, "TestContentProvider.java")
            }

            val odexFile = getFileStreamPath("test.odex")

            BaseDexClassLoaderHookHelper.patchClassLoader(classLoader, apkFile, odexFile)
            ProviderHelper.installProviders(sContext!!, getFileStreamPath("TestContentProvider.java"))
        } catch (e: Exception) {
            throw RuntimeException("hook provider fail")
        }
    }
}