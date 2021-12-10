package edu.tutorials.android.myapplication

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import kotlinx.coroutines.*
import java.net.URL
import java.nio.charset.Charset
import java.sql.Connection
import kotlin.concurrent.thread

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModelClass
    private lateinit var diceModel: MainViewModel

    public final val  DICE_INDEX_KEY = "DICE_INDEX_KEY"
    public final val  DICE_VALUE_KEY = "DICE_VALUE_KEY"

    private lateinit var myService: MyBoundService

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.i(LOG_TAG, "onServiceConnected: connecting to the service")
            val binder = p1 as MyBoundService.MyServiceBinder
            myService = binder.getService()

            // deferred run post service connection.
            runBoundService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }
    }


    //@regionstart handler
    private val handler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // super.handleMessage(msg)
            val bundle = msg.data
            val diceIndex = bundle.getInt(DICE_INDEX_KEY)
            val diceValue = bundle.getInt(DICE_VALUE_KEY)

            Log.i(LOG_TAG,
                "diceIndex: $diceIndex, diceValue: $diceValue")
        }
    }
    //@regionend handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()

//        run()
//        rollDice()
//        runCode()
//        cancelJob()
//        runBoundService()
    }

    private fun runBoundService() {
        CoroutineScope(Dispatchers.Main).launch {
            myService.doSomething()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(LOG_TAG, "onStart: Start bound service.")
        Intent(this, MyBoundService::class.java).also {
            bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.i(LOG_TAG, "onStop: Stop the bound service.")
        unbindService(connection)
    }

    private fun initViewModel() {
        // init viewmodel
        viewModel = ViewModelProvider(this).get(ViewModelClass::class.java)

        // livedata observer
        viewModel.myData.observe(this, Observer {
            Log.i(LOG_TAG, it)
        })

        diceModel = ViewModelProvider(this).get(MainViewModel::class.java)
        diceModel.diceData.observe(this, Observer {
            Log.i(LOG_TAG, "diceIndex: ${it.first}, diceValue: ${it.second}")
        })
    }

    private fun cancelJob() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.cancelWork()
        }
    }

    private fun runCode() {
        /*
        CoroutineScope(Dispatchers.Main).launch {
            val result = fetchSomething()
            Log.i("MainActivity", result ?: "Null")
        }
        */

        runViewModel()
        runIntentService()
        runWorkManager()
        runBoundService()
    }

    private fun runViewModel() {
        // viewModel.doWork()
        // diceModel.rollDice()
    }

    private fun runIntentService() {
        // MyIntentService.startAction(this, FILE_URL)

        // val receiver = MyResultsReceiver(Handler())
        // MyJobIntentService.startAction(this, FILE_URL, receiver)
    }

    private fun runWorkManager() {
        // MyWorker
        // val workRequest = OneTimeWorkRequestBuilder<MyWorker>().build()
        // WorkManager.getInstance(applicationContext).enqueue(workRequest)

        // worker with constrain
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<MyWorker>()
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)

        // work manager with live data
        val workManger = WorkManager.getInstance(applicationContext)
        workManger.enqueue(workRequest)
        workManger.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this, Observer {
                if (it.state == WorkInfo.State.SUCCEEDED) {
                    val result = it.outputData.getString(DATA_KEY)
                    Log.i(LOG_TAG, result?: "Null")
                }
            })
    }

    private fun rollDice() {
        for (i in 1..5) {
            thread(start = true) {
                Thread.sleep(10L * i)
                val bundle = Bundle()
                bundle.putInt(DICE_INDEX_KEY, i)
                for (j in 1..20) {
                    bundle.putInt(DICE_VALUE_KEY, getDiceValue())
                    Message().also {
                        it.data = bundle
                        handler.sendMessage(it)
                    }

                    Thread.sleep(100)
                }
            }
        }
    }

    private fun getDiceValue(): Int {
        // will return an `Int` between 0 and 10 (incl.)
        return (1..6).random()
    }

    private fun run() {
        Handler().post { Log.i(LOG_TAG, "Operation from runnable"); }
        Handler().postDelayed({ Log.i(LOG_TAG, "Operation from runnable" ); }, 1000)

        Log.i(LOG_TAG, "Synchronous Operation 1");
        Log.i(LOG_TAG, "Synchronous Operation 2");
        Log.i(LOG_TAG, "Synchronous Operation 3")
    }

    private suspend fun fetchSomething() : String? {
        delay(2000)
        return "something from web"
    }

    private inner class MyResultsReceiver(handler: Handler) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            if (resultCode == Activity.RESULT_OK) {
                val fileContents = resultData?.get(FILE_CONTENT_KEY) ?: "Null"
                Log.i(LOG_TAG, fileContents as String)
            }
        }
    }

}