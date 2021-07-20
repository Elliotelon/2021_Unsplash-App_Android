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
import androidx.recyclerview.widget.RecyclerView
import com.elliot.unsplash.R
import com.elliot.unsplash.model.Photo
import com.elliot.unsplash.model.SearchData
import com.elliot.unsplash.recyclerview.PhotoGridRecyclerViewAdapter
import com.elliot.unsplash.utils.Constants
import com.elliot.unsplash.utils.SharedPrefManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*
import kotlin.collections.ArrayList

class PhotoCollectionActivity: AppCompatActivity(),
                                SearchView.OnQueryTextListener,
                                CompoundButton.OnCheckedChangeListener,
                                View.OnClickListener{

    //데이터
    private var photoList = ArrayList<Photo>()

    //어답터
    private lateinit var photoGridRecyclerViewAdapter: PhotoGridRecyclerViewAdapter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_collection)

        val bundle = intent.getBundleExtra("array_bundle")
        val searchTerm = intent.getStringExtra("search_term")

        photoList = bundle?.getSerializable("photo_array_list") as ArrayList<Photo>

        Log.d(Constants.TAG, "PhotoCollectionActivity - onCreate() called / searchTerm : $searchTerm, photoList.count() : ${photoList.count()}")

        switch.setOnCheckedChangeListener(this)
        clearSearchButton.setOnClickListener(this)

        topAppBar.title = searchTerm

        //액티비티에서 어떤 액션바를 사용할지 설정
        setSupportActionBar(topAppBar)


        this.photoGridRecyclerViewAdapter = PhotoGridRecyclerViewAdapter()

        this.photoGridRecyclerViewAdapter.submitList(photoList)

        val recyclerView = findViewById<RecyclerView>(R.id.my_photo_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.adapter = photoGridRecyclerViewAdapter

        //저장된 검색기록 가져오기
        searchHistoryList = SharedPrefManager.getSearchHistoryList() as ArrayList<SearchData>

        searchHistoryList.forEach {
            Log.d(Constants.TAG, "저장된 검색기록 - it.term : ${it.term}, it.timestamp : ${it.timestamp}")
        }

    }//

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

            val newSearchData = SearchData(term =query, timestamp = Date().toString())

            searchHistoryList.add(newSearchData)
            SharedPrefManager.storeSearchHistoryList(searchHistoryList)
        }
        mySearchView.setQuery("", false)
        mySearchView.clearFocus()
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
                }else {
                    Log.d(Constants.TAG, "검색어 저장기능 OFF")

                }
            }

        }
    }

    override fun onClick(view: View?) {

        when(view?.id){
            R.id.clear_search_history_button -> {
                Log.d(Constants.TAG, "검색기록 삭제버튼이 클릭되었다.")
            }
        }
    }
}