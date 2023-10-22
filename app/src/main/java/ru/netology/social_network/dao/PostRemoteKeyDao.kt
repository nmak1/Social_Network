package ru.netology.social_network.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import ru.netology.social_network.entity.PostRemoteKeyEntity


@Dao
interface PostRemoteKeyDao {

    @Query("SELECT COUNT(*) == 0 FROM PostRemoteKeyEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT MAX(`key`) FROM PostRemoteKeyEntity")
    suspend fun max(): Long?

    @Query("SELECT MIN(`key`) FROM PostRemoteKeyEntity")
    suspend fun min(): Long?

    @Insert(onConflict = REPLACE)
    suspend fun insert(postRemoteKeyEntity: PostRemoteKeyEntity)

    @Insert(onConflict = REPLACE)
    suspend fun insert(postRemoteKeyEntity: List<PostRemoteKeyEntity>)

    @Query("DELETE FROM PostRemoteKeyEntity")
    suspend fun clear()
}