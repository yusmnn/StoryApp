package com.example.storyapp.view.customviews

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class EmailCustomView : AppCompatEditText, View.OnFocusChangeListener {

    private lateinit var sameEmail: String
    private var emailTaken = false
    var emailValid = false

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr) {
        init()
    }

    private fun onValidateEmail() {
        emailValid = Patterns.EMAIL_ADDRESS.matcher(text.toString().trim()).matches()
        error = if (!emailValid) {
            resources.getString(R.string.wrongEmailFormat)
        } else {
            null
        }
    }

    private fun init() {
        background = ContextCompat.getDrawable(context, R.drawable.bgedittext)
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        onFocusChangeListener = this
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onValidateEmail()
                if (emailTaken){
                    onValidateEmailTaken()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun onValidateEmailTaken() {
        error = if (emailTaken && text.toString().trim() == sameEmail) {
            resources.getString(R.string.emailTaken)
        } else {
            null
        }
    }

    override fun onFocusChange(v: View?, focus: Boolean) {
        if (!focus) {
            onValidateEmail()
            if (emailTaken) {
                onValidateEmailTaken()
            }
        }
    }

    fun setErrorMsg(msg: String, email:String){
        sameEmail = email
        emailTaken = true
        error = if (text.toString().trim() == sameEmail) {
            msg
        } else {
            null
        }
    }
}