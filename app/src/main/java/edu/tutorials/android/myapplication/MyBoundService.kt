package edu.tutorials.android.myapplication

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MyBoundService : Service() {
    private val binder = MyServiceBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun doSomething() {
        Log.i(LOG_TAG, "doSomething: Doing Something in service.")
    }

    inner class MyServiceBinder : Binder() {
        fun getService() = this@MyBoundService
    }
}