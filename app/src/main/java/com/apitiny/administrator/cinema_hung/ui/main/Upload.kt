package com.apitiny.administrator.cinema_hung.ui.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.app.DatePickerDialog
import android.widget.EditText
import com.apitiny.administrator.cinema_hung.R
import java.util.*

class Upload : AppCompatActivity() {

    lateinit var editTextDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upload)

        editTextDate = findViewById(R.id.ngayphathanh)
    }

    fun openPickerDate(view:View){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth -> editTextDate.setText("" + dayOfMonth + "/" + month + "/" + year)
        }, year,month,day)
        dpd.show()
    }
}