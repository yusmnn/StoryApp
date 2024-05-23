package com.example.storyapp.view.customviews

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class NameCustomView: AppCompatEditText, View.OnFocusChangeListener {

    var nameValid = false

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
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        onFocusChangeListener = this
    }

    override fun onFocusChange(v: View?, focus: Boolean) {
        if (!focus) {
            onValidateName()
        }
    }

    private fun onValidateName() {
        nameValid = text.toString().trim().isNotEmpty()
        error = if (!nameValid){
            resources.getString(R.string.emptyName)
        } else {
            null
        }
    }
}