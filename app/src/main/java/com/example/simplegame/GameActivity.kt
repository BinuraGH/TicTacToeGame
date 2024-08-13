package com.example.simplegame

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simplegame.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity(), View.OnClickListener{

    lateinit var binding: ActivityGameBinding

    private var gameModel :GameModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GameData.fetchGameModel() //fetch new data to the UI
        binding.textScore.visibility = if (GameData.isTextScoreVisible) View.VISIBLE else View.INVISIBLE


        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.btn4.setOnClickListener(this)
        binding.btn5.setOnClickListener(this)
        binding.btn6.setOnClickListener(this)
        binding.btn7.setOnClickListener(this)
        binding.btn8.setOnClickListener(this)

        binding.startGameBtn.setOnClickListener{
            startGame()
        }
        binding.quitGameBtn.setOnClickListener {
            quitGame()
        }
        GameData.gameModel.observe(this){
            gameModel = it
            setUI()

        }
    }

    private fun quitGame() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun startGame(){
        binding.startGameBtn.visibility=View.INVISIBLE
        gameModel?.apply {
            updateGameData(
                GameModel(
                    gameId = gameId,
                    gameStatus = GameStatus.INPROGRESS,
                    round = round + 1, //Increment round value By 1
                    scoreX = scoreX, // Set total scores from previous rounds
                    scoreO = scoreO  // Set total scores from previous rounds
                )
            )
            binding.textRound.visibility = View.VISIBLE // Show the text_round TextView
            binding.textScore.visibility = View.VISIBLE
        }
    }

    private fun updateGameData(model: GameModel){
        GameData.saveGameModel(model)
    }

    fun checkForWinner(){
        val winningsPos = arrayOf(
            intArrayOf(0,1,2),
            intArrayOf(3,4,5),
            intArrayOf(6,7,8),
            intArrayOf(0,3,6),
            intArrayOf(1,4,7),
            intArrayOf(2,5,8),
            intArrayOf(0,4,8),
            intArrayOf(2,4,6),
        )

        gameModel?.apply {
            for (i in winningsPos){
                //012
                if(
                    filledPos[i[0]] == filledPos[i[1]] &&
                    filledPos[i[1]] == filledPos[i[2]] &&
                    filledPos[i[0]].isNotEmpty()
                ){
                    gameStatus = GameStatus.FINISHED
                    winner = filledPos[i[0]]

                    if(winner == "X") {
                        scoreX++ // Increment X's score if X wins
                    } else {
                        scoreO++ // Increment O's score if O wins
                    }
                }
                //check whether it is DRAW
                if(filledPos.none(){it.isEmpty()}) {
                    gameStatus = GameStatus.FINISHED
                }
            }

            if (round >= 5) { // Check if round is equal to or greater than 5
                //check whether DRAW - All the values are filled (non of the values are empty)
                if(filledPos.none(){it.isEmpty()}){
                    gameStatus = GameStatus.FINISHED
                    showHighestScore() // Modified: Added call to showHighestScore()

                }
            }
            updateGameData(this)
        }
    }

    private fun showHighestScore() {
        val highestScore = if ((gameModel?.scoreX ?: 0) > (gameModel?.scoreO ?: 0)) {
            gameModel?.scoreX ?: 0
        } else {
            gameModel?.scoreO ?: 0
        }
        binding.textHighestScore.text = "Highest Score: $highestScore"
        binding.textHighestScore.visibility = View.VISIBLE
        binding.quitGameBtn.visibility = View.VISIBLE

    }

private fun setUI(){
    gameModel?.apply {
        binding.btn0.text = filledPos[0]
        binding.btn1.text = filledPos[1]
        binding.btn2.text = filledPos[2]
        binding.btn3.text = filledPos[3]
        binding.btn4.text = filledPos[4]
        binding.btn5.text = filledPos[5]
        binding.btn6.text = filledPos[6]
        binding.btn7.text = filledPos[7]
        binding.btn8.text = filledPos[8]

        binding.textRound.text = "Round : $round"

        binding.textScore.text = "Scores\nX: $scoreX\nO: $scoreO"

        binding.textRound.visibility = View.VISIBLE


        binding.textStatus.text =
            when(gameStatus){
                GameStatus.CREATED -> {
                    binding.quitGameBtn.visibility = View.INVISIBLE
                    binding.textRound.visibility = View.INVISIBLE
                    binding.startGameBtn.visibility = View.INVISIBLE
                    binding.textHighestScore.visibility = View.INVISIBLE
                    "Game ID: $gameId"
                }
                GameStatus.JOINED -> {
                    binding.quitGameBtn.visibility = View.INVISIBLE
                    binding.textRound.visibility = View.INVISIBLE
                    binding.startGameBtn.visibility = View.VISIBLE
                    binding.textHighestScore.visibility = View.INVISIBLE
                    "Click On Start Game"
                }
                GameStatus.INPROGRESS -> {
                    binding.startGameBtn.visibility = View.INVISIBLE

                    when(GameData.myID){
                        currentPlayer -> "Your Turn"
                        else-> "$currentPlayer Turn"
                    }
                }
                GameStatus.FINISHED -> {
                    // Hide the Start button when round 5 is reached
                    if (round >= 5) {
                        binding.startGameBtn.visibility = View.INVISIBLE
                        showHighestScore()
                    }else{
                        binding.startGameBtn.visibility = View.VISIBLE
                    }
                    if(winner.isNotEmpty()){
                        when(GameData.myID) {
                            winner -> "$winner Won"
                            else -> "$winner Won"
                        }
                    } else "DRAW !!!"
                }
            }
    }
}

    override fun onClick(v: View?) {
        gameModel?.apply {
            if(gameStatus != GameStatus.INPROGRESS){
                Toast.makeText(applicationContext,"Game Not Started",Toast.LENGTH_SHORT).show() //Show Toast Mesaage
                return
            }
            //game is  in progress
            if(gameId!="-1" && currentPlayer!=GameData.myID){ //check whether who has the Turn
                Toast.makeText(applicationContext,"Not Your Turn",Toast.LENGTH_SHORT).show() //Show Toast Mesaage
                return
            }

            val clickedPos = (v?.tag as String).toInt() //String to Int
            if(filledPos[clickedPos].isEmpty()){
                filledPos[clickedPos] = currentPlayer
                currentPlayer = if(currentPlayer == "X") "O" else "X"
                checkForWinner()
                updateGameData(this)

            }
        }
    }
}