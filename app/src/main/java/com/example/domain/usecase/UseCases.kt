package com.example.domain.usecase

import com.example.domain.model.AppError
import com.example.domain.model.Result
import com.example.domain.model.SearchQuery
import com.example.domain.model.Song
import com.example.domain.repository.SearchRepository
import com.example.domain.repository.AudioRepository
import javax.inject.Inject

class SearchMusicUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String, limit: Int = 20): Result<List<Song>, AppError> {
        if (query.isBlank()) return Result.Success(emptyList())
        searchRepository.saveSearchQuery(query)
        return searchRepository.searchSongs(SearchQuery(query, limit))
    }
}

class GetSongDetailUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) {
    suspend operator fun invoke(songId: String): Result<Song, AppError> {
        return audioRepository.getSongDetail(songId)
    }
}
