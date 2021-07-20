package com.elliot.unsplash.recyclerview

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.elliot.unsplash.R
import com.elliot.unsplash.model.SearchData
import com.elliot.unsplash.utils.Constants

class SearchItemViewHolder(itemView : View, searchRecyclerViewInterface : ISearchHistoryRecyclerView)
    : RecyclerView.ViewHolder(itemView), View.OnClickListener

{
    private var mySearchRecyclerViewInterface : ISearchHistoryRecyclerView

    //뷰 가져오기
    private val searchTermTextView = itemView.findViewById<TextView>(R.id.search_term_text)
    private val whenSearchedTextView = itemView.findViewById<TextView>(R.id.when_searched_text)
    private val deleteSearchBtn = itemView.findViewById<ImageView>(R.id.delte_search_btn)
    private val constraintSearchItem = itemView.findViewById<ConstraintLayout>(R.id.constraint_search_item)

    init {
        Log.d(Constants.TAG, "SearchItemViewHolder - init() called")
        //리스너 연결
        deleteSearchBtn.setOnClickListener(this)
        constraintSearchItem.setOnClickListener(this)
        this.mySearchRecyclerViewInterface = searchRecyclerViewInterface
    }

    //데이터와 뷰를 묶는다.
    fun bindWithView(searchItem : SearchData){
        Log.d(Constants.TAG, "SearchItemViewHolder - bindWithView() called")
        whenSearchedTextView.text = searchItem.timestamp
        searchTermTextView.text = searchItem.term
    }

    override fun onClick(view: View?) {
        Log.d(Constants.TAG, "SearchItemViewHolder - onClick() called")
        when(view){
            deleteSearchBtn -> {
                Log.d(Constants.TAG, "SearchItemViewHolder - 검색 삭제 버튼 클릭")
                this.mySearchRecyclerViewInterface.onSearchItemDeleteClicked(adapterPosition)
            }
            constraintSearchItem -> {
                Log.d(Constants.TAG, "SearchItemViewHolder - 검색 아이템 클릭")
                this.mySearchRecyclerViewInterface.onSearchItemClicked(adapterPosition)
            }
        }
    }
}