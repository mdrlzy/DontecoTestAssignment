package com.mdr.dontecotestassignment.data

import android.content.Context
import android.media.MediaPlayer
import android.media.VolumeShaper
import androidx.core.net.toUri
import com.mdr.dontecotestassignment.domain.Sound
import com.mdr.dontecotestassignment.domain.SoundManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SoundManagerImpl(private val context: Context) : SoundManager {

    private var mediaPlayer1: MediaPlayer? = null
    private var mediaPlayer2: MediaPlayer? = null
    private var loopJob: Job? = null

    override suspend fun playLoop(
        sound1: Sound,
        sound2: Sound,
        crossFadeSeconds: Int,
        scope: CoroutineScope,
    ) = withContext(Dispatchers.IO) {
        mediaPlayer1?.release()
        mediaPlayer1 = MediaPlayer.create(context, sound1.uri.toUri())
        mediaPlayer2?.release()
        mediaPlayer2 = MediaPlayer.create(context, sound2.uri.toUri())

        val crossFadeMs = (crossFadeSeconds * 1000).toLong()

        if (mediaPlayer1!!.duration < crossFadeMs * 2 ||
            mediaPlayer2!!.duration < crossFadeMs * 2
        )
            return@withContext false

        loopJob = loop(sound1, sound2, crossFadeMs, scope)
        return@withContext true
    }

    private fun loop(
        sound1: Sound,
        sound2: Sound,
        crossFadeMs: Long,
        scope: CoroutineScope,
    ) = scope.launch(Dispatchers.IO) {
        val timeToWait1 = mediaPlayer1!!.duration - crossFadeMs
        val timeToWait2 = mediaPlayer2!!.duration - crossFadeMs
        while (isActive) {
            mediaPlayer1?.release()
            mediaPlayer1 = MediaPlayer.create(context, sound1.uri.toUri())
            fadeInOutAudioStart(mediaPlayer1!!, crossFadeMs)
            delay(timeToWait1)
            mediaPlayer2?.release()
            mediaPlayer2 = MediaPlayer.create(context, sound2.uri.toUri())
            fadeInOutAudioStart(mediaPlayer2!!, crossFadeMs)
            delay(timeToWait2)
        }
    }

    override suspend fun stop() = withContext(Dispatchers.IO) {
        loopJob?.cancelAndJoin()
        mediaPlayer1?.release()
        mediaPlayer1 = null
        mediaPlayer2?.release()
        mediaPlayer2 = null
    }

    private fun fadeInOutAudioStart(
        mediaPlayer: MediaPlayer,
        crossFadeMs: Long
    ) {
        val duration = mediaPlayer.duration.toLong()
        val fadeInEnd = crossFadeMs.toFloat() / duration.toFloat()
        val fadeOutStart =
            (duration.toFloat() - crossFadeMs.toFloat()) / duration.toFloat()
        val times = floatArrayOf(0f, fadeInEnd, fadeOutStart, 1f)
        val volumes = floatArrayOf(0f, 1f, 1f, 0f)

        val config = VolumeShaper.Configuration.Builder()
            .setDuration(mediaPlayer.duration.toLong())
            .setCurve(times, volumes)
            .setInterpolatorType(VolumeShaper.Configuration.INTERPOLATOR_TYPE_LINEAR)
            .build()
        val volumeShaper = mediaPlayer.createVolumeShaper(config)
        mediaPlayer.start()
        volumeShaper.apply(VolumeShaper.Operation.PLAY)
    }

}