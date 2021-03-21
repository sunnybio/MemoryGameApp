package com.example.memgame

import android.animation.ArgbEvaluator
import android.content.Intent
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memgame.models.BoardSize
import com.example.memgame.models.MemoryGame
import com.example.memgame.utils.EXTRA_BOARD_SIZE
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    companion object{
        private const val TAG="MainActivity"
        private const val CREATE_REQUEST_CODE=198
    }

    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var clRoot: ConstraintLayout

    private lateinit var memoryGame: MemoryGame
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private var boardSize: BoardSize=BoardSize.EASY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clRoot=findViewById(R.id.clRoot)
        rvBoard= findViewById(R.id.rvBoard)

        tvNumMoves= findViewById(R.id.tvNumMoves)
        tvNumPairs= findViewById(R.id.tvNumPairs)
        setupBoard()
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mi_referesh ->{
                if (memoryGame.getNumMoves()>0 && !memoryGame.haveWonGame()){
                    showAlertDialog("Quit your current game",null, View.OnClickListener {
                        setupBoard()
                    })
                }else{
                    setupBoard()
                }

            }
            R.id.mi_new_size ->{
                showNewSizeDialog()
                return true
            }
            R.id.mi_custom ->{
                showCreationDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCreationDialog() {
        val boardSizeView= LayoutInflater.from(this).inflate(R.layout.dialog_board_size,null)
        val radioGroupSize= boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        showAlertDialog("Create your own memory board", boardSizeView,View.OnClickListener {
            val desiredBoardSize=when(radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            //navigate to other activity
            val intent=Intent(this, CreateActivity::class.java)
            intent.putExtra(EXTRA_BOARD_SIZE, desiredBoardSize)
            startActivityForResult(intent,CREATE_REQUEST_CODE)

        })

    }

    private fun showNewSizeDialog() {
        val boardSizeView= LayoutInflater.from(this).inflate(R.layout.dialog_board_size,null)
        val radioGroupSize= boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when(boardSize){
            BoardSize.EASY ->radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM ->radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD ->radioGroupSize.check(R.id.rbHard)

        }
        showAlertDialog("Choose New Size", boardSizeView,View.OnClickListener {
            boardSize=when(radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            setupBoard()
        })
    }

    private fun showAlertDialog(title: String, view: View?,positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setView(view)
                .setNegativeButton("Cancel",null)
                .setPositiveButton("OK"){_,_->
                    positiveClickListener.onClick(null)

                }.show()
    }

    private fun setupBoard() {
        when(boardSize){
            BoardSize.EASY ->{
                tvNumPairs.text="Pairs: 0 / 4"
                tvNumMoves.text="EASY: 4 x 2"
            }
            BoardSize.MEDIUM ->{
                tvNumPairs.text="Pairs: 0 / 9"
                tvNumMoves.text="MEDIUM: 3 x 6"
            }
            BoardSize.HARD ->{
                tvNumPairs.text="Pairs: 0 / 12"
                tvNumMoves.text="HARD: 4 x 6"
            }
        }
        tvNumPairs.setTextColor(ContextCompat.getColor(this,R.color.color_progress_none))
        memoryGame= MemoryGame(boardSize)

        adapter  =MemoryBoardAdapter(this, boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener{
            override fun onCardClick(position: Int) {
                updateGameWithFlip(position)
            }

        })

        rvBoard.adapter=adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager= GridLayoutManager( this,  boardSize.getWidth())
    }
    private fun updateGameWithFlip(position: Int) {
        if(memoryGame.haveWonGame()){
            Snackbar.make(clRoot, "You already won", Snackbar.LENGTH_LONG).show()
            return
        }
        if(memoryGame.isCardFaceUP(position)){
            Snackbar.make(clRoot, "invalid move", Snackbar.LENGTH_SHORT).show()
            return
        }
        if (memoryGame.flipCard(position)){


            Log.i(TAG,"Found the match! number of pairs found ${memoryGame.numPairsFound}")
            val color = ArgbEvaluator().evaluate(
                    memoryGame.numPairsFound.toFloat()/boardSize.getNumPairs()
                    ,ContextCompat.getColor(this, R.color.color_progress_none),
                    ContextCompat.getColor(this,R.color.color_progress_full)
            ) as Int
            tvNumPairs.setTextColor(color)
            tvNumPairs.text="Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            if(memoryGame.haveWonGame()){
                Snackbar.make(clRoot,"Congratulations you have won",Snackbar.LENGTH_LONG).show()
            }
        }
        tvNumMoves.text="Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }
}