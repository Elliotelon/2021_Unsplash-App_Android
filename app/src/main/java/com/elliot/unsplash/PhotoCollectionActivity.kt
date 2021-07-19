package com.elliot.unsplash

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elliot.unsplash.model.Photo
import com.elliot.unsplash.recyclerview.PhotoGridRecyclerViewAdapter
import com.elliot.unsplash.utils.Constants
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar

class PhotoCollectionActivity: AppCompatActivity() {

    //데이터
    private var photoList = ArrayList<Photo>()

    //어답터
    private lateinit var photoGridRecyclerViewAdapter: PhotoGridRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_collection)

        val bundle = intent.getBundleExtra("array_bundle")
        val searchTerm = intent.getStringExtra("search_term")

        photoList = bundle?.getSerializable("photo_array_list") as ArrayList<Photo>

        Log.d(Constants.TAG, "PhotoCollectionActivity - onCreate() called / searchTerm : $searchTerm, photoList.count() : ${photoList.count()}")

        findViewById<MaterialToolbar>(R.id.top_App_Bar).title = searchTerm

        this.photoGridRecyclerViewAdapter = PhotoGridRecyclerViewAdapter()

        this.photoGridRecyclerViewAdapter.submitList(photoList)

        val recyclerView = findViewById<RecyclerView>(R.id.my_photo_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.adapter = photoGridRecyclerViewAdapter

    }//
}