package com.example.validator

import android.text.TextUtils
import android.view.View
import android.widget.EditText

interface Rule<in V : View> {

    val priority: Int

    fun init(view: V)

    fun isValid(view: V): Boolean

    fun onValid(view: V)

    fun onInvalid(view: V)
}

abstract class RuleAdapted<V : View> : Rule<V> {

    override val priority: Int = 0

    override fun init(view: V) = Unit

    override fun onValid(view: V) = Unit

    override fun onInvalid(view: V) = Unit
}

class NotEmptyRule : RuleAdapted<EditText>() {

    override val priority: Int = 10

    override fun init(view: EditText) {
        val original = view.onFocusChangeListener
        view.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && !isValid(view) && view.error.isNullOrEmpty()) {
                onInvalid(view)
            }
            original?.onFocusChange(view, hasFocus)
        }
    }

    override fun isValid(view: EditText): Boolean = !view.text.isNullOrEmpty()

    override fun onValid(view: EditText) {
        view.error = null
    }

    override fun onInvalid(view: EditText) {
        view.error = "Text must not be empty"
    }
}

class MinLengthRule(private val length: Int, private val error: String) : RuleAdapted<EditText>() {

    override fun isValid(view: EditText): Boolean = view.length() >= length

    override fun onValid(view: EditText) {
        view.error = null
    }

    override fun onInvalid(view: EditText) {
        view.error = error
    }
}

class OnlyDigitsAllowed : RuleAdapted<EditText>() {
    private val error = "Only digits allowed"

    override fun isValid(view: EditText): Boolean = TextUtils.isDigitsOnly(view.text)

    override fun onValid(view: EditText) {
        if (view.error == error) {
            view.error = null
        }
    }

    override fun onInvalid(view: EditText) {
        view.error = error
    }
}