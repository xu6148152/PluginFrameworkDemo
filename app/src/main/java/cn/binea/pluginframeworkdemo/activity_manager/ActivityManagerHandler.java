package cn.binea.pluginframeworkdemo.activity_manager;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * Created by binea on 5/10/2017.
 */

public class ActivityManagerHandler implements InvocationHandler {

    private static final String TAG = ActivityManagerHandler.class.getCanonicalName();

    private Object base;

    public ActivityManagerHandler(Object base) {
        this.base = base;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if ("startActivity".equals(method.getName())) {
            int index = -1;

            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                Intent intent = (Intent) objects[index];

                Intent newIntent = new Intent();
                String stubPackage = "cn.binea.pluginframeworkdemo";

                ComponentName componentName = new ComponentName(stubPackage, StubActivity.class.getName());
                newIntent.setComponent(componentName);
                newIntent.putExtra(AMSHookHelper.INSTANCE.getEXTRA_TARGET_INTENT(), intent);

                objects[index] = newIntent;

                Log.d(TAG, "hook success");
            }
        }
        return method.invoke(base, objects);
    }
}
