package com.apitiny.administrator.cinema_hung.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.apitiny.administrator.cinema_hung.PreferencesHelper
import com.apitiny.administrator.cinema_hung.R
import com.apitiny.administrator.cinema_hung.api.ApiProvider
import com.apitiny.administrator.cinema_hung.api.ApiResult
import com.apitiny.administrator.cinema_hung.model.BaseModel
import com.apitiny.administrator.cinema_hung.model.ResponseModel
import com.google.gson.JsonObject

class SigninActivity : AppCompatActivity() {

    var preferencesHelper = PreferencesHelper(this@SigninActivity)
    var _emailText: EditText? = null
    var _passwordText: EditText? = null
    var _signinButton: Button? = null
    var _signupButton: Button? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        _signinButton = findViewById(R.id.btnSignin) as Button
        _signupButton = findViewById(R.id.btnSignup) as Button
        _passwordText = findViewById(R.id.password_ed) as EditText
        _emailText = findViewById(R.id.email_ed) as EditText

        val _rspwbtn = findViewById(R.id.resetpassword) as TextView
        _rspwbtn.setOnClickListener {
            startActivity(Intent(this@SigninActivity, ResetPassword::class.java))
            finish()
        }
        _signinButton!!.setOnClickListener { login() }

        _signupButton!!.setOnClickListener {
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivityForResult(intent, REQUEST_SIGNUP)
            finish()
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    fun login() {
        Log.d(TAG, "Login")

        if (!validate()) {
            onLoginFailed()
            return
        }

        _signinButton!!.isEnabled = false

        val progressDialog = ProgressDialog(this@SigninActivity, R.style.Base_Theme_AppCompat_Dialog)

        val _email = _emailText!!.text.toString()
        val _password = _passwordText!!.text.toString()

        ApiProvider().callApiSignin(object : ApiResult {
            override fun onError(e: Exception) {
                Log.e("TAG", e.message)
            }

            override fun onModel(baseModel: BaseModel) {
                if (baseModel is ResponseModel) {

                    // save token on preferences
                    preferencesHelper.saveVal(application, "token", baseModel.isToken)
                    preferencesHelper.saveVal(application, "userID", baseModel.isUser!!._id)
                    preferencesHelper.saveVal(application, "userEmail", baseModel.isUser!!.email)
                    preferencesHelper.saveVal(application, "userName", baseModel.isUser!!.name)

                    Toast.makeText(baseContext, "Đăng nhập thành công!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@SigninActivity, MainActivity::class.java))
                    finish()
                }
            }

            override fun onJson(jsonObject: JsonObject) {
                Log.e("TAG", "Received a different model")
            }

            override fun onAPIFail() {
                Toast.makeText(baseContext, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_LONG).show()
                Log.e("TAG", "Failed horribly")
            }

        }, _email, _password, this)

        android.os.Handler().postDelayed(
                {
                    // On complete call either onLoginSuccess or onLoginFailed
                    onLoginSuccess()
                    // onLoginFailed();
                    progressDialog.dismiss()
                }, 3000)
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

//    override fun onBackPressed() {
//        // Disable going back to the MainActivity
//        moveTaskToBack(true)
//    }

    fun onLoginSuccess() {
        _signinButton!!.isEnabled = true
//        finish()
//        Toast.makeText(baseContext, "Đăng nhập thành công!", Toast.LENGTH_LONG).show()
//        startActivity(Intent(this, MainActivity::class.java))
//        finish()
    }

    fun onLoginFailed() {
//        Toast.makeText(baseContext, "Đăng nhập không thành công!", Toast.LENGTH_LONG).show()
        _signinButton!!.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true

        val email = _emailText!!.text.toString()
        val password = _passwordText!!.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText!!.error = "Địa chỉ Email không đúng!"
            valid = false
        } else if (password.isEmpty() || password.length < 4 || password.length > 10) {
            _passwordText!!.error = "Mật khẩu phải từ 4 đến 10 kí tự"
            valid = false
        }

        return valid
    }

//    fun resetPassword() {
//        startActivity(Intent(this, ResetPassword::class.java))
//        finish()
//    }

    companion object {
        private val TAG = "LoginActivity"
        private val REQUEST_SIGNUP = 0
    }
}