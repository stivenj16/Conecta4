package com.example.conecta4.ui.theme

import androidx.compose.material.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import com.example.conecta4.ui.theme.model.Word

@Composable
fun VocabularyDialog(word: Word, onSubmit: (Boolean) -> Unit) {
    var userInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {},
        title = { Text("Traduce la palabra: ${word.english}") },
        text = {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("Traducción") }
            )
        },
        confirmButton = {
            Button(onClick = {
                val isCorrect = userInput.trim().lowercase() == word.spanish.lowercase()
                onSubmit(isCorrect)
            }) {
                Text("Enviar")
            }
        }
    )
}