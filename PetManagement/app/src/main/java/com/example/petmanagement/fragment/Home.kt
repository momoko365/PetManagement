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
import android.widget.Toast
//import androidx.compose.ui.tooling.data.EmptyGroup.data
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.petmanagement.DB.ImageDao
import com.example.petmanagement.DB.ImageEntity
import com.example.petmanagement.DB.PetDao
import com.example.petmanagement.DB.PetDatabase
import com.example.petmanagement.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class Home: Fragment() {
    private lateinit var db: PetDatabase
    private lateinit var dao: PetDao


    private lateinit var imagedao: ImageDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        var picbtn: ImageButton = view.findViewById(R.id.torokubtn)

        // データベース初期化
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db = Room.databaseBuilder(
                    requireContext(),
                    PetDatabase::class.java,
                    "pet.db"
                ).fallbackToDestructiveMigration().build()
                dao = db.petDAO()
                imagedao = db.imageDAO()
            }
displayLatestImage()
        }
        picbtn.setOnClickListener {
//ボタンが押されたらギャラリーを開く
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, READ_REQUEST_CODE)
        }
        return view


    }
    companion object {
        private const val READ_REQUEST_CODE: Int = 42
    }

    //写真が選択された後の動き
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if (resultCode == Activity.RESULT_OK && requestCode == READ_REQUEST_CODE) {
            data?.data?.let { uri ->
                try {
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
                    val image = BitmapFactory.decodeStream(inputStream)
                    requireView().findViewById<ImageView>(R.id.imageView).setImageBitmap(image)
                } catch (e: Exception) {
                    Toast.makeText(requireActivity(), "エラーが発生しました", Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == READ_REQUEST_CODE) {
            data?.data?.also { uri ->
                uploadImageToFirebase(uri)
            }
        }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val storageInstance = FirebaseStorage.getInstance()
        val ref = storageInstance.reference.child("images/" + UUID.randomUUID().toString())
        ref.putFile(uri)
            .addOnSuccessListener {
                // アップロードが成功したら、画像のURLを取得
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    // 取得したURLをローカルデータベースに保存
                    saveImageToDatabase(imageUrl)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireActivity(), "Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveImageToDatabase(imageUrl: String) {
        val imageEntity = ImageEntity(id=0,imageUrl = imageUrl)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.imageDAO().insertImage(imageEntity)
            }
        }
    }
    private fun displayLatestImage() {
        lifecycleScope.launch {
            val latestImageUrl = withContext(Dispatchers.IO) {
                db.imageDAO().getLatestImageUrl()
            }
            latestImageUrl?.let { url ->
                // ここでGlideなどのライブラリを使用してImageViewに画像を表示
                Glide.with(requireContext()).load(url).into(requireView().findViewById<ImageView>(R.id.imageView))
            }
        }
    }
}