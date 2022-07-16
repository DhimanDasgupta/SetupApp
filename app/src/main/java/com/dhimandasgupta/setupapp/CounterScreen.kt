package com.dhimandasgupta.setupapp

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dhimandasgupta.setupapp.ui.theme.connectivityState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CounterScreen(
    counterViewModel: CounterViewModel = viewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val counterUIStateFlowLifecycleAware = remember(counterViewModel.counterUIState, lifecycleOwner) {
        counterViewModel.counterUIState.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }

    // UI State
    val counterUIState by counterUIStateFlowLifecycleAware.collectAsState(initial = CounterUIState.initialCounterUIState())

    var animateUp by rememberSaveable { mutableStateOf(true) }

    val incrementCounter = {
        counterViewModel.incrementCounter()
        animateUp = true
    }
    val decrementCounter = {
        counterViewModel.decrementCounter()
        animateUp = false
    }
    
    // Network State
    val networkState = connectivityState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background,
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = counterUIState.counter,
                transitionSpec = { incrementDecrementAnimation(increment = animateUp) }
            ) { targetCount ->
                Box(
                    modifier = Modifier
                        .sizeIn(
                            minWidth = 64.dp,
                            maxHeight = 64.dp,
                            maxWidth = 72.dp,
                            minHeight = 72.dp
                    ),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "$targetCount",
                        style = typography.displayLarge,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            val textModifier = Modifier
                .padding(16.dp)
                .wrapContentSize()

            Row(
                modifier = Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                TextButton(
                    onClick = { incrementCounter() },
                    modifier = textModifier,
                    enabled = counterUIState.clickEnabled && networkState.value == ConnectionState.Available
                ) {
                    Text(
                        text = "++",
                        style = typography.displayLarge.netWorkAwareTextStyle(networkState)
                    )
                }

                TextButton(
                    onClick = { decrementCounter() },
                    modifier = textModifier,
                    enabled = counterUIState.clickEnabled && networkState.value == ConnectionState.Available
                ) {
                    Text(
                        text = "--",
                        style = typography.displayLarge.netWorkAwareTextStyle(networkState)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun incrementDecrementAnimation(duration: Int = 500, increment: Boolean = true): ContentTransform {
    return (slideInVertically(animationSpec = tween(durationMillis = duration)) { height -> height }
                    + fadeIn(animationSpec = tween(durationMillis = duration)
    ) with slideOutVertically(animationSpec = tween(durationMillis = duration)) { height -> if (increment) -height else height }
                    + fadeOut(animationSpec = tween(durationMillis = duration)))
        .also { SizeTransform(clip = true) }
}

private fun TextStyle.netWorkAwareTextStyle(connectionState: State<ConnectionState>) = when (connectionState.value) {
    is ConnectionState.Available -> this.copy(textDecoration = TextDecoration.None)
    is ConnectionState.Unavailable -> this.copy(textDecoration = TextDecoration.Underline)
}