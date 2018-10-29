package com.apitiny.administrator.hungcinema.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.apitiny.administrator.hungcinema.PreferencesHelper
import com.apitiny.administrator.hungcinema.R
import com.apitiny.administrator.hungcinema.api.ApiProvider
import com.apitiny.administrator.hungcinema.api.ApiResult
import com.apitiny.administrator.hungcinema.model.BaseModel
import com.apitiny.administrator.hungcinema.model.ResponseModel
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.google.gson.JsonObject
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.acitivity_signup.*

class SignUpActivity : AppCompatActivity() {
  
  var prfHelper = PreferencesHelper(this)
  
  private lateinit var imm: InputMethodManager
  
  lateinit var aDialog: AwesomeProgressDialog
  
  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.acitivity_signup)
    
    layout_signup.setOnTouchListener { _, _ -> imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS) }
    
    imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    
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
    
    btnSignUp!!.setOnClickListener {
      if (validate()) {
        aDialog.show()
        hideSoftKeyboard()
        signup()
      }
    }
    
    btnSignIn!!.setOnClickListener {
      val intent = Intent(applicationContext, SigninActivity::class.java)
      startActivity(intent)
      finish()
      overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
    }
  }
  
  private fun signup() {
    Log.d(TAG, "Signup")
    
    val email = email_ed?.text.toString()
    val name = name_ed?.text.toString()
    val password = password_ed?.text.toString()
    
    ApiProvider().callApiSignup(object : ApiResult {
      override fun onError(e: Exception) {
        Log.e("TAG", e.message)
      }
      
      override fun onModel(baseModel: BaseModel) {
        if (baseModel is ResponseModel) {
          prfHelper.run {
            saveVal(application, "token", baseModel.isToken)
            saveVal(application, "userId", baseModel.isUser!!._id)
            saveVal(application, "userName", baseModel.isUser!!.name)
            saveVal(application, "userEmail", baseModel.isUser!!.email)
          }
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
        val aiDialog = AwesomeInfoDialog(this@SignUpActivity)
            .setTitle("LỖI!!!")
            .setMessage("Email này đã có người sử dụng?")
            .setColoredCircle(R.color.red_btn)
            .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
            .setCancelable(true)
        aiDialog.show()
        Log.e("TAG", "Failed horribly")
      }
    }, name, email, password, this)
  }
  
  private fun validate(): Boolean {
    var valid = true
    
    val email = email_ed!!.text.toString()
    val name = name_ed!!.text.toString()
    val password = password_ed!!.text.toString()
    val reEnterPassword = repassword_ed!!.text.toString()
    
    if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      email_ed!!.error = "Địa chỉ Email không đúng!"
      valid = false
    } else if (name.isEmpty()) {
      name_ed!!.error = "Bạn phải nhập tên của bạn."
      valid = false
    } else if (password.isEmpty() || password.length < 4 || password.length > 10) {
      password_ed!!.error = "Mật khẩu phải dài từ 4 đến 10 kí tự"
      valid = false
    } else if (reEnterPassword.isEmpty() || reEnterPassword.length < 4 || reEnterPassword.length > 10 || reEnterPassword != password) {
      repassword_ed!!.error = "Mật khẩu không trùng!!"
      valid = false
    }
    return valid
  }
  
  private fun hideSoftKeyboard() {
    if (currentFocus != null) {
      val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
  }
  
  companion object {
    private val TAG = "SignupActivity"
  }
}