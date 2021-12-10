package edu.tutorials.android.myapplication

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.util.Log
import java.net.URL
import java.nio.charset.Charset

private const val ACTION_INTENT = "edu.tutorials.android.myapplication.action.INTENT"
private const val EXTRA_PARAM = "edu.tutorials.android.myapplication.param.PARAM"


class MyIntentService : IntentService("MyIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_INTENT -> {
                val fileName = intent.getStringExtra(EXTRA_PARAM)
                handleAction(fileName)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleAction(fileName: String?) {
        val fileContents = URL(fileName).readText(Charset.defaultCharset())
        Log.i(LOG_TAG, fileContents)
    }

    companion object {

        @JvmStatic
        fun startAction(context: Context, fileName: String) {
            val intent = Intent(context, MyIntentService::class.java).apply {
                action = ACTION_INTENT
                putExtra(EXTRA_PARAM, fileName)
            }

            context.startService(intent)
        }
    }
}