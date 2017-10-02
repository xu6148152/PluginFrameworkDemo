package cn.binea.pluginframeworkdemo.dynamic_proxy_hook.dynamic_proxy

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Created by binea on 2/10/2017.
 */
class InvocationHandlerImpl(val base: Any) : InvocationHandler {
    override fun invoke(p0: Any?, p1: Method?, p2: Array<out Any>?): Any {

        if ("doSomething" == (p1!!.name)) {
            val value: Long = p2!![0] as Long
            val doSthValue = value * 5
            println(doSthValue)
            val invoke: Array<Any> = p1.invoke(base, doSthValue) as Array<Any>
            invoke[0] = "d"
            return invoke
        }

        return Unit
    }
}