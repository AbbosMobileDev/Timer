package com.example.timer

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale")
@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    strokeWidth: Dp = 8.dp,
    tailAngle: Float = 20f
) {
    var timeLeft by remember { mutableStateOf(viewModel.timeLeft) }

    val animatedProgress = remember { androidx.compose.animation.core.Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    val targetProgress = viewModel.progress
    val isRunning = viewModel.isRunning

    LaunchedEffect(targetProgress, isRunning) {
        if (isRunning) {
            animatedProgress.animateTo(
                targetValue = targetProgress,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            )
        }
    }
    val minutes = (timeLeft / 1000) / 60
    val seconds = (timeLeft / 1000) % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val color = if (timeLeft <= 10_000L) Color.Red else Color(0xFF2196F3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(250.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = Color.DarkGray,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )

                val sweep = 360f * animatedProgress.value
                val startAngle = -90f

                if (animatedProgress.value >= 1f) {
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                    )
                } else if (sweep > 0f) {
                    val mainSweep = (sweep).coerceAtLeast(0f)
                    if (mainSweep > 0f) {
                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = mainSweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)
                        )
                    }

                    val tailStart = startAngle + mainSweep
                    val tailSweep = if (sweep < tailAngle) sweep else tailAngle

                    if (tailSweep > 0f) {
                        for (i in 0 until tailSweep.toInt()) {
                            val alpha = i / tailSweep
                            drawArc(
                                color = color.copy(alpha = 1f - alpha),
                                startAngle = tailStart + i,
                                sweepAngle = 1f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)
                            )
                        }
                    }
                }
            }

            Text(
                timeFormatted,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Button(
            modifier = Modifier.padding(20.dp),
            onClick = {
                if (!isRunning && timeLeft != 0L) {
                    viewModel.startTimer(
                        onTick = { updatedTime -> timeLeft = updatedTime },
                        onFinish = {
                            timeLeft = 0
                            coroutineScope.launch {
                                animatedProgress.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = LinearEasing
                                    )
                                )
                            }
                        }
                    )
                } else {
                    viewModel.resetTimer()
                    timeLeft = viewModel.timeLeft
                    coroutineScope.launch {
                        animatedProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                        )
                    }
                }
            }
        ) {
            Text(if (isRunning || timeLeft == 0L) "Reset" else "Start")
        }
    }
}
