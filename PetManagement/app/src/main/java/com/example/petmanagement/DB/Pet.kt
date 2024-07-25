package com.example.petmanagement.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pet(
    @PrimaryKey(autoGenerate = true) val id: Int,
    //名前
    val name: String,
    //種別
    val type: String,
    //犬種
    val breed: String,
    //誕生日
    val birthdate: String,
    //お迎えした日
    val adoptionDate: String,
    //性別
    val gender: String,
    //写真のURL
    val iconPhotoUrl: String
    )
@Entity
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val imageUrl: String
)

@Entity
data class WeightRecord(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val petId: Int,
    val weight: Float,
    val date: String
)

@Entity
data class HealthRecord(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val petId: Int,
    val energy: Int,
    val urine: Boolean,
    val stool: Boolean,
    val water: Float,
    val food: Float,
    val photoUrl: String?,
    val date: String
)

@Entity
data class Diary(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val petId: Int,
    val date: String,
    val time: String,
    val content: String,
    val photoUrl: String?
)