package cn.binea.pluginframeworkdemo

import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import cn.binea.pluginframeworkdemo.activity_manager.TargetActivity
import cn.binea.pluginframeworkdemo.receiver_manager.ReceiverHelperKt


class MainActivity : AppCompatActivity() {

    internal val ACTION = "com.weishu.upf.demo.app2.PLUGIN_ACTION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        sample_text.text = stringFromJNI()

        // 注册插件收到我们发送的广播之后, 回传的广播
        registerReceiver(mReceiver, IntentFilter(ACTION))
    }

    internal var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(context, "插件插件,我是主程序,握手完成!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    fun testActivity(view: View) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse("http://www.baidu.com")
        applicationContext.startActivity(intent)
    }

    fun testPMS(view: View) {
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    }

    fun startTargetActivity(view: View) {
        val intent = Intent(this, TargetActivity::class.java)
        startActivity(intent)
    }

    fun sendBroad2Plugin(view: View) {
        Toast.makeText(applicationContext, "插件插件!收到请回答!!", Toast.LENGTH_SHORT).show()
        sendBroadcast(Intent("com.weishu.upf.demo.app2.Receiver1"))
    }

    fun startService1(view: View) {
        startService(Intent().setComponent(ComponentName("com.weishu.upf.demo.app2", "com.weishu.upf.demo.app2.TargetService1")))
    }

    fun startService2(view: View) {
        startService(Intent().setComponent(ComponentName("com.weishu.upf.demo.app2", "com.weishu.upf.demo.app2.TargetService2")))
    }

    fun stopService1(view: View) {
        stopService(Intent().setComponent(ComponentName("com.weishu.upf.demo.app2", "com.weishu.upf.demo.app2.TargetService1")))
    }

    fun stopService2(view: View) {
        stopService(Intent().setComponent(ComponentName("com.weishu.upf.demo.app2", "com.weishu.upf.demo.app2.TargetService2")))
    }
}
