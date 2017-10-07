package cn.binea.pluginframeworkdemo

import dalvik.system.DexClassLoader
import java.io.File
import java.io.IOException

/**
 * Created by binea on 6/10/2017.
 */
class CustomClassLoader(dexPath: String?, optimizedDirectory: String?, librarySearchPath: String?, parent: ClassLoader?) :
        DexClassLoader(dexPath, optimizedDirectory, librarySearchPath, parent) {
    companion object {
        @Throws(IOException::class)
        fun getPluginClassLoader(plugin: File, packageName: String): CustomClassLoader {
            return CustomClassLoader(plugin.path, CommonUtils.getPluginOptDexDir(packageName)!!.path, CommonUtils.getPluginLibDir(packageName)!!.path, MyApp.getContext()!!.classLoader)
        }
    }
}