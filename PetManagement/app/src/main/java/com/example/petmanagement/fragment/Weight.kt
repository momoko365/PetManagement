package com.example.petmanagement.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.petmanagement.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Weight : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var chart: LineChart
    private lateinit var editTextDate: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var buttonRegisterWeight: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_weight, container, false)

        database = FirebaseDatabase.getInstance().reference.child("weights")
        chart = view.findViewById(R.id.chart)
        editTextDate = view.findViewById(R.id.editTextDate)
        editTextWeight = view.findViewById(R.id.editTextWeight)
        buttonRegisterWeight = view.findViewById(R.id.buttonRegisterWeight)

        // 日付選択用のDatePickerDialogを設定
        editTextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                editTextDate.setText(selectedDate)
            }, year, month, day)
            datePickerDialog.show()
        }

        buttonRegisterWeight.setOnClickListener {
            val weight = editTextWeight.text.toString().toFloatOrNull()
            val date = editTextDate.text.toString()
            if (weight != null && date.isNotEmpty()) {
                val weightData = mapOf("date" to date, "weight" to weight)
                database.push().setValue(weightData)
                editTextWeight.text.clear()
                editTextDate.text.clear()
            }
        }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entries = mutableListOf<Entry>()
                for (data in snapshot.children) {
                    val date = data.child("date").getValue(String::class.java)
                    val weight = data.child("weight").getValue(Float::class.java)
                    if (date != null && weight != null) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val dateObj = dateFormat.parse(date)
                        val xValue = dateObj?.time?.toFloat() ?: 0f
                        entries.add(Entry(xValue, weight))
                    }
                }
                val dataSet = LineDataSet(entries, "体重")
                val lineData = LineData(dataSet)
                chart.data = lineData

                // X軸の設定
                val xAxis = chart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.valueFormatter = object : ValueFormatter() {
                    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    override fun getFormattedValue(value: Float): String {
                        return dateFormat.format(Date(value.toLong()))
                    }
                }

                chart.invalidate()
            }

            override fun onCancelled(error: DatabaseError) {
                // エラーハンドリング
            }
        })

        return view
    }
}