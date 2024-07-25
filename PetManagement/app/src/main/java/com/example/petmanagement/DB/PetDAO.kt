package com.example.petmanagement.DB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: Pet)

    @Update
    suspend fun updatePet(pet: Pet)

    @Query("SELECT * FROM Pet WHERE id = :id")
    suspend fun getPetById(id: Int): Pet?

    @Query("DELETE FROM Pet WHERE id = :id")
    suspend fun deletePet(id: Int)

    @Query("SELECT * FROM Pet")
    suspend fun getAllPets(): List<Pet>
}

@Dao
interface ImageDao {
    @Insert
    suspend fun insertImage(imageEntity: ImageEntity)

    @Query("SELECT * FROM ImageEntity")
    suspend fun getAllImages(): List<ImageEntity>
    @Query("SELECT imageUrl FROM ImageEntity ORDER BY id DESC LIMIT 1")
    suspend fun getLatestImageUrl(): String?
}
@Dao
interface WeightRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightRecord(weightRecord: WeightRecord)

    @Update
    suspend fun updateWeightRecord(weightRecord: WeightRecord)

    @Query("SELECT * FROM WeightRecord WHERE petId = :petId")
    suspend fun getWeightRecordsForPet(petId: Int): List<WeightRecord>

    @Query("DELETE FROM WeightRecord WHERE id = :id")
    suspend fun deleteWeightRecord(id: Int)
}

@Dao
interface HealthRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(healthRecord: HealthRecord)

    @Update
    suspend fun updateHealthRecord(healthRecord: HealthRecord)

    @Query("SELECT * FROM HealthRecord WHERE petId = :petId")
    suspend fun getHealthRecordsForPet(petId: Int): List<HealthRecord>

    @Query("DELETE FROM HealthRecord WHERE id = :id")
    suspend fun deleteHealthRecord(id: Int)
}

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(diary: Diary)

    @Update
    suspend fun updateDiary(diary: Diary)

    @Query("SELECT * FROM Diary WHERE petId = :petId")
    suspend fun getDiariesForPet(petId: Int): List<Diary>

    @Query("DELETE FROM Diary WHERE id = :id")
    suspend fun deleteDiary(id: Int)
}