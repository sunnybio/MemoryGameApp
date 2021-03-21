package com.example.memgame

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.memgame.models.BoardSize
import com.example.memgame.models.MemoryCard
import kotlin.math.min

class MemoryBoardAdapter(private val context: Context,
                         private val boardSize: BoardSize,
                         private val cards: List<MemoryCard>,
                         private val cardClickListener: CardClickListener  ) :
        RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {
        companion object{
            private const val MARGIN_SIZE = 10
            private const val TAG="MemoryBoardAdapter"
        }
        interface CardClickListener{
            fun onCardClick(position: Int)
        }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val cardHeight= parent.height / boardSize.getHeight() - (2* MARGIN_SIZE)
        val cardwidth = parent.width / boardSize.getWidth() -(2* MARGIN_SIZE)
        val cardside = min(cardHeight,cardwidth)
        val view = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        val layoutParams = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.height=cardside
        layoutParams.width=cardside
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = boardSize.numCards
    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        private val imageButton=itemView.findViewById<ImageButton>(R.id.imageButton)
        fun bind(position: Int) {
            val memoryCard=cards[position]
            imageButton.setImageResource(if (memoryCard.isFaceUp) memoryCard.identifier else R.drawable.ic_launcher_background )

            imageButton.alpha=if(memoryCard.isMatached) .4f else 1.0f
            val colorStateList=if(memoryCard.isMatached) ContextCompat.getColorStateList(context,R.color.color_gray) else null
            ViewCompat.setBackgroundTintList(imageButton,colorStateList)

            imageButton.setOnClickListener{
                Log.i(TAG, "clicked on position $position")
                cardClickListener.onCardClick(position)
            }

        }
    }

}
