package com.rentify.database.photodb

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photoentity")
    fun getAll(): List<PhotoEntity>

//    @Query("SELECT * FROM fileentity WHERE uid IN (:fileentityIds)")
//    fun loadAllByIds(fileentityIds: IntArray): List<FIleEntity>

    @Query("UPDATE photoentity SET photoname=:file AND foldername=:folder WHERE uid = :uid_")
    fun update(uid_: Int, file: String, folder: String): Int

    @Insert(onConflict = IGNORE)
    suspend fun insert(photoentity: PhotoEntity)

    @Insert(onConflict = REPLACE)
    suspend fun update(photoentity: PhotoEntity)

    @Insert
    fun insertAll(vararg photoentity: PhotoEntity)

    @Delete
    fun delete(fileentity: PhotoEntity)

    @Query("DELETE FROM photoentity")
    suspend fun deleteAll()
}


@Database(entities = arrayOf(PhotoEntity::class), version = 1)
abstract class PhotoDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}