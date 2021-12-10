package edu.tutorials.android.myapplication

import android.os.Bundle
import android.os.Message
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel : ViewModel() {
    val diceData = MutableLiveData<Pair<Int, Int>>()

    fun rollDice() {
        for (diceIndex in 0 until 5) {
            viewModelScope.launch {
                delay(diceIndex * 10L)
                for (j in 1..20) {
                    val number = getDiceValue()
                    diceData.value = Pair<Int, Int>(diceIndex, number)
                    delay(100)
                }
            }
        }
    }

    private fun getDiceValue(): Int {
        // will return an `Int` between 0 and 10 (incl.)
        // return (1..6).random()
        return Random.nextInt(1, 7)
    }
}