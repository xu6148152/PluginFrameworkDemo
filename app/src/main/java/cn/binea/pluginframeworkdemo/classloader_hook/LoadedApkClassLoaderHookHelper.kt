package cn.binea.pluginframeworkdemo.classloader_hook

import android.content.pm.ApplicationInfo
import android.util.ArrayMap
import cn.binea.pluginframeworkdemo.CommonUtils
import cn.binea.pluginframeworkdemo.CustomClassLoader
import java.io.File
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationTargetException

/**
 * Created by binea on 6/10/2017.
 */
class LoadedApkClassLoaderHookHelper {
    companion object {
        val sLoadedApk = HashMap<String, Any>()

        @Throws(ClassNotFoundException::class, NoSuchMethodException::class,
                InvocationTargetException::class, IllegalAccessException::class,
                NoSuchFieldException::class, InstantiationException::class)
        fun hookLoadedApkInActivityThread(apkFile: File) {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread")
            currentActivityThreadMethod.isAccessible = true
            val currentActivityThread = currentActivityThreadMethod.invoke(null)

            val packageField = activityThreadClass.getDeclaredField("mPackages")
            packageField.isAccessible = true
            val packages = packageField.get(currentActivityThread) as ArrayMap<String, Any>

            val compatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo")
            val getPackageInfoNoCheckMethod = activityThreadClass.getDeclaredMethod("getPackageInfoNoCheck", ApplicationInfo::class.java, compatibilityInfoClass)

            val defaultCompatibilityInfoField = compatibilityInfoClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO")
            defaultCompatibilityInfoField.isAccessible = true

            val defaultCompatibilityInfo = defaultCompatibilityInfoField.get(null)
            val applicationInfo = generateApplicationInfo(apkFile)

            val loadedApk = getPackageInfoNoCheckMethod.invoke(currentActivityThread, applicationInfo, defaultCompatibilityInfo)

            val odexPath = CommonUtils.getPluginOptDexDir(applicationInfo!!.packageName)!!.path
            val libDir = CommonUtils.getPluginLibDir(applicationInfo.packageName)!!.path
            val classLoader = CustomClassLoader(apkFile.path, odexPath, libDir, ClassLoader.getSystemClassLoader())
            val classLoaderField = loadedApk.javaClass.getDeclaredField("mClassLoader")
            classLoaderField.isAccessible = true
            classLoaderField.set(loadedApk, classLoader)

            sLoadedApk.put(applicationInfo.packageName, loadedApk)
            val weakReference = WeakReference(loadedApk)
            packages.put(applicationInfo.packageName, weakReference)
        }

        private fun generateApplicationInfo(apkFile: File): ApplicationInfo? {
            val packageParserClass = Class.forName("android.content.pm.PackageParser")
            val packageParserPackageClass = Class.forName("android.content.pm.PackageParser\$Package")
            val packageUserStateClass = Class.forName("android.content.pm.PackageUserState")
            val generateApplicationInfoMethod = packageParserClass.getDeclaredMethod("generateApplicationInfo", packageParserPackageClass, Int::class.java, packageUserStateClass)

            val packageParser = packageParserClass.newInstance()
            val parsePackageMethod = packageParserClass.getDeclaredMethod("parsePackage", File::class.java, Int::class.java)

            val packageObj = parsePackageMethod.invoke(packageParser, apkFile, 0)

            val defaultPackageUserState = packageUserStateClass.newInstance()

            val applicationInfo = generateApplicationInfoMethod.invoke(packageParser, packageObj, 0, defaultPackageUserState) as ApplicationInfo
            val apkPath = apkFile.path

            applicationInfo.sourceDir = apkPath
            applicationInfo.publicSourceDir = apkPath
            return applicationInfo
        }
    }
}