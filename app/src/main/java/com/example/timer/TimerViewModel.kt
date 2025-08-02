package com.example.timer


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel(
    private val totalTime: Long = 60_000L
) : ViewModel() {
    private var timerJob: Job? = null
    var timeLeft: Long = totalTime
        private set
    var isRunning: Boolean = false
        private set
    val progress: Float
        get() = timeLeft / totalTime.toFloat()

    fun startTimer(onTick: (Long) -> Unit, onFinish: () -> Unit) {
        if (timeLeft <= 0L) {
            timeLeft = totalTime
        }

        if (isRunning) return

        isRunning = true
        timerJob = viewModelScope.launch {
            while (timeLeft > 0 && isRunning) {
                delay(16) // ~60 FPS (16ms)
                timeLeft = (timeLeft - 16).coerceAtLeast(0L)
                onTick(timeLeft)
            }
            if (timeLeft <= 0) {
                isRunning = false
                onFinish()
            }
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        timeLeft = totalTime
        isRunning = false
    }
}
