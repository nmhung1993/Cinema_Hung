package com.apitiny.administrator.cinema_hung.activity

import android.app.ProgressDialog
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
import com.apitiny.administrator.cinema_hung.model.User
import com.google.gson.JsonObject

class SignUpActivity : AppCompatActivity() {

    var _emailText: EditText? = null
    var _nameText: EditText? = null
    var _passwordText: EditText? = null
    var _reEnterPasswordText: EditText? = null
    var _signupButton: Button? = null
    var _signinButton: Button? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.apitiny.administrator.cinema_hung.R.layout.acitivity_signup)

        _emailText = findViewById(R.id.email_ed) as EditText
        _nameText = findViewById(R.id.name_ed) as EditText
        _passwordText = findViewById(R.id.password_ed) as EditText
        _reEnterPasswordText = findViewById(R.id.repassword_ed) as EditText

        _signupButton = findViewById(R.id.btnSignUp) as Button
        _signinButton = findViewById(R.id.btnSignIn) as Button

        _signupButton!!.setOnClickListener { signup() }

        _signinButton!!.setOnClickListener {
            // Finish the registration screen and return to the Login activity
            val intent = Intent(applicationContext, SigninActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(com.apitiny.administrator.cinema_hung.R.anim.push_right_in, com.apitiny.administrator.cinema_hung.R.anim.push_right_out)
        }
    }

    fun signup() {
        Log.d(TAG, "Signup")

        if (!validate()) {
            onSignupFailed()
            return
        }

        _signupButton!!.isEnabled = false

        val progressDialog = ProgressDialog(this@SignUpActivity,
                com.apitiny.administrator.cinema_hung.R.style.Base_Theme_AppCompat_Dialog)
//        progressDialog.isIndeterminate = true
//        progressDialog.setMessage("Đang tạo tài khoản...")
//        progressDialog.show()

        val _email = _emailText!!.text.toString()
        val _name = _nameText!!.text.toString()
        val _password = _passwordText!!.text.toString()

        ApiProvider().callApiSignup(object : ApiResult {
            override fun onError(e: Exception) {
                Log.e("TAG", e.message)
            }

            override fun onModel(baseModel: BaseModel) {
                if (baseModel is ResponseModel) {
                    Toast.makeText(baseContext, "Đăng ký thành công!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                    finish()
                }
            }

            override fun onJson(jsonObject: JsonObject) {
                Log.e("TAG", "Received a different model")
            }

            override fun onAPIFail() {
                Toast.makeText(baseContext, "Email này đã có người sử dụng", Toast.LENGTH_LONG).show()
                Log.e("TAG", "Failed horribly")
            }

        }, _name, _email, _password, this)

        android.os.Handler().postDelayed(
                {
                    // On complete call either onSignupSuccess or onSignupFailed
                    // depending on success
                    onSignupSuccess()
                    // onSignupFailed();
                    progressDialog.dismiss()
                }, 3000)
    }


    fun onSignupSuccess() {
        _signupButton!!.isEnabled = true
//        setResult(Activity.RESULT_OK, null)
//        Toast.makeText(baseContext, "Đăng ký thành công!", Toast.LENGTH_LONG).show()
//        startActivity(Intent(this, MainActivity::class.java))
//        finish()
    }

    fun onSignupFailed() {
//        Toast.makeText(baseContext, "Đăng ký không thành công!", Toast.LENGTH_LONG).show()
        _signupButton!!.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true

        val email = _emailText!!.text.toString()
        val name = _nameText!!.text.toString()
        val password = _passwordText!!.text.toString()
        val reEnterPassword = _reEnterPasswordText!!.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText!!.error = "Địa chỉ Email không đúng!"
            valid = false
        } else if (name.isEmpty()) {
            _nameText!!.error = "Bạn phải nhập tên của bạn."
            valid = false
        } else if (password.isEmpty() || password.length < 4 || password.length > 10) {
            _passwordText!!.error = "Mật khẩu phải dài từ 4 đến 10 kí tự"
            valid = false
        } else if (reEnterPassword.isEmpty() || reEnterPassword.length < 4 || reEnterPassword.length > 10 || reEnterPassword != password) {
            _reEnterPasswordText!!.error = "Mật khẩu không trùng!!"
            valid = false
        }

        return valid
    }

    companion object {
        private val TAG = "SignupActivity"
    }
}