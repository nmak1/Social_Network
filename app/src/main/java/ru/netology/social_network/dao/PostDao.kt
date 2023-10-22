package ru.netology.social_network.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.social_network.dto.Post
import ru.netology.social_network.entity.PostEntity


@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity WHERE views = 0 ORDER BY id DESC")

    fun getAll(): Flow<List<PostEntity>>

    @Insert(onConflict = REPLACE)
    suspend fun insert(post: PostEntity)

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, PostEntity>

    @Insert(onConflict = REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Insert(onConflict = IGNORE)
    suspend fun insertInvisible(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)
    suspend fun save(post: PostEntity) =
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)

    @Query(
        """
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likeByMe THEN -1 ELSE 1 END,
        likeByMe = CASE WHEN likeByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    suspend fun likeById(id: Long)

    @Query("UPDATE PostEntity SET views = 0 WHERE views = 1")
    suspend fun viewedPosts()

    @Query("DELETE FROM PostEntity")
    suspend fun clear()

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)
    fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
    fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
}