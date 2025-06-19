package com.example.conecta4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.conecta4.ui.theme.Conecta4Theme
import com.example.conecta4.ui.theme.GameScreen
import com.example.conecta4.ui.theme.GameScreenOnline
import com.example.conecta4.ui.theme.LobbyScreen
import com.google.firebase.FirebaseApp
import com.example.conecta4.ui.theme.WaitingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            Conecta4Theme {
                var modoJuego by remember { mutableStateOf("") }
                var salaId by remember { mutableStateOf("") }
                var playerTag by remember { mutableStateOf("") }

                when (modoJuego) {
                    "" -> LobbyScreen { modo, idSala, tagJugador ->
                        modoJuego = modo
                        salaId = idSala
                        playerTag = tagJugador
                    }

                    "IA" -> GameScreen()

                    "ESPERA" -> WaitingScreen(
                        salaId = salaId,
                        uidJugador = playerTag, // Aquí estamos pasando el uid temporal
                        onStartGame = { modo, id, tag ->
                            modoJuego = modo
                            salaId = id
                            playerTag = tag
                        }
                    )

                    "ONLINE" -> GameScreenOnline(
                        salaId = salaId,
                        playerTag = playerTag
                    )
                }

            }
        }
    }
}

