package cn.binea.pluginframeworkdemo.ams_pms_hook;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.Arrays;

import cn.binea.pluginframeworkdemo.InvocationHandlerBase;

public class HookHandler extends InvocationHandlerBase {

    private static final String TAG = "HookHandler";

    public HookHandler(Object base) {
        super(base);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Log.d(TAG, "hey, baby; you are hooked!!");
        Log.d(TAG, "method:" + method.getName() + " called with args:" + Arrays.toString(args));

        if ("getActivityInfo".equals(method.getName())) {

        }
        return method.invoke(getBase(), args);
    }
}
