package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SongDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "duration_ms") val durationMs: Long,
    @Json(name = "artist") val artist: ArtistDto,
    @Json(name = "album") val album: AlbumDto?,
    @Json(name = "stream_url") val streamUrl: String,
    @Json(name = "cover_art_url") val coverArtUrl: String?
)

@JsonClass(generateAdapter = true)
data class ArtistDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "image_url") val imageUrl: String?
)

@JsonClass(generateAdapter = true)
data class AlbumDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "release_year") val releaseYear: Int?,
    @Json(name = "cover_art") val coverArtUrl: String?
)

@JsonClass(generateAdapter = true)
data class SearchResponseDto(
    @Json(name = "results") val results: List<SongDto>,
    @Json(name = "total") val total: Int
)
