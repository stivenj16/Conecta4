package com.example.conecta4.ui.theme.data

import com.example.conecta4.ui.theme.model.Word

class VocabularyRepository {
    private val words = listOf(
        Word("apple", "manzana"),
        Word("house", "casa"),
        Word("book", "libro"),
        Word("dog", "perro"),
        Word("cat", "gato"),
        Word("car", "coche"),
        Word("water", "agua"),
        Word("tree", "arbol"),
        Word("sun", "sol"),
        Word("moon", "luna"),
        Word("star", "estrella"),
        Word("window", "ventana"),
        Word("door", "puerta"),
        Word("school", "escuela"),
        Word("chair", "silla"),
        Word("table", "mesa"),
        Word("bread", "pan"),
        Word("milk", "leche"),
        Word("cheese", "queso"),
        Word("fish", "pescado"),
        Word("bird", "pajaro"),
        Word("flower", "flor"),
        Word("river", "rio"),
        Word("sky", "cielo")
    )

    fun getRandomWord(): Word {
        return words.random()
    }
}