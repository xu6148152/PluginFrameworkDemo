package cn.binea.pluginframeworkdemo.receiver_manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.util.Log
import cn.binea.pluginframeworkdemo.CustomClassLoader
import java.io.File

/**
 * Created by binea on 7/10/2017.
 */
object ReceiverHelperKt {

    val TAG = ReceiverHelperKt::class.java.canonicalName

    private val sCache = HashMap<ActivityInfo, List<*>>()

    @Throws(Exception::class)
    private fun parseReceiver(apkFile: File) {
        val packageParserClass = Class.forName("android.content.pm.PackageParser")
        val parsePackageMethod = packageParserClass.getDeclaredMethod("parsePackage", File::class.java, Int::class.java)

        val packageParser = packageParserClass.newInstance()

        val packageObj = parsePackageMethod.invoke(packageParser, apkFile, PackageManager.GET_RECEIVERS)

        val receiversField = packageObj.javaClass.getDeclaredField("receivers")
        val receivers = receiversField.get(packageObj) as List<*>

        val packageParserActivityClass = Class.forName("android.content.pm.PackageParser\$Activity")
        val packageUserStateClass = Class.forName("android.content.pm.PackageUserState")
        val userHandler = Class.forName("android.os.UserHandle")
        val getCallingUserIdMethod = userHandler.getDeclaredMethod("getCallingUserId")
        val userId = getCallingUserIdMethod.invoke(null) as Int
        val defaultUserState = packageUserStateClass.newInstance()

        val componentClass = Class.forName("android.content.pm.PackageParser\$Component")
        val intentsField = componentClass.getDeclaredField("intents")

        val generateReceiverInfo = packageParserClass.getDeclaredMethod("generateActivityInfo", packageParserActivityClass, Int::class.java, packageUserStateClass, Int::class.java)

        for (receiver in receivers) {
            val info = generateReceiverInfo.invoke(packageParser, receiver, 0, defaultUserState, userId) as ActivityInfo
            val filters = intentsField.get(receiver) as List<*>
            sCache.put(info, filters)
        }
    }

    @Throws(Exception::class)
    fun preLoadReceiver(context: Context, apk: File) {
        parseReceiver(apk)

        var cl: ClassLoader? = null

        for (activityInfo in sCache.keys) {
            Log.d(TAG, "preload receiver: ${activityInfo.name}")
            val intentFilters = sCache.get(activityInfo) as List<*>
            if (cl == null) {
                cl = CustomClassLoader.getPluginClassLoader(apk, activityInfo.packageName)
            }

            for (intentFilter in intentFilters) {
                val receiver = cl.loadClass(activityInfo.name).newInstance() as BroadcastReceiver
                context.registerReceiver(receiver, intentFilter as IntentFilter)
            }
        }
    }
}