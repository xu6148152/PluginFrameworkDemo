package cn.binea.pluginframeworkdemo.binder_hook;

import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by binea on 3/10/2017.
 */

public class BinderProxyHookHandler implements InvocationHandler {

    private final String TAG = BinderProxyHookHandler.class.getCanonicalName();
    private IBinder base;

    private Class<?> stub;
    private Class<?> iinterface;

    public BinderProxyHookHandler(IBinder base) {
        this.base = base;
        try {
            stub = Class.forName("android.content.IClipboard$Stub");
            iinterface = Class.forName("android.content.IClipboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if("queryLocalInterface".equals(method.getName())) {
            Log.d(TAG, "hook queryLocalInterface");
            return Proxy.newProxyInstance(proxy.getClass().getClassLoader(), new Class[]{iinterface}, new BinderHookHandlerKt(base, (Class<Object>) stub));
        }

        Log.d(TAG, "method: " + method.getName());
        return method.invoke(base, args);
    }
}
