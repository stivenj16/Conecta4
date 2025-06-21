package com.example.conecta4.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.conecta4.ui.theme.data.FirebaseGameService
import com.example.conecta4.ui.theme.data.VocabularyRepository
import com.google.firebase.database.GenericTypeIndicator

@Composable
fun GameScreenOnline(salaId: String, playerTag: String, onVolverAlMenu: () -> Unit) {
    val scope = rememberCoroutineScope()
    val repo = VocabularyRepository()

    val board = remember { mutableStateOf(List(6) { MutableList(7) { 0 } }) }
    val turnoActual = remember { mutableStateOf("player1") }
    val ganador = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val palabra = remember { mutableStateOf(repo.getRandomWord()) }
    val puedeJugar = remember { mutableStateOf(false) }

    // Escuchar la sala en Firebase
    LaunchedEffect(salaId) {
        FirebaseGameService.escucharSala(salaId) { snapshot ->
            val boardSnapshot = snapshot.child("board")
            val type = object : GenericTypeIndicator<List<List<Long>>>() {}
            val boardFirebase = boardSnapshot.getValue(type)

            if (boardFirebase != null) {
                board.value = boardFirebase.map { row -> row.map { it.toInt() }.toMutableList() }
            }

            turnoActual.value = snapshot.child("turn").getValue(String::class.java) ?: "player1"
            ganador.value = snapshot.child("winner").getValue(String::class.java) ?: ""

            if (turnoActual.value == playerTag && ganador.value == "") {
                palabra.value = repo.getRandomWord()
                showDialog.value = true
                puedeJugar.value = false
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text("Conecta 4: $playerTag", style = MaterialTheme.typography.headlineMedium)
        Text("Turno actual: ${turnoActual.value}", color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        // TABLERO ADAPTATIVO
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(7f / 6f)
                .background(Color(0xFF3366CC))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            val cellSize = maxWidth / 7

            Column {
                for (row in 0 until 6) {
                    Row {
                        for (col in 0 until 7) {
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(colorForCell(board.value[row][col]))
                                    .clickable(
                                        enabled = turnoActual.value == playerTag &&
                                                puedeJugar.value &&
                                                board.value[0][col] == 0
                                    ) {
                                        val newBoard = board.value.map { it.toMutableList() }.toMutableList()
                                        for (fila in 5 downTo 0) {
                                            if (newBoard[fila][col] == 0) {
                                                newBoard[fila][col] = if (playerTag == "player1") 1 else 2
                                                break
                                            }
                                        }
                                        FirebaseGameService.enviarJugada(
                                            salaId,
                                            newBoard,
                                            if (playerTag == "player1") "player2" else "player1"
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (ganador.value != "") {
            if (ganador.value == "empate") {
                Text("Empate", color = Color.Blue)
            } else {
                Text("Ganó: ${ganador.value}", color = Color.Green)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                FirebaseGameService.reiniciarSala(salaId)
            }) {
                Text("Reiniciar partida")
            }
            Button(onClick = {
                onVolverAlMenu()
            }) {
                Text("Volver")
            }
        }

        if (showDialog.value) {
            VocabularyDialog(palabra.value) { correcto ->
                showDialog.value = false
                puedeJugar.value = correcto
                if (!correcto) {
                    FirebaseGameService.enviarJugada(
                        salaId,
                        board.value,
                        if (playerTag == "player1") "player2" else "player1"
                    )
                }
            }
        }
    }
}

