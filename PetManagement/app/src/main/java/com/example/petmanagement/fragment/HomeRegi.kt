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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeRegi : Fragment() {

    private lateinit var name: EditText
    private lateinit var  gender: EditText
    private lateinit var animaltype: EditText
    private lateinit var bleed: EditText
    private lateinit var birth: EditText
    private lateinit var house: EditText
    private lateinit var torokubtn: Button
    private lateinit var database: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_home_regi, container, false)

        name=view.findViewById(R.id.nameT)
             gender=view.findViewById(R.id.genderT)
            animaltype = view.findViewById(R.id.animalT)
            bleed = view.findViewById(R.id.bleedT)
            birth = view.findViewById(R.id.birthT)
             house = view.findViewById(R.id.houseT)
        torokubtn=view.findViewById(R.id.button)
        database = FirebaseDatabase.getInstance().reference.child("pets")
        torokubtn.setOnClickListener{
            val petName = name.text.toString()
            val petGender = gender.text.toString()
            val petType = animaltype.text.toString()
            val petBleed = bleed.text.toString()
            val petBirth = birth.text.toString()
            val petHouse = house.text.toString()

            if (petName.isBlank() || petGender.isBlank() || petType.isBlank() || petBleed.isBlank() || petBirth.isBlank() || petHouse.isBlank()) {
                Toast.makeText(requireContext(), "全ての項目を入力してください", Toast.LENGTH_SHORT).show()
            } else {
                val petData = mapOf(
                    "name" to petName,
                    "gender" to petGender,
                    "type" to petType,
                    "bleed" to petBleed,
                    "birth" to petBirth,
                    "house" to petHouse
                )
                database.push().setValue(petData)
                Toast.makeText(requireContext(), "登録が完了しました", Toast.LENGTH_SHORT).show()
                clearFields()
            }
        }

        return view
    }
    private fun clearFields() {
        name.text.clear()
        gender.text.clear()
        animaltype.text.clear()
        bleed.text.clear()
        birth.text.clear()
        house.text.clear()
    }
}













//    private var _binding: FragmentHomeRegiBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var db: PetDatabase
//    private lateinit var dao: PetDao



//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        super.onCreateView(inflater, container, savedInstanceState)
//        val view = FragmentHomeRegiBinding.inflate(inflater, container, false)
//        val view = binding.root
//        // データベース初期化
//        lifecycleScope.launch {
//            withContext(Dispatchers.IO) {
//                db = Room.databaseBuilder(
//                    requireContext(),
//                    PetDatabase::class.java,
//                    "pet.db"
//                ).fallbackToDestructiveMigration().build()
//                dao = db.petDAO()
//            }
//
//        }
//
//
//        val torokubtn : Button = view.findViewById(R.id.button)
//
//        torokubtn.setOnClickListener {
//            val name: EditText = view.findViewById(R.id.nameT)
//            val gender: EditText = view.findViewById(R.id.genderT)
//            val animaltype: EditText = view.findViewById(R.id.animalT)
//            val bleed: EditText = view.findViewById(R.id.bleedT)
//            val birth: EditText = view.findViewById(R.id.birthT)
//            val house: EditText = view.findViewById(R.id.houseT)
//
//
//            if (name.text.toString().isBlank() || gender.text.toString().isBlank() || animaltype.text.toString().isBlank() || bleed.text.toString().isBlank() || birth.text.toString().isBlank() || house.text.toString().isBlank()) {
//                Toast.makeText(requireContext(), "全ての項目を入力してください", Toast.LENGTH_SHORT)
//                    .show()
//            } else {
//                val pet = Pet(
//                    id = 0,
//                    name = name.text.toString(),
//                    type = animaltype.text.toString(),
//                    breed = bleed.text.toString(),
//                    birthdate = birth.text.toString(),
//                    adoptionDate = house.text.toString(),
//                    gender = gender.text.toString(),
//                    iconPhotoUrl = ""
//                )
//
//
//                lifecycleScope.launch {
//                    withContext(Dispatchers.IO) {
//                        dao.insertPet(pet)
//                    }
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(requireContext(), "登録が完了しました", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//
//
//                }
//            }
//        }
//        return view
//    }
////    override fun onDestroyView() {
////        super.onDestroyView()
////        _binding = null
////    }
//}