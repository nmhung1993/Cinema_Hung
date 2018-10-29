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
import kotlinx.android.synthetic.main.activity_changepassword.*

class ChangePassword : AppCompatActivity() {
  
  private lateinit var imm: InputMethodManager
  lateinit var aDialog: AwesomeProgressDialog
  private var prefValue = PreferencesHelper(this)
  
  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_changepassword)
    
    layout_changepw.setOnTouchListener { _, _ -> imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS) }
    
    imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    
    aDialog = AwesomeProgressDialog(this)
        .setMessage("")
        .setTitle("")
//        .setDialogBodyBackgroundColor(R.color.float_transparent)
        .setColoredCircle(R.color.colorPrimary)
        .setCancelable(false)
    
    Toasty.Config.getInstance()
        .setSuccessColor(Color.parseColor("#02afee"))
        .setErrorColor(Color.parseColor("#ef5350"))
        .setTextSize(18)
    .apply()
  
    val token = prefValue.getVal(application, "token")
    
    btnChangepw.setOnClickListener {
      imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
      if (!validate()) {
        btnChangepw.isEnabled = true
      } else {
        aDialog.show()
        changepw(token)
      }
    }
    
  }
  
  private fun changepw(_token: String?) {
    Log.d(TAG, "Reset Password")
    
    btnChangepw!!.isEnabled = false
    
    val oldPass = oldPass_ed?.text.toString()
    val newPass = newPass_ed?.text.toString()
    
    if (_token != null) {
      ApiProvider().callApiChangePw(object : ApiResult {
        override fun onError(e: Exception) {
          Log.e("TAG", e.message)
        }
        
        override fun onModel(baseModel: BaseModel) {
          if (baseModel is ResponseModel) {
            if (baseModel.isStatus == 200) {
              Toasty.success(this@ChangePassword, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT, true).show()
              startActivity(Intent(this@ChangePassword, ProfileActivity::class.java))
              finish()
              overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
            } else {
              val aiDialog = AwesomeInfoDialog(this@ChangePassword)
                  .setTitle("LỖI!")
                  .setMessage("Sai mật khẩu cũ!")
                  .setColoredCircle(R.color.red_btn)
                  .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                  .setCancelable(true)
              aiDialog.show()
              //                            Toasty.error(this@ChangePassword, "Sai mật khẩu cũ!", Toast.LENGTH_SHORT, true).show()
              btnChangepw!!.isEnabled = true
              aDialog.hide()
            }
          }
        }
        
        override fun onJson(jsonObject: JsonObject) {
          Log.e("TAG", "Received a different model")
        }
        
        override fun onAPIFail() {
          Toasty.error(this@ChangePassword, "Sai mật khẩu cũ!", Toast.LENGTH_SHORT, true).show()
          btnChangepw!!.isEnabled = true
          aDialog.hide()
          Log.e("TAG", "Failed horribly")
        }
        
      }, _token, oldPass, newPass, this)
    }
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
    
    val oldPass = oldPass_ed!!.text.toString()
    val newPass = newPass_ed!!.text.toString()
    val rnewPass = rnewPass_ed!!.text.toString()
    
    if (oldPass.isEmpty()) {
      oldPass_ed!!.error = "Không được bỏ trống!"
      valid = false
    } else if (newPass.isEmpty() || newPass.length < 4 || newPass.length > 10)
      newPass_ed!!.error = "Mật khẩu mới phải từ 4 đến 10 ký tự!"
    else if (rnewPass.isEmpty() || rnewPass.length < 4 || rnewPass.length > 10 || rnewPass != newPass) {
      rnewPass_ed!!.error = "Mật khẩu không trùng!!"
      valid = false
    }
    
    return valid
  }
  
  companion object {
    private const val TAG = "ChangePassword"
    private const val REQUEST_SIGNUP = 0
  }
}