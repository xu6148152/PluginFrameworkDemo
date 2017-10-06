package cn.binea.pluginframeworkdemo.classloader_hook

import dalvik.system.DexClassLoader

/**
 * Created by binea on 6/10/2017.
 */
class CustomClassLoader(dexPath: String?, optimizedDirectory: String?, librarySearchPath: String?, parent: ClassLoader?) : DexClassLoader(dexPath, optimizedDirectory, librarySearchPath, parent) {
}