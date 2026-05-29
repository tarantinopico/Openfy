package com.example.data.network

import com.example.domain.model.Album
import com.example.domain.model.AppError
import com.example.domain.model.Artist
import com.example.domain.model.Result
import com.example.domain.model.Song
import retrofit2.Response
import java.io.IOException

fun SongDto.toDomain(): Song {
    return Song(
        id = id,
        title = title,
        durationMs = durationMs,
        artist = Artist(id = artist.id, name = artist.name, imageUrl = artist.imageUrl),
        album = album?.let { Album(id = it.id, title = it.title, releaseYear = it.releaseYear, coverArtUrl = it.coverArtUrl) },
        streamUrl = streamUrl,
        coverArtUrl = coverArtUrl,
        isAvailableOffline = false
    )
}

suspend fun <T, R> safeApiCall(
    apiCall: suspend () -> Response<T>,
    mapper: (T) -> R
): Result<R, AppError> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.Success(mapper(body))
            } else {
                Result.Error(AppError.NetworkError.SerializationError)
            }
        } else {
            Result.Error(AppError.NetworkError.HttpError(response.code(), response.message()))
        }
    } catch (e: IOException) {
        Result.Error(AppError.NetworkError.NoConnection)
    } catch (e: Exception) {
        Result.Error(AppError.NetworkError.Unknown)
    }
}
