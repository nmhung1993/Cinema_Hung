package com.apitiny.administrator.cinema_hung

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.app.DatePickerDialog
import android.content.Intent
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.upload.*

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