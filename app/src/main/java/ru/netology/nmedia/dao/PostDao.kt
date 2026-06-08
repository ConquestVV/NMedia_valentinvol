package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverter
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.enumeration.AttachmentType

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getData(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) FROM PostEntity WHERE hidden = 1")
    fun getNewerCount(): Flow<Int>

    @Query("UPDATE PostEntity SET hidden = 0 WHERE hidden = 1")
    suspend fun showAllNewer()

    @Query("SELECT COUNT(*) FROM PostEntity WHERE id = :id")
    suspend fun countById(id: Long): Int

    @Query("SELECT COALESCE(MAX(id), 0) FROM PostEntity")
    suspend fun maxId(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)

    suspend fun save(post: PostEntity) =
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)

    @Query("""
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """)
    fun like(id: Long)

    @Query("SELECT * FROM PostEntity WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PostEntity?

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun remove(id: Long)
}

class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name
}