package com.example.validator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val validator = Validator()
        val nameEditText = findViewById<EditText>(R.id.name)
        val numberEditText = findViewById<EditText>(R.id.number)

        findViewById<Button>(R.id.validate).setOnClickListener {
            if (validator.validate()) {
                Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show()
            }
        }

        validator {
            on(nameEditText) {
                add(NotEmptyRule())
                add(MinLengthRule(10, "Length must be greater than 10"))
            }
            on(numberEditText) {
                add(NotEmptyRule())
                add(OnlyDigitsAllowed())
            }
        }
    }
}
