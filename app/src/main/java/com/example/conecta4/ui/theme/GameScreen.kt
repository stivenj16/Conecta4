package com.example.conecta4.ui.theme

import ads_mobile_sdk.h4
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.conecta4.ui.theme.data.FirebaseService
import com.example.conecta4.ui.theme.data.VocabularyRepository
import com.example.conecta4.ui.theme.model.GameState
import com.example.conecta4.ui.theme.model.Move
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen() {
    val gameState = remember { mutableStateOf(GameState()) }
    val vocabularyRepo = VocabularyRepository()
    val showDialog = remember { mutableStateOf(true) }
    val word = remember { mutableStateOf(vocabularyRepo.getRandomWord()) }
    val allowMove = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().background(Color.White)) {
        Text("Conecta 4", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Tablero
        Box(
            modifier = Modifier
                .background(Color(0xFF3366CC), shape = RoundedCornerShape(8.dp)) // fondo azul
                .padding(8.dp)
        ) {
            Column {
                for (row in 0 until 6) {
                    Row {
                        for (col in 0 until 7) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(4.dp)
                                    .background(Color(0xFF3366CC)) // color del fondo
                                    .clickable(
                                        enabled = !gameState.value.isGameOver &&
                                                allowMove.value &&
                                                gameState.value.board[0][col] == 0
                                    ) {
                                        if (dropDisc(gameState.value, col, gameState.value.currentPlayer)) {
                                            gameState.value.totalMoves++
                                            if (checkVictory(gameState.value.board, gameState.value.currentPlayer)) {
                                                gameState.value = gameState.value.copy(
                                                    isGameOver = true,
                                                    winner = gameState.value.currentPlayer
                                                )
                                            } else if (gameState.value.totalMoves == 42) {
                                                gameState.value = gameState.value.copy(isGameOver = true, winner = 0)
                                            } else {
                                                gameState.value.currentPlayer = if (gameState.value.currentPlayer == 1) 2 else 1
                                                word.value = vocabularyRepo.getRandomWord()
                                                showDialog.value = true
                                                allowMove.value = false

                                                if (gameState.value.currentPlayer == 2) {
                                                    scope.launch {
                                                        delay(1000)
                                                        showDialog.value = false
                                                        allowMove.value = true
                                                        val colBot = (0..6).firstOrNull { gameState.value.board[0][it] == 0 } ?: return@launch
                                                        if (dropDisc(gameState.value, colBot, 2)) {
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
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(colorForCell(gameState.value.board[row][col]))
                                )
                            }
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
        }

        if (showDialog.value && gameState.value.currentPlayer == 1) {
            VocabularyDialog(word.value) { isCorrect ->
                showDialog.value = false
                allowMove.value = isCorrect
                if (!isCorrect) {
                    gameState.value.currentPlayer = 2
                    word.value = vocabularyRepo.getRandomWord()
                    showDialog.value = true
                }
            }
        }
    }
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


