package com.example.getfitness

import android.app.Application
import com.example.getfitness.di.authModule
import com.example.getfitness.di.feedModule
import com.example.getfitness.di.formModule
import com.example.getfitness.di.trainingModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)

            modules(
                authModule,
                trainingModule,
                feedModule,
                formModule,
                )
        }
    }
}