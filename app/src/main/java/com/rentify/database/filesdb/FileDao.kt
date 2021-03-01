package com.rentify.database.filesdb

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import com.rentify.local.InputDataEntity

@Dao
interface FileDao {
    @Query("SELECT * FROM fileentity")
    fun getAll(): List<FileEntity>

//    @Query("SELECT * FROM fileentity WHERE uid IN (:fileentityIds)")
//    fun loadAllByIds(fileentityIds: IntArray): List<FIleEntity>

//    @Query("UPDATE fileentity SET Filename=:file AND folder=:folder AND raw_data=:raw_data WHERE uid = :uid_")
//    fun update(uid_: Int, file: String, folder: String, raw_data: InputDataEntity): Int

    @Insert(onConflict = IGNORE)
    suspend fun insert(fileentity: FileEntity)

    @Insert(onConflict = REPLACE)
    suspend fun update(fileentity: FileEntity)

    @Insert
    fun insertAll(vararg fileentity: FileEntity)

    @Delete
    fun delete(fileentity: FileEntity)

    @Query("DELETE FROM fileentity")
    suspend fun deleteAll()
}


@Database(entities = arrayOf(FileEntity::class), version = 1)
abstract class FileDatabase : RoomDatabase() {
    abstract fun FileDao(): FileDao
}