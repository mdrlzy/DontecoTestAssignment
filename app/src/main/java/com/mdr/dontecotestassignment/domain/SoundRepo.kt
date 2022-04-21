package com.mdr.dontecotestassignment.domain

interface SoundRepo {
    fun provideSound(uri: StringUri): Sound?
}