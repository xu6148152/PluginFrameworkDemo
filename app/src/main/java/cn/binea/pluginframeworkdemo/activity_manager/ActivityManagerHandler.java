package cn.binea.pluginframeworkdemo.activity_manager;

import android.content.ComponentName;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.lang.reflect.Method;

import cn.binea.pluginframeworkdemo.AMSHookHelper;
import cn.binea.pluginframeworkdemo.InvocationHandlerBase;
import cn.binea.pluginframeworkdemo.MyApp;
import cn.binea.pluginframeworkdemo.service_manager.ProxyService;
import cn.binea.pluginframeworkdemo.service_manager.ServiceDispatcher;


/**
 * Created by binea on 5/10/2017.
 */

public class ActivityManagerHandler extends InvocationHandlerBase {

    private static final String TAG = ActivityManagerHandler.class.getCanonicalName();

    public ActivityManagerHandler(Object base) {
        super(base);
        this.setBase(base);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if ("startActivity".equals(method.getName())) {
            interceptActivityAndService(objects, StubActivity.class.getName());
        } else if ("startService".equals(method.getName())) {
            interceptActivityAndService(objects, ProxyService.class.getName());
        } else if ("stopService".equals(method.getName())) {
            Pair<Integer, Intent> integerIntentPair = foundFirstIntentOfArgs(objects);
            if (integerIntentPair != null) {
                Intent rawIntent = integerIntentPair.second;
                if (!TextUtils.equals(rawIntent.getComponent().getPackageName(), MyApp.Companion.getContext().getPackageName())) {
                    return ServiceDispatcher.INSTANCE.stopService(rawIntent);
                }
            }
        }
        return method.invoke(this.getBase(), objects);
    }

    private void interceptActivityAndService(Object[] objects, String className) {
        Pair<Integer, Intent> integerIntentPair = foundFirstIntentOfArgs(objects);

        if (integerIntentPair != null) {
            Intent intent = (Intent) objects[integerIntentPair.first];

            Intent newIntent = new Intent();
            String stubPackage = MyApp.Companion.getContext().getPackageName();

            ComponentName componentName = new ComponentName(stubPackage, className);
            newIntent.setComponent(componentName);
            newIntent.putExtra(AMSHookHelper.INSTANCE.getEXTRA_TARGET_INTENT(), intent);

            objects[integerIntentPair.first] = newIntent;

            Log.d(TAG, "hook success");
        }

    }

    private Pair<Integer, Intent> foundFirstIntentOfArgs(Object... args) {
        int index = -1;

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Intent) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return null;
        }
        return Pair.create(index, (Intent) args[index]);
    }
}
