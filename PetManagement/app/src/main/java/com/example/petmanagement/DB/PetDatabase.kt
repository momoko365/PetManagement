package com.example.petmanagement.DB

import androidx.compose.ui.Modifier
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Pet::class,WeightRecord::class,HealthRecord::class,Diary::class], version = 1, exportSchema = false)
abstract class PetDatabase : RoomDatabase(){
    abstract fun petDAO():PetDao
    abstract fun weightDAO():WeightRecordDao
    abstract fun healthDAO():HealthRecordDao
    abstract fun diaryDAO():DiaryDao
}