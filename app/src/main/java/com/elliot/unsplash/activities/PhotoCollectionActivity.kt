package com.elliot.unsplash.activities

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elliot.unsplash.R
import com.elliot.unsplash.model.Photo
import com.elliot.unsplash.model.SearchData
import com.elliot.unsplash.recyclerview.ISearchHistoryRecyclerView
import com.elliot.unsplash.recyclerview.PhotoGridRecyclerViewAdapter
import com.elliot.unsplash.recyclerview.SearchHistoryRecyclerViewAdapter
import com.elliot.unsplash.retrofit.RetrofitManager
import com.elliot.unsplash.utils.Constants
import com.elliot.unsplash.utils.RESPONSE_STATUS
import com.elliot.unsplash.utils.SharedPrefManager
import com.elliot.unsplash.utils.toSimpleString
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*
import kotlin.collections.ArrayList

class PhotoCollectionActivity : AppCompatActivity(),
                                SearchView.OnQueryTextListener,
                                CompoundButton.OnCheckedChangeListener,
                                View.OnClickListener,
                                ISearchHistoryRecyclerView{

    //데이터
    private var photoList = ArrayList<Photo>()

    //어답터
    private lateinit var photoGridRecyclerViewAdapter: PhotoGridRecyclerViewAdapter
    private lateinit var mySearchHistoryRecyclerViewAdapter: SearchHistoryRecyclerViewAdapter

    //서치뷰
    private lateinit var mySearchView : SearchView

    //서치뷰 EditText
    private lateinit var mySearchViewEditText : EditText

    //검색기록 배열
    private var searchHistoryList = ArrayList<SearchData>()

    private val topAppBar by lazy {findViewById<MaterialToolbar>(R.id.top_app_bar)}
    private val searchHistoryView by lazy {findViewById<LinearLayout>(R.id.linear_search_history_view)}
    private val switch by lazy {findViewById<SwitchMaterial>(R.id.search_history_mode_switch)}
    private val clearSearchButton by lazy {findViewById<Button>(R.id.clear_search_history_button)}

    private val photoRV by lazy {findViewById<RecyclerView>(R.id.my_photo_recycler_view)}
    private val searchHistoryRV by lazy {findViewById<RecyclerView>(R.id.search_history_recycler_view)}

    private val searchHistoryRVLabel by lazy {findViewById<TextView>(R.id.search_history_recycler_view_label)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_collection)

        val bundle = intent.getBundleExtra("array_bundle")
        val searchTerm = intent.getStringExtra("search_term")

        Log.d(Constants.TAG, "PhotoCollectionActivity - onCreate() called / searchTerm : $searchTerm, photoList.count() : ${photoList.count()}")

        switch.setOnCheckedChangeListener(this)
        clearSearchButton.setOnClickListener(this)

        switch.isChecked = SharedPrefManager.checkSearchHistoryMode()

        topAppBar.title = searchTerm

        //액티비티에서 어떤 액션바를 사용할지 설정
        setSupportActionBar(topAppBar)

        photoList = bundle?.getSerializable("photo_array_list") as ArrayList<Photo>

        //사진 리사이클러뷰 세팅
        this.photoCollectionRecyclerViewSetting(this.photoList)

        //저장된 검색기록 가져오기
        searchHistoryList = SharedPrefManager.getSearchHistoryList() as ArrayList<SearchData>

        searchHistoryList.forEach {
            Log.d(Constants.TAG, "저장된 검색기록 - it.term : ${it.term}, it.timestamp : ${it.timestamp}")
        }

        handleSearchViewUi()

        //검색기록 리사이클러뷰 준비
        searchHistoryRecyclerViewSetting(searchHistoryList)

        if (searchTerm != null) {
            if(searchTerm.isNotEmpty()){
                val term = searchTerm?.let {
                    it
                }?: ""
                insertSearchTermHistory(term)
            }
        }

    }//

    //검색기록 리사이클러뷰 준비
    private fun searchHistoryRecyclerViewSetting(searchHistoryList : ArrayList<SearchData>){
        Log.d(Constants.TAG, "PhotocollectionActivity - searchHistoryRecyclerViewSetting() called")

        //
        mySearchHistoryRecyclerViewAdapter = SearchHistoryRecyclerViewAdapter(this)
        mySearchHistoryRecyclerViewAdapter.submitList(searchHistoryList)

        val myLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        myLinearLayoutManager.stackFromEnd = true

        searchHistoryRV.apply {
            layoutManager = myLinearLayoutManager
            this.scrollToPosition(mySearchHistoryRecyclerViewAdapter.itemCount - 1)
            adapter = mySearchHistoryRecyclerViewAdapter

        }
    }

    //그리드 사진 리사이클러뷰 세팅
    private fun photoCollectionRecyclerViewSetting(photoList : ArrayList<Photo>){
        Log.d(Constants.TAG, "PhotocollectionActivity - photoCollectionRecyclerViewSetting() called")

        //
        this.photoGridRecyclerViewAdapter = PhotoGridRecyclerViewAdapter()

        this.photoGridRecyclerViewAdapter.submitList(photoList)

        photoRV.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        photoRV.adapter = photoGridRecyclerViewAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        Log.d(Constants.TAG, "PhotocollectionActivity - onCreateOptionsMenu() called")

        val inflater = menuInflater
        inflater.inflate(R.menu.top_app_bar_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        mySearchView = menu?.findItem(R.id.search_menu_item)?.actionView as SearchView
        mySearchView.apply {
            this.queryHint = "검색어를 입력해주세요"

            this.setOnQueryTextListener(this@PhotoCollectionActivity)

            this.setOnQueryTextFocusChangeListener { _, hasFocus ->
                when(hasFocus){
                    true -> {
                        Log.d(Constants.TAG, "서치뷰 열림")
                        searchHistoryView.visibility = View.VISIBLE
                        handleSearchViewUi()
                    }
                    false -> {
                        Log.d(Constants.TAG, "서치뷰 닫힘")
                        searchHistoryView.visibility = View.INVISIBLE

                    }
                }
            }
            //서치뷰에서 EditText를 가져온다.
            mySearchViewEditText = this.findViewById(androidx.appcompat.R.id.search_src_text)

        }

        mySearchViewEditText.apply {
            this.filters = arrayOf(InputFilter.LengthFilter(12))
            this.setTextColor(Color.WHITE)
            this.setHintTextColor(Color.WHITE)
        }

        return true
    }

    //서치뷰 검색어 입력 이벤트
    //검색 버튼이 클릭되었을때
    override fun onQueryTextSubmit(query: String?): Boolean {

        Log.d(Constants.TAG, "PhotoCollectionActivity - onQueryTextSubmit() called / query : $query")
        if(!query.isNullOrEmpty()){
            topAppBar.title = query

            //api 호출

            //검색어 저장
            insertSearchTermHistory(query)
            searchPhotoApiCall(query)
        }
//        mySearchView.setQuery("", false)
//        mySearchView.clearFocus()
        topAppBar.collapseActionView()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        Log.d(Constants.TAG, "PhotoCollectionActivity - onQueryTextChange() called / newText : $newText")

        val userInputText = newText ?: ""
        if(userInputText.count() == 12){
            Toast.makeText(this, "검색어는 12자 까지만 입력가능합니다.", Toast.LENGTH_SHORT).show()
        }
        return true

    }

    override fun onCheckedChanged(switch: CompoundButton?, isChecked: Boolean) {

        when(switch?.id){

            R.id.search_history_mode_switch -> {
                if(isChecked){
                    Log.d(Constants.TAG, "검색어 저장기능 ON")
                    SharedPrefManager.setSearchHistoryMode(isActivated = true)
                }else {
                    Log.d(Constants.TAG, "검색어 저장기능 OFF")
                    SharedPrefManager.setSearchHistoryMode(isActivated = false)

                }
            }

        }
    }

    override fun onClick(view: View?) {

        when(view?.id){
            R.id.clear_search_history_button -> {
                Log.d(Constants.TAG, "검색기록 삭제버튼이 클릭되었다.")
                SharedPrefManager.clearSearchHistoryList()
                searchHistoryList.clear()
                //ui처리
                handleSearchViewUi()
            }
        }
    }

    //검색 아이템 삭제 버튼 이벤트
    override fun onSearchItemDeleteClicked(position: Int) {
        Log.d(Constants.TAG, "PhotoCollectionActivity - onSearchItemDeleteClicked() called / position : $position")

        //해당 요소 삭제
        searchHistoryList.removeAt(position)
        //데이터 덮어쓰기
        SharedPrefManager.storeSearchHistoryList(searchHistoryList)
        //데이터 변경 되었다고 알려줌
        mySearchHistoryRecyclerViewAdapter.notifyDataSetChanged()
        handleSearchViewUi()
    }

    //검색 아이템 버튼 이벤트
    override fun onSearchItemClicked(position: Int) {
        Log.d(Constants.TAG, "PhotoCollectionActivity - onSearchItemClicked() called / position : $position")

        val queryString = searchHistoryList[position].term
        //해당 녀석의 검색어로 API 호출
        searchPhotoApiCall(queryString)

        topAppBar.title = queryString

        insertSearchTermHistory(searchTerm = queryString)
        topAppBar.collapseActionView()
    }

    //사진 검색 API 호출
    private fun searchPhotoApiCall(query : String){
        RetrofitManager.instance.searchPhotos(searchTerm = query, completion = {status, list ->
            when(status){
                RESPONSE_STATUS.OKAY -> {
                    Log.d(Constants.TAG, "PhotoCollectionActivity - searchPhotoApiCall() called / list.size : ${list?.size}")
                    if(list != null){
                        photoList.clear()
                        photoList = list
                        photoGridRecyclerViewAdapter.submitList(photoList)
                        photoGridRecyclerViewAdapter.notifyDataSetChanged()

                    }
                }
                RESPONSE_STATUS.NO_CONTENT -> {
                    Toast.makeText(this, "$query 에 대한 검색결과가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun handleSearchViewUi(){
        Log.d(Constants.TAG, "PhotoCollectionActivity - handleSearchViewUi() called / size : ${searchHistoryList.size}")
        if(searchHistoryList.size > 0){
            searchHistoryRV.visibility = View.VISIBLE
            searchHistoryRVLabel.visibility = View.VISIBLE
            clearSearchButton.visibility = View.VISIBLE
        } else {
            searchHistoryRV.visibility = View.INVISIBLE
            searchHistoryRVLabel.visibility = View.INVISIBLE
            clearSearchButton.visibility = View.INVISIBLE
        }
    }

    //검색어 저장
    private fun insertSearchTermHistory(searchTerm : String){
        Log.d(Constants.TAG, "PhotoCollectionActivity - insertSearchTermHistory() called}")

        if(SharedPrefManager.checkSearchHistoryMode()){
            //중복 아이템 삭제
            var indexListToRemove = ArrayList<Int>()

            searchHistoryList.forEachIndexed { index, searchDataItem ->
                Log.d(Constants.TAG, "index : $index")

                if(searchDataItem.term == searchTerm){
                    indexListToRemove.add(index)
                }
            }

            indexListToRemove.forEach{
                searchHistoryList.removeAt(it)
            }
            //새 아이템 넣기
            val newSearchData = SearchData(term = searchTerm, timestamp = Date().toSimpleString())
            searchHistoryList.add(newSearchData)

            //기존데이터에 덮어쓰기
            SharedPrefManager.storeSearchHistoryList(searchHistoryList)
            mySearchHistoryRecyclerViewAdapter.notifyDataSetChanged()
        }
    }
}