package com.example.conecta4.ui.theme.model

data class GameState(
    val board: Array<Array<Int>> = Array(6) { Array(7) { 0 } },
    var currentPlayer: Int = 1,
    var isGameOver: Boolean = false,
    var winner: Int = 0,
    var totalMoves: Int = 0
)