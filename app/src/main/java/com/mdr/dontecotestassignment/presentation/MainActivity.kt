package com.mdr.dontecotestassignment.presentation

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mdr.dontecotestassignment.R
import com.mdr.dontecotestassignment.databinding.ActivityMainBinding
import androidx.activity.result.contract.ActivityResultContracts
import org.koin.androidx.viewmodel.ext.android.viewModel

import org.orbitmvi.orbit.viewmodel.observe

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding by viewBinding(ActivityMainBinding::bind)
    private val viewModel: MainViewModel by viewModel()

    private val pickSound1 =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri ?: return@registerForActivityResult
            viewModel.onSound1Picked(uri.toString())
        }

    private val pickSound2 =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri ?: return@registerForActivityResult
            viewModel.onSound2Picked(uri.toString())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUi()
        viewModel.observe(this@MainActivity, ::render, ::handleSideEffect)
    }

    private fun initUi() = with(binding) {
        slider.addOnChangeListener { slider, value, fromUser ->
            if (!fromUser) return@addOnChangeListener
            viewModel.onCrossFadeSecondsChange(value.toInt())
        }
        btnPickSound1.setOnClickListener {
            viewModel.onSound1PickBtnClick()
        }
        btnPickSound2.setOnClickListener {
            viewModel.onSound2PickBtnClick()
        }
        ivPlayStop.setOnClickListener {
            viewModel.onPlayPauseBtnClick()
        }
    }

    private fun render(state: MainState) = binding.apply {
        slider.value = state.crossFadeSeconds.toFloat()
        tvCrossfadeSeconds.text =
            getString(R.string.crossfade_seconds, state.crossFadeSeconds.toString())
        tvSound1File.text = state.sound1?.fileName ?: ""
        tvSound2File.text = state.sound2?.fileName ?: ""
        ivPlayStop.setImageResource(
            if (state.isPlaying)
                R.drawable.ic_pause_circle
            else
                R.drawable.ic_play_circle
        )

    }

    private fun handleSideEffect(effect: MainSideEffect) = when (effect) {
        MainSideEffect.PickSound1 -> pickSound1.launch(AUDIO_MIME_TYPE)
        MainSideEffect.PickSound2 -> pickSound2.launch(AUDIO_MIME_TYPE)
        MainSideEffect.ToastEmptySound1 -> Toast.makeText(
            this,
            getString(R.string.choose_first_sound),
            Toast.LENGTH_SHORT
        ).show()
        MainSideEffect.ToastEmptySound2 -> Toast.makeText(
            this,
            getString(R.string.choose_second_sound),
            Toast.LENGTH_SHORT
        ).show()
        MainSideEffect.ToastAudioDurationTooShort -> Toast.makeText(
            this,
            getString(R.string.audio_duration_too_short),
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        private const val AUDIO_MIME_TYPE = "audio/*"
    }
}