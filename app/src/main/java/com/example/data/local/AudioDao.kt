package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao {
    @Query("SELECT * FROM favorite_songs ORDER BY addedAt DESC")
    fun getFavoriteSongs(): Flow<List<FavoriteSongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(song: FavoriteSongEntity)

    @Query("DELETE FROM favorite_songs WHERE id = :songId")
    suspend fun deleteFavorite(songId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_songs WHERE id = :songId)")
    fun isFavorite(songId: String): Flow<Boolean>

    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT 20")
    fun getRecentSearches(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(query: RecentSearchEntity)

    @Query("DELETE FROM recent_searches")
    suspend fun clearRecentSearches()
}
