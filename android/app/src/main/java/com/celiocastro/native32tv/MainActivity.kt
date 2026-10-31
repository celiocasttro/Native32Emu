package com.celiocastro.native32tv

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.content.Intent
import android.net.Uri

class MainActivity : Activity() {
    private lateinit var status: TextView
    private val pickGame = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(36, 24, 36, 24)
            setBackgroundColor(0xFF101114.toInt())
        }
        val title = TextView(this).apply {
            text = "NATIVE32 TV"
            textSize = 34f
            setTextColor(0xFFE8553A.toInt())
            gravity = Gravity.CENTER
        }
        status = TextView(this).apply {
            text = "Emulador Native32 • Android TV / H8TV\n\nSelecione um jogo .SMF, .SGM, .SSL ou .ZIP."
            textSize = 18f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 24)
        }
        val button = Button(this).apply {
            text = "ABRIR JOGO"
            textSize = 18f
            isFocusable = true
            setOnClickListener { openGamePicker() }
        }
        root.addView(title)
        root.addView(status)
        root.addView(button)
        setContentView(root)
        button.requestFocus()
    }

    private fun openGamePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, pickGame)
    }

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickGame && resultCode == RESULT_OK) {
            val uri: Uri? = data?.data
            status.text = if (uri != null) "Arquivo selecionado:\n$uri\n\nO motor de execução ainda precisa ser integrado nesta versão." else "Nenhum arquivo selecionado."
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK -> { finish(); true }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}
