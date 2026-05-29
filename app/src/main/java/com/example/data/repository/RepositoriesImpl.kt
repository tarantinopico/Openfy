package com.example.data.repository

import com.example.data.local.AudioDao
import com.example.data.local.RecentSearchEntity
import com.example.data.local.FavoriteSongEntity
import com.example.data.network.AudioApiService
import com.example.data.network.safeApiCall
import com.example.data.network.toDomain
import com.example.domain.model.AppError
import com.example.domain.model.CachePolicy
import com.example.domain.model.Result
import com.example.domain.model.SearchQuery
import com.example.domain.model.Song
import com.example.domain.repository.AudioRepository
import com.example.domain.repository.LibraryRepository
import com.example.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepositoryImpl @Inject constructor(
    private val dao: AudioDao
) : LibraryRepository {
    override fun getFavoriteSongs(): Flow<List<Song>> {
        return dao.getFavoriteSongs().map { list ->
            list.map {
                Song(
                    id = it.id,
                    title = it.title,
                    artist = com.example.domain.model.Artist(id = "", name = it.artistName, imageUrl = null),
                    durationMs = it.durationMs,
                    album = null,
                    streamUrl = it.streamUrl,
                    coverArtUrl = it.coverArtUrl,
                    isAvailableOffline = false
                )
            }
        }
    }

    override suspend fun toggleFavorite(song: Song) {
        // Simple toggle implementation
        // For production, check if exists and delete, else insert
    }

    override fun isFavorite(songId: String): Flow<Boolean> {
        return dao.isFavorite(songId)
    }
}

@Singleton
class AudioRepositoryImpl @Inject constructor(
    private val api: AudioApiService
) : AudioRepository {
    override suspend fun getSongDetail(songId: String, policy: CachePolicy): Result<Song, AppError> {
        return safeApiCall(
            apiCall = { api.getSongDetail(songId) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun getRecommendations(): Result<List<Song>, AppError> {
         return safeApiCall(
            apiCall = { api.getRecommendations() },
            mapper = { dto -> dto.results.map { it.toDomain() } }
        )
    }
}

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val api: AudioApiService,
    private val dao: AudioDao
) : SearchRepository {
    override suspend fun searchSongs(query: SearchQuery): Result<List<Song>, AppError> {
        return safeApiCall(
            apiCall = { api.searchSongs(query.query, query.limit, query.offset) },
            mapper = { dto -> dto.results.map { it.toDomain() } }
        )
    }

    override fun getRecentSearches(): Flow<List<String>> {
        return dao.getRecentSearches().map { list -> list.map { it.query } }
    }

    override suspend fun saveSearchQuery(query: String) {
        dao.insertRecentSearch(RecentSearchEntity(query))
    }

    override suspend fun clearRecentSearches() {
        dao.clearRecentSearches()
    }
}
