package fr.javatic.yafull.compose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.structuralEqualityPolicy

class ObservableMutableState<T>(private val state: MutableState<T>, private val listener: (T) -> Unit) : MutableState<T> {
    override var value: T
        get() = state.value
        set(value) {
            state.value = value
            listener(value)
        }

    override fun component1(): T = value
    override fun component2(): (T) -> Unit = { value = it }
}

fun <T> observableMutableStateOf(
    value: T,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
    listener: (T) -> Unit,
): ObservableMutableState<T> = ObservableMutableState(mutableStateOf(value, policy), listener)
