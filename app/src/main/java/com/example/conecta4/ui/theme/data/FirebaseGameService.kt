package com.example.conecta4.ui.theme.data

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object FirebaseGameService {

    private val db = FirebaseDatabase.getInstance().getReference("games")

    fun crearSalaCompleta(salaId: String, uidPlayer1: String) {
        val board = List(6) { List(7) { 0 } }

        val sala = mapOf(
            "board" to board,
            "turn" to "player1",
            "winner" to "",
            "players" to mapOf(
                "player1" to uidPlayer1,
                "player2" to "" // aún no se une
            ),
            "vocabulary" to mapOf(
                "currentWord" to "apple",
                "expected" to "manzana"
            )
        )

        db.child(salaId).setValue(sala)
    }

    fun unirseASala(salaId: String, uid: String) {
        db.child(salaId).child("players").child("player2").setValue(uid)
    }

    fun escucharSala(salaId: String, onData: (DataSnapshot) -> Unit) {
        db.child(salaId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onData(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun enviarJugada(salaId: String, board: List<List<Int>>, nextTurn: String) {
        val ref = db.child(salaId)
        ref.child("board").setValue(board)
        ref.child("turn").setValue(nextTurn)

        val ganador = checkGanador(board)
        if (ganador != 0) {
            ref.child("winner").setValue("player$ganador")
        }

        val totalMovs = board.sumOf { row -> row.count { it != 0 } }
        if (totalMovs == 42 && ganador == 0) {
            ref.child("winner").setValue("empate")
        }
    }

    fun reiniciarSala(salaId: String) {
        val nuevoTablero = List(6) { List(7) { 0 } }
        db.child(salaId).child("board").setValue(nuevoTablero)
        db.child(salaId).child("turn").setValue("player1")
        db.child(salaId).child("winner").setValue("")
    }

    private fun checkGanador(board: List<List<Int>>): Int {
        val b = board.map { it.toTypedArray() }.toTypedArray()

        for (r in 0..5) {
            for (c in 0..3) {
                val s = (0..3).map { b[r][c + it] }
                if (s.all { it != 0 && it == s[0] }) return s[0]
            }
        }
        for (r in 0..2) {
            for (c in 0..6) {
                val s = (0..3).map { b[r + it][c] }
                if (s.all { it != 0 && it == s[0] }) return s[0]
            }
        }
        for (r in 0..2) {
            for (c in 0..3) {
                val s = (0..3).map { b[r + it][c + it] }
                if (s.all { it != 0 && it == s[0] }) return s[0]
            }
        }
        for (r in 3..5) {
            for (c in 0..3) {
                val s = (0..3).map { b[r - it][c + it] }
                if (s.all { it != 0 && it == s[0] }) return s[0]
            }
        }
        return 0
    }
}

