package com.example.petmanagement.fragment

//import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.petmanagement.DB.Pet
import com.example.petmanagement.DB.PetDao
import com.example.petmanagement.DB.PetDatabase
import com.example.petmanagement.R
import com.example.petmanagement.databinding.FragmentHomeRegiBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeRegi : Fragment() {
    private var _binding: FragmentHomeRegiBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: PetDatabase
    private lateinit var dao: PetDao



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentHomeRegiBinding.inflate(inflater, container, false)
        val view = binding.root
        // データベース初期化
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db = Room.databaseBuilder(
                    requireContext(),
                    PetDatabase::class.java,
                    "pet.db"
                ).fallbackToDestructiveMigration().build()
                dao = db.petDAO()
            }

        }


        val torokubtn : Button = view.findViewById(R.id.button)

        torokubtn.setOnClickListener {
            val name: EditText = view.findViewById(R.id.nameT)
            val gender: EditText = view.findViewById(R.id.genderT)
            val animaltype: EditText = view.findViewById(R.id.animalT)
            val bleed: EditText = view.findViewById(R.id.bleedT)
            val birth: EditText = view.findViewById(R.id.birthT)
            val house: EditText = view.findViewById(R.id.houseT)


            if (name.text.toString().isBlank() || gender.text.toString().isBlank() || animaltype.text.toString().isBlank() || bleed.text.toString().isBlank() || birth.text.toString().isBlank() || house.text.toString().isBlank()) {
                Toast.makeText(requireContext(), "全ての項目を入力してください", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val pet = Pet(
                    id = 0,
                    name = name.text.toString(),
                    type = animaltype.text.toString(),
                    breed = bleed.text.toString(),
                    birthdate = birth.text.toString(),
                    adoptionDate = house.text.toString(),
                    gender = gender.text.toString(),
                    iconPhotoUrl = ""
                )


                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        dao.insertPet(pet)
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "登録が完了しました", Toast.LENGTH_SHORT)
                            .show()
                    }


                }
            }
        }
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}