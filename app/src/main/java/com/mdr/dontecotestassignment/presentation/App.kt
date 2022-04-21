package com.mdr.dontecotestassignment.presentation

import android.app.Application
import com.mdr.dontecotestassignment.di.KOIN_MODULES
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(KOIN_MODULES)
        }
    }
}