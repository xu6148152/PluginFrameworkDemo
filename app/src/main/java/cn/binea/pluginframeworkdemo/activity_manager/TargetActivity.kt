package cn.binea.pluginframeworkdemo.activity_manager

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView

/**
 * Created by binea on 5/10/2017.
 */
@SuppressLint("SetTextI18n")
class TargetActivity : FragmentActivity() {

    companion object {
        val TAG = TargetActivity::class.java.canonicalName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        val tv = TextView(this)
        tv.text = "start successfully"
        setContentView(tv)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStartCommand")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}