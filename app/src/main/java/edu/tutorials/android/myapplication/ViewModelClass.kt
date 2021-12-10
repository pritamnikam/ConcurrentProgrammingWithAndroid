package edu.tutorials.android.myapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.net.URL
import java.nio.charset.Charset

class ViewModelClass : ViewModel() {
    val myData = MutableLiveData<String>()

    lateinit var job: Job

    fun doWork() {
        job = viewModelScope.launch {
            myData.value = fetchSomething();
        }
    }

    fun cancelWork() {
        try {
            job.cancel()
            myData.value = "Job cancelled"
        } catch (e: UninitializedPropertyAccessException) {
        }
    }

    private suspend fun fetchSomething() : String? {
        return withContext(Dispatchers.IO) {
            return@withContext URL(FILE_URL).readText(Charset.defaultCharset())
        }
    }
}