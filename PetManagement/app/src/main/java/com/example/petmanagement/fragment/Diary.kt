package com.example.petmanagement.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.petmanagement.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Diary : Fragment() {
    // UIコンポーネントの宣言
    private lateinit var linearLayout: LinearLayout
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private lateinit var buttonSelectImage: Button
    private lateinit var imageView: ImageView
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference.child("messages")
    private val storage = FirebaseStorage.getInstance().reference.child("images")
    private var selectedImageUri: Uri? = null

    // Fragmentのビューを作成するメソッド
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_diary, container, false)
        // UIコンポーネントの初期化
        linearLayout = view.findViewById(R.id.linearLayout)
        editTextMessage = view.findViewById(R.id.editTextMessage)
        buttonSend = view.findViewById(R.id.buttonSend)
        buttonSelectImage = view.findViewById(R.id.buttonSelectImage)
        imageView = view.findViewById(R.id.imageView)
        auth = FirebaseAuth.getInstance()
// 送信ボタンのクリックリスナー
        buttonSend.setOnClickListener {
            val message = editTextMessage.text.toString()
            val user = auth.currentUser
            if (message.isNotBlank() && user != null) {
                val email = user.email ?: "Unknown"
                if (selectedImageUri != null) {
                    saveImageToStorage(email, message, selectedImageUri!!)
                } else {
                    saveMessageToDatabase(email, message, null)
                }
                editTextMessage.text.clear()
                imageView.visibility = View.GONE
                selectedImageUri = null
            }
        }
// 画像選択ボタンのクリックリスナー
        buttonSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        // Firebase Realtime Databaseのリスナーを設定
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val email = snapshot.child("email").getValue(String::class.java) ?: "Unknown"
                val message = snapshot.child("message").getValue(String::class.java) ?: ""
                val imageUrl = snapshot.child("imageUrl").getValue(String::class.java)
                addMessageToLayout(email, message, imageUrl)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // 必要に応じて実装
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // 必要に応じて実装
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // 必要に応じて実装
            }

            override fun onCancelled(error: DatabaseError) {
                // エラーハンドリング
            }
        })

        return view
    }
    // 画像選択の結果を受け取るメソッド
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            imageView.setImageURI(selectedImageUri)
            imageView.visibility = View.VISIBLE
        }
    }
    // メッセージをレイアウトに追加するメソッド
    private fun addMessageToLayout(email: String, message: String, imageUrl: String?) {
        val textView = TextView(context).apply {
            text = "$email: $message"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        linearLayout.addView(textView)

        if (imageUrl != null) {
            val imageView = ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    200
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            // Glideなどのライブラリを使用して画像を読み込む
            Glide.with(this).load(imageUrl).into(imageView)
            linearLayout.addView(imageView)
        }
    }
    // メッセージをFirebase Realtime Databaseに保存するメソッド
    private fun saveMessageToDatabase(email: String, message: String, imageUrl: String?) {
        val messageData = mapOf(
            "email" to email,
            "message" to message,
            "imageUrl" to imageUrl
        )
        database.push().setValue(messageData)
    }
    // 画像をFirebase Storageに保存するメソッド
    private fun saveImageToStorage(email: String, message: String, imageUri: Uri) {
        val imageRef: StorageReference = storage.child(imageUri.lastPathSegment!!)
        imageRef.putFile(imageUri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                saveMessageToDatabase(email, message, uri.toString())
            }
        }
    }
}