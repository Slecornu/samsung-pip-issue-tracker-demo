package com.slecornu.pipdemo

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("SAM-MainActivity(${hashCode()}) - onCreate")
        val videoButton = Button(this).apply {
            text = "Launch Video"
            setOnClickListener {
                startActivity(VideoActivity.createIntent(this@MainActivity))
            }
        }

        val messageButton = Button(this).apply {
            text = "Launch Message"
            setOnClickListener {
                startActivity(MessageActivity.createIntent(this@MainActivity))
            }
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
            )
            gravity = Gravity.CENTER
            addView(videoButton)
            addView(messageButton)
        }

        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        println("SAM-MainActivity(${hashCode()}) - onResume")
    }

    override fun onPause() {
        super.onPause()
        println("SAM-MainActivity(${hashCode()}) - onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("SAM-MainActivity(${hashCode()}) - onDestroy")
    }
}
