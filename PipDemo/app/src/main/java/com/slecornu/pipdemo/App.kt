package com.slecornu.pipdemo

import android.app.Application
import android.os.Build
import android.os.StrictMode

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            val options = StrictMode.VmPolicy.Builder()
                .penaltyLog()
                .detectBlockedBackgroundActivityLaunch()
                .build()
            StrictMode.setVmPolicy(options)
        }
    }
}