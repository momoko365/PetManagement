package com.example.petmanagement.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.petmanagement.R
import com.github.mikephil.charting.charts.LineChart
import com.google.firebase.database.DatabaseReference

class Weight : Fragment(){
    private lateinit var database: DatabaseReference
    private lateinit var chart: LineChart
    private lateinit var editTextWeight: EditText
    private lateinit var buttonRegisterWeight: Button
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return  inflater.inflate(R.layout.fragment_weight, container, false)

    }
}