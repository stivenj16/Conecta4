package com.example.conecta4.ui.theme

import ads_mobile_sdk.h4
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.example.conecta4.ui.theme.data.VocabularyRepository
import com.example.conecta4.ui.theme.model.GameState
import com.example.conecta4.ui.theme.model.Move
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun GameScreen(onVolverAlMenu: () -> Unit) {
    val gameState = remember { mutableStateOf(GameState()) }
    val vocabularyRepo = VocabularyRepository()
    val showDialog = remember { mutableStateOf(true) }
    val word = remember { mutableStateOf(vocabularyRepo.getRandomWord()) }
    val allowMove = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun processAIMove() {
        val validColumns = (0..6).filter { gameState.value.board[0][it] == 0 }
        val selectedColumn = validColumns.maxByOrNull { scoreColumn(gameState.value.board, it, 2) } ?: return
        dropDisc(gameState.value, selectedColumn, 2)
        gameState.value.totalMoves++
        if (checkVictory(gameState.value.board, 2)) {
            gameState.value = gameState.value.copy(isGameOver = true, winner = 2)
        } else if (gameState.value.totalMoves == 42) {
            gameState.value = gameState.value.copy(isGameOver = true, winner = 0)
        } else {
            gameState.value.currentPlayer = 1
            word.value = vocabularyRepo.getRandomWord()
            showDialog.value = true
            allowMove.value = false
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text("Conecta 4", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .background(Color(0xFF3366CC))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 0 until 6) {
                Row {
                    for (col in 0 until 7) {
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(colorForCell(gameState.value.board[row][col]))
                                .clickable(enabled = !gameState.value.isGameOver && allowMove.value && gameState.value.board[0][col] == 0 && gameState.value.currentPlayer == 1) {
                                    if (dropDisc(gameState.value, col, 1)) {
                                        gameState.value.totalMoves++
                                        if (checkVictory(gameState.value.board, 1)) {
                                            gameState.value = gameState.value.copy(isGameOver = true, winner = 1)
                                        } else if (gameState.value.totalMoves == 42) {
                                            gameState.value = gameState.value.copy(isGameOver = true, winner = 0)
                                        } else {
                                            gameState.value.currentPlayer = 2
                                            scope.launch {
                                                delay(1000)
                                                showDialog.value = false
                                                allowMove.value = true

                                                val colBot = getBestMove(gameState.value)
                                                if (colBot != -1 && dropDisc(gameState.value, colBot, 2)) {
                                                    gameState.value.totalMoves++
                                                    if (checkVictory(gameState.value.board, 2)) {
                                                        gameState.value = gameState.value.copy(isGameOver = true, winner = 2)
                                                    } else if (gameState.value.totalMoves == 42) {
                                                        gameState.value = gameState.value.copy(isGameOver = true, winner = 0)
                                                    } else {
                                                        gameState.value.currentPlayer = 1
                                                        word.value = vocabularyRepo.getRandomWord()
                                                        showDialog.value = true
                                                        allowMove.value = false
                                                    }
                                                }
                                            }

                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (gameState.value.isGameOver) {
            if (gameState.value.winner == 0) {
                Text("Empate", color = Color.Blue)
            } else {
                Text("Ganador: Jugador ${gameState.value.winner}", color = Color.Green)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    gameState.value = GameState()
                    word.value = vocabularyRepo.getRandomWord()
                    showDialog.value = true
                    allowMove.value = false
                }) {
                    Text("Reiniciar")
                }

                Button(onClick = {
                    // Volver al menú (usa una lambda de navegación que pasaremos)
                    onVolverAlMenu()
                }) {
                    Text("Volver")
                }
            }
        }


        if (showDialog.value && gameState.value.currentPlayer == 1) {
            VocabularyDialog(word.value) { isCorrect ->
                showDialog.value = false
                allowMove.value = isCorrect
                if (!isCorrect) {
                    gameState.value.currentPlayer = 2
                    scope.launch {
                        delay(500)
                        processAIMove()
                    }
                }
            }
        }
    }
}

fun getBestMove(state: GameState): Int {
    val board = state.board

    // 1. Ver si la máquina puede ganar en el siguiente turno
    for (col in 0..6) {
        val tempBoard = board.map { it.clone() }.toTypedArray()
        if (dropDiscTemp(tempBoard, col, 2) && checkVictory(tempBoard, 2)) return col
    }

    // 2. Bloquear al jugador si va a ganar
    for (col in 0..6) {
        val tempBoard = board.map { it.clone() }.toTypedArray()
        if (dropDiscTemp(tempBoard, col, 1) && checkVictory(tempBoard, 1)) return col
    }

    // 3. Priorizar columnas centrales
    val preferredOrder = listOf(3, 2, 4, 1, 5, 0, 6)
    for (col in preferredOrder) {
        if (board[0][col] == 0) return col
    }

    // 4. Si no hay movimientos posibles
    return -1
}

// Versión temporal de dropDisc para simular movimientos
fun dropDiscTemp(board: Array<Array<Int>>, column: Int, player: Int): Boolean {
    for (row in 5 downTo 0) {
        if (board[row][column] == 0) {
            board[row][column] = player
            return true
        }
    }
    return false
}


fun scoreColumn(board: Array<Array<Int>>, column: Int, player: Int): Int {
    val tempBoard = board.map { it.copyOf() }.toTypedArray()
    for (row in 5 downTo 0) {
        if (tempBoard[row][column] == 0) {
            tempBoard[row][column] = player
            break
        }
    }
    return countConnections(tempBoard, player)
}

fun countConnections(board: Array<Array<Int>>, player: Int): Int {
    var score = 0
    for (r in 0..5) {
        for (c in 0..3) {
            if ((0..3).count { board[r][c + it] == player } >= 2) score++
        }
    }
    for (r in 0..2) {
        for (c in 0..6) {
            if ((0..3).count { board[r + it][c] == player } >= 2) score++
        }
    }
    return score
}

fun colorForCell(value: Int): Color = when (value) {
    1 -> Color.Red
    2 -> Color.Yellow
    else -> Color.LightGray
}

fun dropDisc(state: GameState, column: Int, player: Int): Boolean {
    for (row in 5 downTo 0) {
        if (state.board[row][column] == 0) {
            state.board[row][column] = player
            return true
        }
    }
    return false
}

fun checkVictory(board: Array<Array<Int>>, player: Int): Boolean {
    for (r in 0..5) {
        for (c in 0..3) {
            if ((0..3).all { board[r][c + it] == player }) return true
        }
    }
    for (r in 0..2) {
        for (c in 0..6) {
            if ((0..3).all { board[r + it][c] == player }) return true
        }
    }
    for (r in 0..2) {
        for (c in 0..3) {
            if ((0..3).all { board[r + it][c + it] == player }) return true
        }
    }
    for (r in 3..5) {
        for (c in 0..3) {
            if ((0..3).all { board[r - it][c + it] == player }) return true
        }
    }
    return false
}


