package com.elliot.unsplash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.elliot.unsplash.databinding.ActivityMainBinding
import com.elliot.unsplash.retrofit.RetrofitManager
import com.elliot.unsplash.utils.Constants
import com.elliot.unsplash.utils.RESPONSE_STATUS
import com.elliot.unsplash.utils.SEARCH_TYPE
import com.elliot.unsplash.utils.onMyTextChanged

class MainActivity : AppCompatActivity() {

    private var currentSearchType : SEARCH_TYPE = SEARCH_TYPE.PHOTO

    private lateinit var binding : ActivityMainBinding

    private val progressBar by lazy {findViewById<ProgressBar>(R.id.btn_progress)}

    private val searchBtn by lazy { findViewById<Button>(R.id.btn_search) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        Log.d(Constants.TAG, "MainActivity - onCreate() called")

        //라디오 그룹 가져오기
        binding.searchTermRadioGroup.setOnCheckedChangeListener { _, checkedId ->

            // when 문
            when(checkedId){
                R.id.photo_search_radio_btn -> {
                    Log.d(Constants.TAG, "사진검색 버튼 클릭")
                    binding.searchTermTextLayout.hint = "사진검색"
                    binding.searchTermTextLayout.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_photo_library_24, resources.newTheme())
                    this.currentSearchType = SEARCH_TYPE.PHOTO
                }

                R.id.user_search_radio_btn -> {
                    Log.d(Constants.TAG, "사용자검색 버튼 클릭")
                    binding.searchTermTextLayout.hint = "사용자검색"
                    binding.searchTermTextLayout.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_person_24, resources.newTheme())
                    this.currentSearchType = SEARCH_TYPE.USER
                }
            }
            Log.d(Constants.TAG, "MainActivity - onCheckedChanged() called / currentSearchType : $currentSearchType")
        }

        //텍스트가 변경이 되었을 때
        binding.searchTermEditText.onMyTextChanged {
            //입력된 글자가 하나라도 있으면
            if(it.toString().count() > 0){
                //검색 버튼을 보여준다.
                findViewById<FrameLayout>(R.id.frame_search_btn).visibility = View.VISIBLE

                binding.searchTermTextLayout.helperText = ""

                //스크롤뷰를 올린다.
                binding.mainScrollview.scrollTo(0, 200)
            }else {
                findViewById<FrameLayout>(R.id.frame_search_btn).visibility = View.INVISIBLE
                binding.searchTermTextLayout.helperText = "검색어를 입력해주세요"
            }

            if(it.toString().count() == 12){
                Log.d(Constants.TAG, "MainActivity - 에러 띄우기")
                Toast.makeText(this, "검색어는 12자 까지만 입력 가능합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //검색 버튼 클릭시
        findViewById<Button>(R.id.btn_search).setOnClickListener {

            this.handleSearchButtonUi()

            Log.d(Constants.TAG, "MainActivity - 검색버튼이 클릭되었다. / currentSearchType : $currentSearchType")

            val userSearchInput = binding.searchTermEditText.text.toString()

            //검색 api 호출
            RetrofitManager.instance.searchPhotos(searchTerm = binding.searchTermEditText.text.toString(), completion = {
                responseState, responseDataArrayList ->

                when(responseState){
                    RESPONSE_STATUS.OKAY -> {
                        Log.d(Constants.TAG, "api 호출 성공 : ${responseDataArrayList?.size}")
                        val intent = Intent(this, PhotoCollectionActivity::class.java)

                        val bundle = Bundle()

                        bundle.putSerializable("photo_array_list", responseDataArrayList)

                        intent.putExtra("array_bundle", bundle)
                        intent.putExtra("search_term", userSearchInput)

                        startActivity(intent)
                    }
                    RESPONSE_STATUS.FAIL -> {
                        Toast.makeText(this, "api 호출 에러", Toast.LENGTH_SHORT).show()
                        Log.d(Constants.TAG, "api 호출 실패 : $responseDataArrayList")
                    }

                    RESPONSE_STATUS.NO_CONTENT -> {
                        Toast.makeText(this, "검색결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                progressBar.visibility = View.INVISIBLE
                searchBtn.visibility = View.VISIBLE
                searchBtn.text = "검색"
                binding.searchTermEditText.setText("")
            })

        }
    }//onCreate

    private fun handleSearchButtonUi(){
        progressBar.visibility = View.VISIBLE
        searchBtn.visibility = View.INVISIBLE

//        Handler(Looper.getMainLooper()).postDelayed({
//            progressBar.visibility = View.INVISIBLE
//            searchBtn.visibility = View.VISIBLE
//            searchBtn.text = "검색"
//        }, 1500)
    }
}