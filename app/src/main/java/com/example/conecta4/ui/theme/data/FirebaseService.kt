package com.example.conecta4.ui.theme.data

import com.example.conecta4.ui.theme.model.Move
import com.google.firebase.database.*

class FirebaseService {
    private val db = FirebaseDatabase.getInstance().reference

    fun sendMove(gameId: String, move: Move) {
        db.child("games/$gameId/moves").push().setValue(move)
    }

    fun listenForMoves(gameId: String, onMoveReceived: (Move) -> Unit) {
        db.child("games/$gameId/moves").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Move::class.java)?.let(onMoveReceived)
            }
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }
}
