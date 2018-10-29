package com.apitiny.administrator.hungcinema.activity

import android.app.Activity
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
import kotlinx.android.synthetic.main.activity_signin.*


class SigninActivity : AppCompatActivity() {
  
  private lateinit var imm: InputMethodManager
  
  lateinit var aDialog: AwesomeProgressDialog
  
  var preferencesHelper = PreferencesHelper(this@SigninActivity)
  
  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_signin)
    
    layout_signin.setOnTouchListener { _, _ -> imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS) }
    
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
    
    resetpassword.setOnClickListener {
      startActivity(Intent(this@SigninActivity, ResetPassword::class.java))
      finish()
    }
    btnSignin.setOnClickListener {
      imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
      if (validate()) {
        aDialog.show()
        login()
      }
    }
    
    btnSignup.setOnClickListener {
      val intent = Intent(applicationContext, SignUpActivity::class.java)
      startActivityForResult(intent, REQUEST_SIGNUP)
      finish()
      overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
    }
  }
  
  private fun login() {
    Log.d(TAG, "Login")
    
    val email = email_ed!!.text.toString()
    val password = password_ed!!.text.toString()
    
    ApiProvider().callApiSignin(object : ApiResult {
      override fun onError(e: Exception) {
        Log.e("TAG", e.message)
      }
      
      override fun onModel(baseModel: BaseModel) {
        if (baseModel is ResponseModel) {
          
          // save token on preferences
          preferencesHelper.run {
            saveVal(application, "token", baseModel.isToken)
            saveVal(application, "userId", baseModel.isUser!!._id)
            saveVal(application, "userEmail", baseModel.isUser!!.email)
            saveVal(application, "userName", baseModel.isUser!!.name)
            saveVal(application, "avatarURL", baseModel.isUser!!.avatarURL)
          }
          
          Toasty.success(this@SigninActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT, true).show()
          startActivity(Intent(this@SigninActivity, MainActivity::class.java))
          finish()
        }
      }
      
      override fun onJson(jsonObject: JsonObject) {
        Log.e("TAG", "Received a different model")
      }
      
      override fun onAPIFail() {
        aDialog.hide()
        val aiDialog = AwesomeInfoDialog(this@SigninActivity)
            .setTitle("Lỗi")
            .setMessage("Sai tài khoản Email hoặc mật khẩu!!!")
            .setColoredCircle(R.color.red_btn)
            .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
            .setCancelable(true)
        aiDialog.show()
        Log.e("TAG", "Failed horribly")
      }
    }, email, password, this)
    
    android.os.Handler().postDelayed({ onLoginSuccess() }, 3000)
  }
  
  
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    if (requestCode == REQUEST_SIGNUP) {
      if (resultCode == Activity.RESULT_OK) {
        this.finish()
      }
    }
  }
  
  //    override fun onBackPressed() {
  //        // Disable going back to the MainActivity
  //        moveTaskToBack(true)
  //    }
  
  private fun onLoginSuccess() {
    btnSignin!!.isEnabled = true
  }
  
  private fun validate(): Boolean {
    var valid = true
    
    val email = email_ed!!.text.toString()
    val password = password_ed!!.text.toString()
    
    if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      email_ed!!.error = "Địa chỉ Email không đúng!"
      valid = false
    } else if (password.isEmpty() || password.length < 4 || password.length > 10) {
      password_ed!!.error = "Mật khẩu phải từ 4 đến 10 kí tự"
      valid = false
    }
    
    return valid
  }
  
  companion object {
    private const val TAG = "LoginActivity"
    private const val REQUEST_SIGNUP = 0
  }
}