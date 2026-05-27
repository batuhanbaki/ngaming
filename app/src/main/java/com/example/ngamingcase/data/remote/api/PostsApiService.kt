package com.example.ngamingcase.data.remote.api

import com.example.ngamingcase.data.remote.dto.PostDto
import retrofit2.http.GET

interface PostsApiService {
    @GET("posts")
    suspend fun getPosts(): List<PostDto>
}
