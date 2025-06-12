package com.example.conecta4.ui.theme.data

import com.example.conecta4.ui.theme.model.Word

class VocabularyRepository {
    private val words = listOf(
        Word("apple", "manzana"),
        Word("house", "casa"),
        Word("book", "libro"),
        Word("dog", "perro"),
        Word("cat", "gato")
    )

    fun getRandomWord(): Word {
        return words.random()
    }
}