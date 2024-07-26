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

        petDatabase = FirebaseDatabase.getInstance().reference.child("pets")
        imageDatabase = FirebaseDatabase.getInstance().reference.child("images")
        storage = FirebaseStorage.getInstance().reference.child("images")

        // ペットデータを取得して表示
        petDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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

    companion object {
        private const val READ_REQUEST_CODE: Int = 42
    }
}
//class Home: Fragment() {
//    private lateinit var db: PetDatabase
//    private lateinit var dao: PetDao
//    private lateinit var imagedao: ImageDao
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_home, container, false)
//        var picbtn: ImageButton = view.findViewById(R.id.torokubtn)
//
//        // データベース初期化
//        lifecycleScope.launch {
//            withContext(Dispatchers.IO) {
//                db = Room.databaseBuilder(
//                    requireContext(),
//                    PetDatabase::class.java,
//                    "pet.db"
//                ).fallbackToDestructiveMigration().build()
//                dao = db.petDAO()
//                imagedao = db.imageDAO()
//            }
//displayLatestImage()
//            displayLatestImage()
//            val pets = dao.getAllPets()
//            if (pets.isNotEmpty()) {
//                val name: TextView = view.findViewById(R.id.name)
//
//                var typedobutu: TextView = view.findViewById(R.id.type)
//                val gender: TextView =view.findViewById(R.id.gender)
//                val breed: TextView = view.findViewById(R.id.breed)
//                val birhday: TextView = view.findViewById(R.id.birthday)
//                val comehomeday: TextView = view.findViewById(R.id.comehomeday)
//                val pet = pets[0] // リストの最初のペットを取得
//                withContext(Dispatchers.Main) {
//                    // UIスレッドでテキストビューに値を設定
//                    name.text = pet.name
//                    // seigodate.text = // ここに計算した成犬の日付を設定
//                    typedobutu.text = pet.type
//                    gender.text = pet.gender
//                    breed.text = pet.breed
//                    birhday.text = pet.birthdate
//                    comehomeday.text = pet.adoptionDate
//                }
//            }
//        }
//
//
//
////        lifecycleScope.launch {
////            val pets = withContext(Dispatchers.IO) {
////                dao.getAllPets()
////            }
////            if (pets.isNotEmpty()) {
////                val pet = pets[0] // リストの最初のペットを取得
////                // UIスレッドでテキストビューに値を設定
////                withContext(Dispatchers.Main) {
////                    name.text = pet.name
//////                    seigodate.text = // ここに計算した成犬の日付を設定
////                        typedobutu.text = pet.type
////                    gender.text = pet.gender
////                    breed.text = pet.breed
////                    birhday.text = pet.birthdate
////                    comehomeday.text = pet.adoptionDate
////                }
////            }
////        }
//        picbtn.setOnClickListener {
////ボタンが押されたらギャラリーを開く
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//                addCategory(Intent.CATEGORY_OPENABLE)
//                type = "image/*"
//            }
//            startActivityForResult(intent, READ_REQUEST_CODE)
//        }
//        return view
//
//
//    }
//    companion object {
//        private const val READ_REQUEST_CODE: Int = 42
//    }
//
//    //写真が選択された後の動き
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
//        if (resultCode == Activity.RESULT_OK && requestCode == READ_REQUEST_CODE) {
//            data?.data?.let { uri ->
//                try {
//                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
//                    val image = BitmapFactory.decodeStream(inputStream)
//                    requireView().findViewById<ImageView>(R.id.imageView).setImageBitmap(image)
//                } catch (e: Exception) {
//                    Toast.makeText(requireActivity(), "エラーが発生しました", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//        if (resultCode == Activity.RESULT_OK && requestCode == READ_REQUEST_CODE) {
//            data?.data?.also { uri ->
//                uploadImageToFirebase(uri)
//            }
//        }
//    }
//
//    private fun uploadImageToFirebase(uri: Uri) {
//        val storageInstance = FirebaseStorage.getInstance()
//        val ref = storageInstance.reference.child("images/" + UUID.randomUUID().toString())
//        ref.putFile(uri)
//            .addOnSuccessListener {
//                // アップロードが成功したら、画像のURLを取得
//                ref.downloadUrl.addOnSuccessListener { downloadUri ->
//                    val imageUrl = downloadUri.toString()
//                    // 取得したURLをローカルデータベースに保存
//                    saveImageToDatabase(imageUrl)
//                }
//            }
//            .addOnFailureListener {
//                Toast.makeText(requireActivity(), "Failed", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun saveImageToDatabase(imageUrl: String) {
//        val imageEntity = ImageEntity(id=0,imageUrl = imageUrl)
//        lifecycleScope.launch {
//            withContext(Dispatchers.IO) {
//                db.imageDAO().insertImage(imageEntity)
//            }
//        }
//    }
//    private fun displayLatestImage() {
//        lifecycleScope.launch {
//            val latestImageUrl = withContext(Dispatchers.IO) {
//                db.imageDAO().getLatestImageUrl()
//            }
//            latestImageUrl?.let { url ->
//                // ここでGlideなどのライブラリを使用してImageViewに画像を表示
//                Glide.with(requireContext()).load(url).into(requireView().findViewById<ImageView>(R.id.imageView))
//            }
//        }
//    }
//}