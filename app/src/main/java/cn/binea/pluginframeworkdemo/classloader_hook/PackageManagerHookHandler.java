package cn.binea.pluginframeworkdemo.classloader_hook;

import android.content.pm.PackageInfo;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

import cn.binea.pluginframeworkdemo.InvocationHandlerBase;

/**
 * Created by binea on 6/10/2017.
 */

public class PackageManagerHookHandler extends InvocationHandlerBase {

    public PackageManagerHookHandler(@Nullable Object base) {
        super(base);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("getPackageInfo")) {
            return new PackageInfo();
        }

        return method.invoke(getBase(), args);
    }
}
