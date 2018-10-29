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
import com.apitiny.administrator.hungcinema.R
import com.apitiny.administrator.hungcinema.api.ApiProvider
import com.apitiny.administrator.hungcinema.api.ApiResult
import com.apitiny.administrator.hungcinema.model.BaseModel
import com.apitiny.administrator.hungcinema.model.ResponseModel
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.google.gson.JsonObject
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_resetpassword.*

class ResetPassword : AppCompatActivity() {
  
  private lateinit var imm: InputMethodManager
  
  lateinit var aDialog: AwesomeProgressDialog
  
  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_resetpassword)
    
    layout_rspass.setOnTouchListener { _, _ -> imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS) }
    
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
    
    btnResetpw!!.setOnClickListener {
      if (validate()) resetPw()
    }
    
  }
  
  private fun resetPw() {
    aDialog.show()
    Log.d(TAG, "Reset Password")
    
    btnResetpw!!.isEnabled = false
    
    val email = email_ed?.text.toString()
    
    ApiProvider().callApiResetPw(object : ApiResult {
      override fun onError(e: Exception) {
        Log.e("TAG", e.message)
      }
      
      override fun onModel(baseModel: BaseModel) {
        if (baseModel is ResponseModel) {
          aDialog.hide()
          Toasty.success(this@ResetPassword, "Đặt lại mật khẩu thành công!", Toast.LENGTH_SHORT, true).show()
          startActivity(Intent(this@ResetPassword, SigninActivity::class.java))
          finish()
        }
      }
      
      override fun onJson(jsonObject: JsonObject) {
        Log.e("TAG", "Received a different model")
      }
      
      override fun onAPIFail() {
        aDialog.hide()
        btnResetpw!!.isEnabled = true
        val aiDialog = AwesomeInfoDialog(this@ResetPassword)
            .setTitle("LỖI!")
            .setMessage("Địa chỉ Email không tồn tại!")
            .setColoredCircle(R.color.red_btn)
            .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
            .setCancelable(true)
        aiDialog.show()
        //                Toasty.error(this@ResetPassword, "Địa chỉ Email không tồn tại!", Toast.LENGTH_SHORT, true).show()
        Log.e("TAG", "Failed horribly")
      }
      
    }, email, this)
  }
  
  
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    if (requestCode == REQUEST_SIGNUP) {
      if (resultCode == Activity.RESULT_OK) {
        
        this.finish()
      }
    }
  }
  
  private fun validate(): Boolean {
    var valid = true
    
    val email = email_ed!!.text.toString()
    
    if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      email_ed!!.error = "Địa chỉ Email không đúng!"
      valid = false
    }
    
    return valid
  }
  
  companion object {
    private const val TAG = "ResetPassword"
    private const val REQUEST_SIGNUP = 0
  }
}