package com.mdr.dontecotestassignment.di

import com.mdr.dontecotestassignment.data.SoundManagerImpl
import com.mdr.dontecotestassignment.data.SoundRepoImpl
import com.mdr.dontecotestassignment.domain.SoundManager
import com.mdr.dontecotestassignment.domain.SoundRepo
import com.mdr.dontecotestassignment.presentation.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val KOIN_MODULES by lazy {
    listOf(soundModule, viewModelsModule)
}

private val viewModelsModule = module {
    viewModel { MainViewModel(get(), get()) }
}

private val soundModule = module {
    single<SoundManager> { SoundManagerImpl(get()) }
    single<SoundRepo> { SoundRepoImpl(get()) }
}