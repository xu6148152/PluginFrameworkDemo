package cn.binea.pluginframeworkdemo.classloader_hook

import dalvik.system.DexClassLoader
import dalvik.system.DexFile
import java.io.File
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * Created by binea on 6/10/2017.
 */
class BaseDexClassLoaderHookHelper {

    companion object {
        @Throws(IllegalAccessException::class, NoSuchMethodException::class, IOException::class, InvocationTargetException::class, InstantiationException::class)
        fun patchClassLoader(cl: ClassLoader, apkFile: File, optDexFile: File) {
            val pathListField = DexClassLoader::class.java.getDeclaredField("pathList")
            pathListField.isAccessible = true
            val pathListObj = pathListField.get(cl)

            val dexElementArray = pathListObj.javaClass.getDeclaredField("dexElements")
            dexElementArray.isAccessible = true
            val dexElements = dexElementArray.get(pathListObj) as Array<*>

            val elementClass = dexElements.javaClass.componentType
            val newElements = Array<Any>(dexElements.size + 1, {
                elementClass
            })

            val constructor = elementClass.getConstructor(Field::class.java, Boolean::class.java, File::class.java, DexFile::class.java)
            val o = constructor.newInstance(apkFile, false, apkFile, DexFile.loadDex(apkFile.canonicalPath, optDexFile.absolutePath, 0))
            val toAddElementArray = arrayOf(o)
            System.arraycopy(dexElements, 0, newElements, 0, dexElements.size)
            System.arraycopy(toAddElementArray, 0, newElements, dexElements.size, toAddElementArray.size)

            dexElementArray.set(pathListObj, newElements)
        }
    }
}