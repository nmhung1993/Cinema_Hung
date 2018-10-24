package com.apitiny.administrator.cinema_hung.activity

import android.app.Activity
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
import com.apitiny.administrator.cinema_hung.R
import com.apitiny.administrator.cinema_hung.api.ApiProvider
import com.apitiny.administrator.cinema_hung.api.ApiResult
import com.apitiny.administrator.cinema_hung.model.BaseModel
import com.apitiny.administrator.cinema_hung.model.ResponseModel
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.google.gson.JsonObject
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_resetpassword.*

class ResetPassword : AppCompatActivity() {

    var _emailText: EditText? = null
    var _btnResetpw: Button? = null
    lateinit var inputMethodManager: InputMethodManager

    lateinit var aDialog: AwesomeProgressDialog

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpassword)

        layout_rspass.setOnTouchListener { v, event -> inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS) }

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

        _btnResetpw = findViewById(R.id.btnResetpw) as Button
        _emailText = findViewById(R.id.email_ed) as EditText

        _btnResetpw!!.setOnClickListener {
            if (validate()) resetpw()
        }

    }

    fun resetpw() {
        aDialog.show()
        Log.d(TAG, "Reset Password")

        _btnResetpw!!.isEnabled = false

        val _email = _emailText!!.text.toString()

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
                _btnResetpw!!.isEnabled = true
                var aiDialog = AwesomeInfoDialog(this@ResetPassword)
                        .setTitle("LỖI!")
                        .setMessage("Địa chỉ Email không tồn tại!")
                        .setColoredCircle(R.color.red_btn)
                        .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                        .setCancelable(true)
                aiDialog.show()
//                Toasty.error(this@ResetPassword, "Địa chỉ Email không tồn tại!", Toast.LENGTH_SHORT, true).show()
                Log.e("TAG", "Failed horribly")
            }

        }, _email, this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {

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