package com.example.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AudioApiService {
    @GET("search")
    suspend fun searchSongs(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<SearchResponseDto>

    @GET("tracks/{id}")
    suspend fun getSongDetail(
        @Path("id") songId: String
    ): Response<SongDto>

    @GET("recommendations")
    suspend fun getRecommendations(
        @Query("limit") limit: Int = 20
    ): Response<SearchResponseDto>
}
