package com.elliot.unsplash.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elliot.unsplash.R
import com.elliot.unsplash.model.SearchData
import com.elliot.unsplash.utils.Constants

class SearchHistoryRecyclerViewAdapter(searchHistoryRecyclerViewInterface : ISearchHistoryRecyclerView) : RecyclerView.Adapter<SearchItemViewHolder>() {

    private var searchHistoryList : ArrayList<SearchData> = ArrayList()

    private var iSearchHistoryRecyclerView : ISearchHistoryRecyclerView? = null

    init {
        Log.d(Constants.TAG, "SearchHistoryRecyclerViewAdapter - init() called")
        this.iSearchHistoryRecyclerView = searchHistoryRecyclerViewInterface
    }
    //뷰홀더가 메모리에 올라갔을때
    //뷰홀더와 레이아웃을 연결 시켜준다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val searchItemViewHolder = SearchItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.layout_search_item, parent, false),
            this.iSearchHistoryRecyclerView!!
        )

        return searchItemViewHolder
    }

    override fun getItemCount(): Int {
        return searchHistoryList.size
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {

        val dataItem = searchHistoryList[position]

        holder.bindWithView(dataItem)
    }

    //외부에서 어답터에 데이터 배열을 넣어준다.
    fun submitList(searchHistoryList : ArrayList<SearchData>){
        this.searchHistoryList = searchHistoryList
    }


}