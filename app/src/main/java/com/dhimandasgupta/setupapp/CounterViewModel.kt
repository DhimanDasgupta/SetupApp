package com.dhimandasgupta.setupapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CounterViewModel : ViewModel() {
    // Flow to determine the current Counter value.
    private var counterFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    // Flow to determine the current Clickable State.
    private var clickEnabledFlow: MutableStateFlow<Boolean> = MutableStateFlow(true)

    // Final UI State to be observed and UI should be updated accordingly.
    private val counterUIStateFlow = MutableStateFlow(CounterUIState.initialCounterUIState())
    val counterUIState = counterUIStateFlow.asStateFlow()

    init {
        counterFlow.combine(clickEnabledFlow) { counter, clickEnabled ->
            counterUIStateFlow.value = counterUIStateFlow.value.copy(
                counter = counter,
                clickEnabled = clickEnabled
            )
        }.launchIn(viewModelScope)
    }

    fun incrementCounter() {
        viewModelScope.launch {
            clickEnabledFlow.emit(false)
            delay(500)
            counterFlow.emit(++counterFlow.value)
            delay(500)
            clickEnabledFlow.emit(true)
        }
    }

    fun decrementCounter() {
        viewModelScope.launch {
            clickEnabledFlow.emit(false)
            delay(500)
            counterFlow.emit(--counterFlow.value)
            delay(500)
            clickEnabledFlow.emit(true)
        }
    }
}

data class CounterUIState(
    val counter: Int,
    val clickEnabled: Boolean
) {
    companion object {
        fun initialCounterUIState() = CounterUIState(counter = 0, clickEnabled = true)
    }
}