package com.example.petmanagement.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
//import androidx.compose.ui.tooling.data.EmptyGroup.data
import androidx.fragment.app.Fragment
import com.example.petmanagement.R

class Home: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        var picbtn: ImageButton = view.findViewById(R.id.torokubtn)
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
    }

}