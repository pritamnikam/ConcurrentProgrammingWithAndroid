package edu.tutorials.android.myapplication

import android.app.Activity
import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import androidx.core.app.JobIntentService
import java.net.URL
import java.nio.charset.Charset


private const val JOB_ACTION = "edu.tutorials.android.myapplication.action.JOB"
private const val EXTRA_FILE_URL = "edu.tutorials.android.myapplication.param.FILE_URL"

private const val JOB_ID = 1001

class MyJobIntentService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        when (intent.action) {
            JOB_ACTION -> {
                val fileName = intent.getStringExtra(EXTRA_FILE_URL)
                val receiver = intent.getParcelableExtra<ResultReceiver>(RECEIVER_KEY)
                handleAction(receiver, fileName)
            }
        }
    }
    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleAction(receiver: ResultReceiver?, fileName: String?) {
        val fileContents = URL(fileName).readText(Charset.defaultCharset())
        // Log.i(LOG_TAG, fileContents)

        val bundle = Bundle()
        bundle.putString(FILE_CONTENT_KEY, fileContents)
        receiver?.send(Activity.RESULT_OK, bundle)
    }

    companion object {

        fun startAction(context: Context, fileName: String, receiver: ResultReceiver) {
            val intent = Intent(context, MyIntentService::class.java).apply {
                action = JOB_ACTION
                putExtra(RECEIVER_KEY, receiver)
                putExtra(EXTRA_FILE_URL, fileName)
            }

            enqueueWork(context,
                MyJobIntentService::class.java,
                JOB_ID,
                intent)
        }
    }
}