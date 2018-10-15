package com.apitiny.administrator.cinema_hung

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uploadButton = findViewById<Button>(R.id.upload)
        uploadButton.setOnClickListener{
                val intent = Intent(applicationContext, Upload::class.java)
                startActivity(intent)
            }
        }
    }
