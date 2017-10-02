package cn.binea.pluginframeworkdemo.dynamic_proxy_hook.proxy

/**
 * Created by binea on 2/10/2017.
 */
class DoSthImpl : DoSth {

    override fun doSomething(sth: Long): Array<Any> {
        println("DoSthImpl " + sth)
        return arrayOf("a", "b", "c");
    }
}