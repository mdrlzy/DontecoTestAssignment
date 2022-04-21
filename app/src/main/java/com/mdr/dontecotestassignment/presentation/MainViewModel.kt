package com.mdr.dontecotestassignment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdr.dontecotestassignment.domain.Sound
import com.mdr.dontecotestassignment.domain.SoundManager
import com.mdr.dontecotestassignment.domain.SoundRepo
import com.mdr.dontecotestassignment.domain.StringUri
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

data class MainState(
    val crossFadeSeconds: Int = 2,
    val sound1: Sound? = null,
    val sound2: Sound? = null,
    val isPlaying: Boolean = false
)

sealed class MainSideEffect {
    object PickSound1 : MainSideEffect()
    object PickSound2 : MainSideEffect()
    object ToastEmptySound1 : MainSideEffect()
    object ToastEmptySound2 : MainSideEffect()
    object ToastAudioDurationTooShort : MainSideEffect()
}

class MainViewModel(
    private val soundRepo: SoundRepo,
    private val soundManager: SoundManager
) : ViewModel(), ContainerHost<MainState, MainSideEffect> {
    override val container: Container<MainState, MainSideEffect> =
        container(MainState())

    fun onSound1PickBtnClick() = intent {
        postSideEffect(MainSideEffect.PickSound1)
    }

    fun onSound1Picked(uri: StringUri) = intent {
        val sound1 = soundRepo.provideSound(uri)
        reduce {
            state.copy(sound1 = sound1)
        }
    }

    fun onSound2PickBtnClick() = intent {
        postSideEffect(MainSideEffect.PickSound2)
    }

    fun onSound2Picked(uri: StringUri) = intent {
        val sound2 = soundRepo.provideSound(uri)
        reduce {
            state.copy(sound2 = sound2)
        }
    }

    fun onCrossFadeSecondsChange(seconds: Int) = intent {
        reduce {
            state.copy(crossFadeSeconds = seconds)
        }
    }

    fun onPlayPauseBtnClick() = intent {
        if (state.isPlaying)
            onPause()
        else
            onPlay()
    }

    private fun onPlay() = intent {
        state.sound1 ?: let {
            postSideEffect(MainSideEffect.ToastEmptySound1)
            return@intent
        }
        state.sound2 ?: let {
            postSideEffect(MainSideEffect.ToastEmptySound2)
            return@intent
        }

        val isSuccessLaunch = soundManager.playLoop(
            state.sound1!!,
            state.sound2!!,
            state.crossFadeSeconds,
            viewModelScope
        )
        if (isSuccessLaunch) {
            reduce {
                state.copy(isPlaying = true)
            }
        } else {
            postSideEffect(MainSideEffect.ToastAudioDurationTooShort)
        }
    }

    private fun onPause() = intent {
        soundManager.stop()
        reduce {
            state.copy(isPlaying = false)
        }
    }
}