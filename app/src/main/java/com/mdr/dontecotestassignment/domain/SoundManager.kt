package com.mdr.dontecotestassignment.domain

import kotlinx.coroutines.CoroutineScope

interface SoundManager {
    suspend fun playLoop(
        sound1: Sound,
        sound2: Sound,
        crossFadeSeconds: Int,
        scope: CoroutineScope
    ): Boolean

    suspend fun stop()
}