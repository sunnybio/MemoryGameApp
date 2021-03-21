package com.example.memgame.models

data class MemoryCard (
    val identifier: Int,
    var isFaceUp : Boolean= false,
    var isMatached : Boolean = false
)