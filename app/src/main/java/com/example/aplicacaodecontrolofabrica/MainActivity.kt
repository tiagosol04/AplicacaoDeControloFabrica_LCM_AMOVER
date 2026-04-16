package com.example.aplicacaodecontrolofabrica

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.aplicacaodecontrolofabrica.app.AppNavigation
import com.example.aplicacaodecontrolofabrica.ui.theme.AplicacaoDeControloFabricaTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AplicacaoDeControloFabricaTheme {
                AppNavigation()
            }
        }
    }
}