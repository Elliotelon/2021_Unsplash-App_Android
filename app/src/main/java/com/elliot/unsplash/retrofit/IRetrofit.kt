package com.elliot.unsplash.retrofit

import com.elliot.unsplash.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IRetrofit {

    //https:www.unsplash.com/search/photos?query="searchTerm"
    @GET(API.SEARCH_PHOTOS)
    fun searchPhotos(@Query("query") searchTerm : String) : Call<JsonElement>

    //https:www.unsplash.com/search/users?query="searchTerm"
    @GET(API.SEARCH_USERS)
    fun searchUsers(@Query("query") searchTerm : String) : Call<JsonElement>
}