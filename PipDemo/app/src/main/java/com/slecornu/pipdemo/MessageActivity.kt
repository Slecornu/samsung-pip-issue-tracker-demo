package com.slecornu.pipdemo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MessageActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val message = TextView(this).apply {
            text = "This screen is blocked by the pip"
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
            )
            gravity = Gravity.CENTER
            addView(message)
        }

        setContentView(root)
    }

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, MessageActivity::class.java)
    }
}