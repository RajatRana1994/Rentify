package com.rentify.database.photodb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhotoEntity(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "photoname") val photoname: String?="",
    @ColumnInfo(name = "foldername") val foldername: String?=""
)