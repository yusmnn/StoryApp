package com.example.storyapp.view.customviews
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class SamePassCustomView: AppCompatEditText, View.OnFocusChangeListener {

     var  samePassValid = false

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
    private fun init() {
        background = ContextCompat.getDrawable(context, R.drawable.bgedittext)
        transformationMethod = PasswordTransformationMethod.getInstance()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onValidatePass()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun onValidatePass() {
        val pass = text.toString().trim()
        val passConfirm = (parent as ViewGroup).findViewById<PasswordCustomView>(R.id.cv_password).text.toString().trim()

        samePassValid = pass.length >= 8 && pass == passConfirm
        error = if (!samePassValid) {
            resources.getString(R.string.passNotSame)
        } else {
            null
        }
    }

    override fun onFocusChange(p0: View?, focused: Boolean) {
        if (!focused) {
            onValidatePass()
        }
    }
}