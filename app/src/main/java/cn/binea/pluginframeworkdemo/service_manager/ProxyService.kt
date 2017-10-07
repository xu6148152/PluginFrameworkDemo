package cn.binea.pluginframeworkdemo.service_manager

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * Created by binea on 7/10/2017.
 */
class ProxyService : Service() {
    companion object {
        val TAG = ProxyService::class.java.canonicalName
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand() called with intent = [$intent], startId = [$startId]")
        ServiceDispatcher.onStartCommand(intent, flags, startId)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy() called")
        super.onDestroy()
    }
}