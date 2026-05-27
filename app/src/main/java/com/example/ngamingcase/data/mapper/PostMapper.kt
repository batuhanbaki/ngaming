package com.example.ngamingcase.data.mapper

import com.example.ngamingcase.data.remote.dto.PostDto
import com.example.ngamingcase.domain.model.Post

fun PostDto.toDomain(): Post = Post(id = id, title = title, body = body)
