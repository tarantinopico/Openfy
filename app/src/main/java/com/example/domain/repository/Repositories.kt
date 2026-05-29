package com.example.domain.repository

import com.example.domain.model.AppError
import com.example.domain.model.CachePolicy
import com.example.domain.model.Result
import com.example.domain.model.SearchQuery
import com.example.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface AudioRepository {
    suspend fun getSongDetail(songId: String, policy: CachePolicy = CachePolicy.NETWORK_FIRST): Result<Song, AppError>
    suspend fun getRecommendations(): Result<List<Song>, AppError>
}

interface SearchRepository {
    suspend fun searchSongs(query: SearchQuery): Result<List<Song>, AppError>
    fun getRecentSearches(): Flow<List<String>>
    suspend fun saveSearchQuery(query: String)
    suspend fun clearRecentSearches()
}

interface LibraryRepository {
    fun getFavoriteSongs(): Flow<List<Song>>
    suspend fun toggleFavorite(song: Song)
    fun isFavorite(songId: String): Flow<Boolean>
}
