package com.example.conecta4.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.conecta4.ui.theme.data.FirebaseGameService
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


@Composable
fun WaitingScreen(
    salaId: String,
    uidJugador: String,
    onStartGame: (String, String, String) -> Unit
) {
    var jugador2Listo by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Escuchar si player2 se ha unido
    LaunchedEffect(true) {
        FirebaseGameService.escucharSala(salaId) { snapshot ->
            val player2 = snapshot.child("players").child("player2").getValue(String::class.java)
            if (!player2.isNullOrBlank() && !jugador2Listo) {
                jugador2Listo = true

                scope.launch {
                    delay(1000)
                    onStartGame("ONLINE", salaId, "player1")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Código de sala: $salaId", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Esperando a que otro jugador se una...")
        if (jugador2Listo) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Jugador 2 conectado. Iniciando partida...", color = MaterialTheme.colorScheme.primary)
        }
    }
}

