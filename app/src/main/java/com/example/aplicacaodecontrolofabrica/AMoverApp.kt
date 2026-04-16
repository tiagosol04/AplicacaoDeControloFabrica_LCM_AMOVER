package com.example.aplicacaodecontrolofabrica

import android.app.Application
import com.example.aplicacaodecontrolofabrica.data.repository.ServiceLocator

class AMoverApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}