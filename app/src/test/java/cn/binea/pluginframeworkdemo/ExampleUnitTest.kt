package cn.binea.pluginframeworkdemo

import cn.binea.pluginframeworkdemo.dynamic_proxy_hook.dynamic_proxy.InvocationHandlerImpl
import cn.binea.pluginframeworkdemo.dynamic_proxy_hook.proxy.DoSth
import cn.binea.pluginframeworkdemo.dynamic_proxy_hook.proxy.DoSthImpl
import cn.binea.pluginframeworkdemo.dynamic_proxy_hook.proxy.ProxyDoSth
import org.junit.Test

import org.junit.Assert.*
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_proxy() {
        val sthImpl = DoSthImpl()
//        val proxyDoSth = ProxyDoSth(sthImpl)
//        println(Arrays.toString(proxyDoSth.doSomething(10)))
        val result = Proxy.newProxyInstance(DoSth::class.java.classLoader, sthImpl.javaClass.interfaces, InvocationHandlerImpl(sthImpl))
        val ds = (result as DoSth).doSomething(10)
        println(Arrays.toString(ds))
    }
}
