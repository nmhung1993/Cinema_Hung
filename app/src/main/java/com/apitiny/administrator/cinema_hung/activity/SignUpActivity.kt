package com.apitiny.administrator.cinema_hung.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.apitiny.administrator.cinema_hung.PreferencesHelper
import com.apitiny.administrator.cinema_hung.R
import com.apitiny.administrator.cinema_hung.api.ApiProvider
import com.apitiny.administrator.cinema_hung.api.ApiResult
import com.apitiny.administrator.cinema_hung.model.BaseModel
import com.apitiny.administrator.cinema_hung.model.ResponseModel
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.google.gson.JsonObject
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.acitivity_signup.*

class SignUpActivity : AppCompatActivity() {

    var preferencesHelper = PreferencesHelper(this)

    var _emailText: EditText? = null
    var _nameText: EditText? = null
    var _passwordText: EditText? = null
    var _reEnterPasswordText: EditText? = null
    var _signupButton: Button? = null
    var _signinButton: Button? = null

    lateinit var inputMethodManager: InputMethodManager

    lateinit var aDialog: AwesomeProgressDialog

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_signup)

        layout_signup.setOnTouchListener { v, event -> inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS) }

        inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        aDialog = AwesomeProgressDialog(this)
                .setMessage("")
                .setTitle("")
                .setDialogBodyBackgroundColor(R.color.float_transparent)
                .setColoredCircle(R.color.colorPrimary)
                .setCancelable(false)

        Toasty.Config.getInstance()
                .setSuccessColor(Color.parseColor("#02afee"))
                .setErrorColor(Color.parseColor("#ef5350"))
                .setTextSize(18)
                .apply()

        _emailText = findViewById(R.id.email_ed) as EditText
        _nameText = findViewById(R.id.name_ed) as EditText
        _passwordText = findViewById(R.id.password_ed) as EditText
        _reEnterPasswordText = findViewById(R.id.repassword_ed) as EditText

        _signupButton = findViewById(R.id.btnSignUp) as Button
        _signinButton = findViewById(R.id.btnSignIn) as Button

        _signupButton!!.setOnClickListener {
            if (validate()) {
                aDialog.show()
                hideSoftKeyboard()
                signup()
            }
        }

        _signinButton!!.setOnClickListener {
            val intent = Intent(applicationContext, SigninActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
        }
    }

    fun signup() {
        Log.d(TAG, "Signup")

        val _email = _emailText!!.text.toString()
        val _name = _nameText!!.text.toString()
        val _password = _passwordText!!.text.toString()

        ApiProvider().callApiSignup(object : ApiResult {
            override fun onError(e: Exception) {
                Log.e("TAG", e.message)
            }
            override fun onModel(baseModel: BaseModel) {
                if (baseModel is ResponseModel) {
                    preferencesHelper.saveVal(application, "token", baseModel.isToken)
                    preferencesHelper.saveVal(application, "userID", baseModel.isUser!!._id)
                    preferencesHelper.saveVal(application, "userName", baseModel.isUser!!.name)
                    preferencesHelper.saveVal(application, "userEmail", baseModel.isUser!!.email)
                    Toasty.success(this@SignUpActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT, true).show()
                    startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                    finish()
                }
            }
            override fun onJson(jsonObject: JsonObject) {
                Log.e("TAG", "Received a different model")
            }
            override fun onAPIFail() {
                aDialog.hide()
                var aiDialog = AwesomeInfoDialog(this@SignUpActivity)
                        .setTitle("LỖI!!!")
                        .setMessage("Email này đã có người sử dụng?")
                        .setColoredCircle(R.color.red_btn)
                        .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                        .setCancelable(true)
                aiDialog.show()
                Log.e("TAG", "Failed horribly")
            }
        }, _name, _email, _password, this)
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

    fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    companion object {
        private val TAG = "SignupActivity"
    }
}