package com.apitiny.administrator.cinema_hung.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.apitiny.administrator.cinema_hung.PreferencesHelper
import com.apitiny.administrator.cinema_hung.R
import com.apitiny.administrator.cinema_hung.api.ApiProvider
import com.apitiny.administrator.cinema_hung.api.ApiResult
import com.apitiny.administrator.cinema_hung.model.BaseModel
import com.apitiny.administrator.cinema_hung.model.ResponseModel
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure
import com.google.gson.JsonObject
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_changepassword.*

class ChangePassword : AppCompatActivity() {

    lateinit var inputMethodManager: InputMethodManager
    lateinit var aDialog: AwesomeProgressDialog
    var _oldpassED: EditText? = null
    var _newpassED: EditText? = null
    var _rnewpassED: EditText? = null
    var _btnChangepw: Button? = null
    var prefValue = PreferencesHelper(this)


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepassword)

        layout_changepw.setOnTouchListener { v, event -> inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS) }

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

        _btnChangepw = findViewById(R.id.btnChangepw) as Button
        _oldpassED = findViewById(R.id.oldPass_ed) as EditText
        _newpassED = findViewById(R.id.newPass_ed) as EditText
        _rnewpassED = findViewById(R.id.rnewPass_ed) as EditText

        val _token = prefValue.getVal(application, "token")

        _btnChangepw!!.setOnClickListener {
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            if (!validate()) {
                _btnChangepw!!.isEnabled = true
            } else {
                aDialog.show()
                changepw(_token)
            }
        }

    }

    fun changepw(_token: String?) {
        Log.d(TAG, "Reset Password")

        _btnChangepw!!.isEnabled = false

        val _oldpass = _oldpassED!!.text.toString()
        val _newpass = _newpassED!!.text.toString()

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
                            var aiDialog = AwesomeInfoDialog(this@ChangePassword)
                                    .setTitle("LỖI!")
                                    .setMessage("Sai mật khẩu cũ!")
                                    .setColoredCircle(R.color.red_btn)
                                    .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                                    .setCancelable(true)
                            aiDialog.show()
//                            Toasty.error(this@ChangePassword, "Sai mật khẩu cũ!", Toast.LENGTH_SHORT, true).show()
                            _btnChangepw!!.isEnabled = true
                            aDialog.hide()
                        }
                    }
                }

                override fun onJson(jsonObject: JsonObject) {
                    Log.e("TAG", "Received a different model")
                }

                override fun onAPIFail() {
                    Toasty.error(this@ChangePassword, "Sai mật khẩu cũ!", Toast.LENGTH_SHORT, true).show()
                    _btnChangepw!!.isEnabled = true
                    aDialog.hide()
                    Log.e("TAG", "Failed horribly")
                }

            }, _token, _oldpass, _newpass, this)
        }
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

        val oldpass = _oldpassED!!.text.toString()
        val newpass = _newpassED!!.text.toString()
        val rnewpass = _rnewpassED!!.text.toString()

        if (oldpass.isEmpty()) {
            _oldpassED!!.error = "Không được bỏ trống!"
            valid = false
        } else if (newpass.isEmpty() || newpass.length < 4 || newpass.length > 10)
            _newpassED!!.error = "Mật khẩu mới phải từ 4 đến 10 ký tự!"
        else if (rnewpass.isEmpty() || rnewpass.length < 4 || rnewpass.length > 10 || rnewpass != newpass) {
            _rnewpassED!!.error = "Mật khẩu không trùng!!"
            valid = false
        }

        return valid
    }

    companion object {
        private val TAG = "ChangePassword"
        private val REQUEST_SIGNUP = 0
    }
}