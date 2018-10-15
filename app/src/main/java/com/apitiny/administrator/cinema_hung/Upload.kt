package com.apitiny.administrator.cinema_hung

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class Upload : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upload)

        val theloai = arrayOf("Hành động", "Tâm lý", "Kinh dị", "Khoa học viễn tưởng", "Hài")
        val theloai_onClick = findViewById<TextView>(R.id.tvtheloai)
        //theloai_onClick.setOnClickListener
    }
}
