package com.example.petmanagement.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petmanagement.DB.ChatMessage
import com.example.petmanagement.R
import com.example.petmanagement.chat.ChatAdapter
import com.example.petmanagement.databinding.FragmentDiaryBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Diary : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter
    private lateinit var database: DatabaseReference
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        val view = binding.root

        // バインディングオブジェクトを使用してビューにアクセス
        recyclerView = binding.recyclerView
        editTextMessage = binding.editTextMessage
        buttonSend = binding.buttonSend

        adapter = ChatAdapter(messages)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // レイアウトファイルを表示
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // データベースからデータを取得
        database = FirebaseDatabase.getInstance().reference.child("messages")

        buttonSend.setOnClickListener {
            val message = editTextMessage.text.toString()
            if (message.isNotBlank()) {
                val chatMessage = ChatMessage("User", message, "")
                database.push().setValue(chatMessage)
                editTextMessage.text.clear()
            }
        }

        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                chatMessage?.let {
                    messages.add(it)
                    adapter.notifyItemInserted(messages.size - 1)
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
