package com.elliot.unsplash.recyclerview

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elliot.unsplash.App
import com.elliot.unsplash.R
import com.elliot.unsplash.model.Photo

class PhotoItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    //뷰들을 가져온다..
    private val photoImageView = itemView?.findViewById<ImageView>(R.id.photo_image)
    private val photoCreatedAtText = itemView?.findViewById<TextView>(R.id.created_at_text)
    private val photoLikesCountText = itemView?.findViewById<TextView>(R.id.likes_count_text)

    //데이터와 뷰를 묶는다.
    fun bindWithView(photoItem : Photo){
        photoCreatedAtText.text = photoItem.createdAt
        photoLikesCountText.text = photoItem.likesCount.toString()

        // 이미지를 설정한다.
        Glide.with(App.instance)
            .load(photoItem.thumbnail)
            .placeholder(R.drawable.ic_baseline_insert_photo_24)
            .into(photoImageView)
    }
}