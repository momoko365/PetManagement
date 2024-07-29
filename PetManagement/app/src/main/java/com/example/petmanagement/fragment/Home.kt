package com.example.petmanagement.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
//import androidx.compose.ui.tooling.data.EmptyGroup.data
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.petmanagement.DB.ImageDao
import com.example.petmanagement.DB.ImageEntity
import com.example.petmanagement.DB.Pet
import com.example.petmanagement.DB.PetDao
import com.example.petmanagement.DB.PetDatabase
import com.example.petmanagement.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
class Home : Fragment() {

    private lateinit var typedobutu: TextView
    private lateinit var name: TextView
    private lateinit var gender: TextView
    private lateinit var breed: TextView
    private lateinit var birhday: TextView
    private lateinit var comehomeday: TextView
    private lateinit var imageView: ImageView
    private lateinit var petDatabase: DatabaseReference
    private lateinit var imageDatabase: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        name = view.findViewById(R.id.name)
        typedobutu = view.findViewById(R.id.type)
        gender = view.findViewById(R.id.gender)
        breed = view.findViewById(R.id.breed)
        birhday = view.findViewById(R.id.birthday)
        comehomeday = view.findViewById(R.id.comehomeday)
        imageView = view.findViewById(R.id.imageView)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        userId?.let {
            petDatabase = FirebaseDatabase.getInstance().reference.child("users").child(it).child("pets")
            imageDatabase = FirebaseDatabase.getInstance().reference.child("users").child(it).child("images")
            storage = FirebaseStorage.getInstance().reference.child("images").child(it)
        }

        // ペットデータを取得して表示
        petDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val petName = data.child("name").getValue(String::class.java)
                        val petGender = data.child("gender").getValue(String::class.java)
                        val petType = data.child("type").getValue(String::class.java)
                        val petBleed = data.child("bleed").getValue(String::class.java)
                        val petBirth = data.child("birth").getValue(String::class.java)
                        val petHouse = data.child("house").getValue(String::class.java)

                        name.text = petName
                        gender.text = petGender
                        typedobutu.text = petType
                        breed.text = petBleed
                        birhday.text = petBirth
                        comehomeday.text = petHouse
                    }
                } else {
                    Toast.makeText(requireContext(), "ペットデータが存在しません", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "データの取得に失敗しました: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // 画像データを取得して表示
        imageDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val imageUrl = data.child("imageUrl").getValue(String::class.java)
                    if (imageUrl != null) {
                        Glide.with(this@Home).load(imageUrl).into(imageView)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "画像の取得に失敗しました: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // 画像選択ボタンのクリックリスナー
        view.findViewById<View>(R.id.torokubtn).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, READ_REQUEST_CODE)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == READ_REQUEST_CODE) {
            data?.data?.let { uri ->
                uploadImageToFirebase(uri)
            }
        }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val ref = storage.child(UUID.randomUUID().toString())
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    saveImageToDatabase(imageUrl)
                    savePetToDatabase()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireActivity(), "画像のアップロードに失敗しました", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveImageToDatabase(imageUrl: String) {
        val imageData = mapOf("imageUrl" to imageUrl)
        imageDatabase.push().setValue(imageData)
    }

    private fun savePetToDatabase() {
        val petData = mapOf(
            "name" to name.text.toString(),
            "gender" to gender.text.toString(),
            "type" to typedobutu.text.toString(),
            "bleed" to breed.text.toString(),
            "birth" to birhday.text.toString(),
            "house" to comehomeday.text.toString()
        )
        petDatabase.push().setValue(petData)
    }

    companion object {
        private const val READ_REQUEST_CODE: Int = 42
    }
}