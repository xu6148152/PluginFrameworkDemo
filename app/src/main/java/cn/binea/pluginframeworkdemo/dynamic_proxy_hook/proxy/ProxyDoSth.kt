package cn.binea.pluginframeworkdemo.dynamic_proxy_hook.proxy

/**
 * Created by binea on 2/10/2017.
 */
class ProxyDoSth(val base: DoSth) : DoSth {

    override fun doSomething(sth: Long): Array<Any> {
        println("ProxyDoSth " + (sth * 10))
        val sth = base.doSomething(sth)
        sth[0] = "d"
        return sth
    }
}