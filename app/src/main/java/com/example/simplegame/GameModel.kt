package com.example.simplegame

import kotlin.random.Random

data class GameModel(
    var gameId: String = "-1",
    var filledPos: MutableList<String> = mutableListOf("","","","","","","","",""),
    var winner: String = "",
    var gameStatus: GameStatus = GameStatus.CREATED,
    var currentPlayer: String = (arrayOf("X","O"))[Random.nextInt(2)],
    var round: Int = 0,
    var scoreX: Int = 0, // Score for player X
    var scoreO: Int = 0, // Score for player O

)

enum class GameStatus{ //enum class : allows to define a set of named constants
    CREATED,
    JOINED,
    INPROGRESS,
    FINISHED
}