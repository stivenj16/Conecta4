package com.example.conecta4.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.conecta4.ui.theme.data.FirebaseGameService
import java.util.*

@Composable
fun LobbyScreen(onModoSeleccionado: (String, String, String) -> Unit) {
    var codigoSalaCreada by remember { mutableStateOf<String?>(null) }
    var inputSalaId by remember { mutableStateOf("") }
    val uidJugador = remember { "jugador_" + UUID.randomUUID().toString().take(5) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Selecciona el modo de juego", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        // 🔵 Jugar contra IA
        Button(onClick = {
            onModoSeleccionado("IA", "", "")
        }) {
            Text("Jugador vs IA")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🟢 Crear sala
        Button(onClick = {
            val salaId = UUID.randomUUID().toString().take(6)
            FirebaseGameService.crearSalaCompleta(salaId, uidJugador)
            codigoSalaCreada = salaId
        }) {
            Text("Crear sala multijugador")
        }

        // Mostrar código y botón para continuar
        if (codigoSalaCreada != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Código de sala: ${codigoSalaCreada}")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                onModoSeleccionado("ESPERA", codigoSalaCreada!!, uidJugador)
            }) {
                Text("Esperar al jugador 2")
            }
        }


        Spacer(modifier = Modifier.height(32.dp))

        // 🟡 Unirse a sala existente
        OutlinedTextField(
            value = inputSalaId,
            onValueChange = { inputSalaId = it },
            label = { Text("Código de sala") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            enabled = inputSalaId.isNotBlank(),
            onClick = {
                FirebaseGameService.unirseASala(inputSalaId, uidJugador)
                onModoSeleccionado("ONLINE", inputSalaId, "player2")
            }
        ) {
            Text("Unirse a sala")
        }
    }
}



