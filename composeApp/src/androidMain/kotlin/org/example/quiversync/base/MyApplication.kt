package org.example.quiversync.base

import android.app.Application
import com.cloudinary.android.MediaManager
import org.example.quiversync.di.initKoin
import org.example.quiversync.utils.AppConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@MyApplication)
        }

        //Cloudinary
        val config = mapOf(
            "cloud_name" to AppConfig.cloudName
        )
        MediaManager.init(this, config)
    }
}