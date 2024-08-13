package com.example.simplegame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simplegame.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding //to directly access the all the items in the layout

    private  var gameModel:GameModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playOfflineGameBtn.setOnClickListener {
            createOfflineGame()
            GameData.isTextScoreVisible = false // Hide text_score when play_offline_game_btn is clicked

        }

        binding.createOnlineGameBtn.setOnClickListener {
            CreateOnlineGame()
            GameData.isTextScoreVisible = true // Hide text_score when play_offline_game_btn is clicked

        }

        binding.joinOnlineGameBtn.setOnClickListener {
            joinOnlineGame()
            GameData.isTextScoreVisible = true // Hide text_score when play_offline_game_btn is clicked

        }
    }


    fun createOfflineGame(){
        GameData.saveGameModel(
        GameModel(
            gameStatus = GameStatus.JOINED
        )
        )
        startGame()
    }

    fun CreateOnlineGame(){
        GameData.myID = "X"
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.CREATED,
                gameId = Random.nextInt(1000..9999).toString() //we will get random number for the game ID
            )
        )
        startGame()

    }

    fun joinOnlineGame(){
        var gameId = binding.gameIdInput.text.toString()
        if(gameId.isEmpty()){
            binding.gameIdInput.setError("Please enter game ID")
            return
        }
        GameData.myID= "O"
        Firebase.firestore.collection("games")
            .document(gameId)
            .get()
            .addOnSuccessListener {
                val model = it?.toObject(GameModel::class.java)
                if(model==null){
                    binding.gameIdInput.setError("Please Enter Valid Game Id")
                }else{
                    model.gameStatus = GameStatus.JOINED
                    GameData.saveGameModel(model)
                    startGame()
                }
            }
    }

    fun startGame(){
        startActivity(Intent(this,GameActivity::class.java))
    }

}
