package com.example.petmanagement.DB

import androidx.compose.ui.Modifier
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Pet::class,WeightRecord::class,HealthRecord::class,Diary::class,ImageEntity::class], version =3, exportSchema = false)
abstract class PetDatabase : RoomDatabase(){
    abstract fun petDAO():PetDao
    abstract fun weightDAO():WeightRecordDao
    abstract fun healthDAO():HealthRecordDao
    abstract fun diaryDAO():DiaryDao
    abstract fun imageDAO():ImageDao
}