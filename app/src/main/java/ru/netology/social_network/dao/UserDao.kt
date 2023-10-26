package ru.netology.social_network.dao

import androidx.room.*
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow
import ru.netology.social_network.entity.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM UserEntity ORDER BY name ASC")
    fun getAll(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: List<UserEntity>)

    @Query("DELETE FROM UserEntity WHERE id = :id")
    suspend fun removeById(id: Long)
}