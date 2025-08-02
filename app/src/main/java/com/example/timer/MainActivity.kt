package com.example.timer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.timer.ui.theme.TimerTheme

class MainActivity : ComponentActivity() {
    private val viewModel = TimerViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimerTheme {
                TimerScreen(viewModel)
                }
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
     val viewModel = TimerViewModel()
    TimerTheme {
        TimerScreen(viewModel)
    }
}