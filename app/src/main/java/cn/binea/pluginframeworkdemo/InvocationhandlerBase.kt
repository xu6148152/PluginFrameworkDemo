package cn.binea.pluginframeworkdemo

import java.lang.reflect.InvocationHandler

/**
 * Created by binea on 6/10/2017.
 */
abstract class InvocationhandlerBase(base: Any?) : InvocationHandler {
    var base = base

    get() {
        return field
    }

    set(value) {
        field = value
    }
}