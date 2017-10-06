package cn.binea.pluginframeworkdemo.activity_manager

import android.content.Intent
import android.os.Handler
import android.os.Message
import cn.binea.pluginframeworkdemo.AMSHookHelper
import java.lang.reflect.Field


/**
 * Created by binea on 5/10/2017.
 */
open class ActivityThreadHandlerCallbackKt(base: Handler?): Handler.Callback {

    var base = base

    get() {return field }
    set(value) {field = value}

    override fun handleMessage(message: Message): Boolean {

        when (message.what) {
            100 -> handleLaunchActivity(message)
        }
        return false
    }

    open fun handleLaunchActivity(message: Message) {
        val obj = message.obj

        var intentField: Field?
        try {
            intentField = obj.javaClass.getDeclaredField("intent")

            intentField!!.isAccessible = true
            val intent = intentField.get(obj) as Intent

            val target = intent.getParcelableExtra<Intent>(AMSHookHelper.EXTRA_TARGET_INTENT)
            if (target != null) {
                intent.component = target.component
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }
}