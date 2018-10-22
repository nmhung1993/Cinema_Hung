package com.apitiny.administrator.cinema_hung.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.apitiny.administrator.cinema_hung.R
import com.apitiny.administrator.cinema_hung.api.ApiProvider
import com.apitiny.administrator.cinema_hung.api.ApiResult
import com.apitiny.administrator.cinema_hung.model.BaseModel
import com.apitiny.administrator.cinema_hung.model.ResponseModel
import com.google.gson.JsonObject

class ResetPassword : AppCompatActivity() {

    var _emailText: EditText? = null
    var _btnResetpw: Button? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpassword)

        _btnResetpw = findViewById(R.id.btnResetpw) as Button
        _emailText = findViewById(R.id.email_ed) as EditText

        _btnResetpw!!.setOnClickListener {
            if (!validate()) {
                _btnResetpw!!.isEnabled = true
            } else resetpw()
        }

    }

    fun resetpw() {
        Log.d(TAG, "Reset Password")

        _btnResetpw!!.isEnabled = false

        val _email = _emailText!!.text.toString()

        ApiProvider().callApiResetPw(object : ApiResult {
            override fun onError(e: Exception) {
                Log.e("TAG", e.message)
            }

            override fun onModel(baseModel: BaseModel) {
                if (baseModel is ResponseModel) {

                    Toast.makeText(baseContext, baseModel.isMessage, Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@ResetPassword, SigninActivity::class.java))
                    finish()
                }
            }

            override fun onJson(jsonObject: JsonObject) {
                Log.e("TAG", "Received a different model")
            }

            override fun onAPIFail() {
                Toast.makeText(baseContext, "Địa chỉ Email không tồn tại", Toast.LENGTH_LONG).show()
                Log.e("TAG", "Failed horribly")
            }

        }, _email, this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish()
            }
        }
    }

    fun validate(): Boolean {
        var valid = true

        val email = _emailText!!.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText!!.error = "Địa chỉ Email không đúng!"
            valid = false
        }

        return valid
    }

    companion object {
        private val TAG = "ResetPassword"
        private val REQUEST_SIGNUP = 0
    }
}