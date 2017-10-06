package cn.binea.pluginframeworkdemo

import android.content.Context
import java.io.*

/**
 * Created by binea on 6/10/2017.
 */
object CommonUtils {
    var sBaseDir: File? = null

    fun extractAssets(context: Context, sourceName: String) {
        val am = context.assets
        var inputStream: InputStream? = null
        var fos: FileOutputStream? = null
        try {
            inputStream = am.open(sourceName)
            val extractFile = context.getFileStreamPath(sourceName)
            fos = FileOutputStream(extractFile)
            val buffer = ByteArray(1024, { 0 })
            var count = 0
            while (count > 0) {
                count = inputStream.read(buffer)
                fos.write(buffer, 0, count)
            }
            fos.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            closeSilently(inputStream)
            closeSilently(fos)
        }
    }

    fun closeSilently(closeable: Closeable?) {
        try {
            closeable!!.close()
        } catch (e: Throwable) {

        }
    }

    fun getPluginOptDexDir(packageName: String): File? {
        return enforceDirExists(File(getPluginBaseDir(packageName), "odex"))
    }

    fun getPluginLibDir(packageName: String): File? {
        return enforceDirExists(File(getPluginBaseDir(packageName), "lib"))
    }

    fun getPluginBaseDir(packageName: String): File {
        if (sBaseDir == null) {
            sBaseDir = MyApp.getContext()!!.getFileStreamPath("plugin")
            enforceDirExists(sBaseDir!!)
        }
        return enforceDirExists(File(sBaseDir, packageName))!!
    }

    private fun enforceDirExists(baseDir: File): File? {
        if (baseDir.exists()) {
            val ret = baseDir.mkdir()
            if (!ret) {
                throw RuntimeException("create dir " + baseDir + " failed")
            }
        }
        return baseDir
    }
}