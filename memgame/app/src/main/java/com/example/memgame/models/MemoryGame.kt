package com.example.memgame.models

import com.example.memgame.utils.DEFAULT_ICONS

class MemoryGame(private val boardSize: BoardSize) {

    val cards: List<MemoryCard>
    var numPairsFound=0
    private var numCardFlips=0
    private var indexOfSingleSelectedCard: Int? = null
    init {
        val chosenImages = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages= (chosenImages + chosenImages).shuffled()
        cards = randomizedImages.map { MemoryCard(it) }
    }

    fun flipCard(position: Int): Boolean {
        numCardFlips++
        val card=cards[position]
        var foundMatch=false
        //Three cases:
        // 0 card flipped => flip the selected card
        // 1 card flipped => flip the selected card and check if pair matches
        // 2 card flipped => reset the position and flip the card
        if (indexOfSingleSelectedCard == null){
            restoreCards()

            indexOfSingleSelectedCard=position
        }else{
            foundMatch= checkForMatch(indexOfSingleSelectedCard!!,position)
            indexOfSingleSelectedCard=null

        }
        card.isFaceUp=!card.isFaceUp
        return foundMatch

    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if (cards[position1].identifier!=cards[position2].identifier){
            return false
        }
        cards[position1].isMatached=true
        cards[position2].isMatached=true
        numPairsFound++
        return true
    }

    private fun restoreCards() {
        for(card in cards) {
            if (!card.isMatached){
                card.isFaceUp = false
        }
        }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound==boardSize.getNumPairs()

    }

    fun isCardFaceUP(position: Int): Boolean {
        return cards[position].isFaceUp

    }

    fun getNumMoves(): Int {
        return numCardFlips/2

    }


}