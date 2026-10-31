package com.celiocastro.native32tv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.swordfish.libretrodroid.GLRetroView
import com.swordfish.libretrodroid.GLRetroViewData
import com.swordfish.libretrodroid.ShaderConfig
import java.io.File

class MainActivity : AppCompatActivity() {
    private val pickGame = 101
    private var retroView: GLRetroView? = null
    private lateinit var root: FrameLayout
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        root = FrameLayout(this)
        showHome()
        setContentView(root)
    }

    private fun showHome(message: String = "Emulador Native32 • Android TV / H8TV\n\nEscolha um jogo compatível para iniciar.") {
        retroView?.let { lifecycle.removeObserver(it) }
        retroView = null
        root.removeAllViews()

        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(40, 24, 40, 24)
            setBackgroundColor(0xFF101114.toInt())
        }
        val title = TextView(this).apply {
            text = "NATIVE32 TV"
            textSize = 34f
            setTextColor(0xFFE8553A.toInt())
            gravity = Gravity.CENTER
        }
        status = TextView(this).apply {
            text = message
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
        panel.addView(title)
        panel.addView(status)
        panel.addView(button)
        root.addView(panel, FrameLayout.LayoutParams(-1, -1))
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
        if (requestCode != pickGame || resultCode != RESULT_OK) return
        val uri: Uri = data?.data ?: return
        try {
            val name = queryDisplayName(uri) ?: "selected-game"
            val gameFile = File(cacheDir, name.replace(Regex("[^A-Za-z0-9._-]"), "_"))
            contentResolver.openInputStream(uri)?.use { input ->
                gameFile.outputStream().use { output -> input.copyTo(output) }
            } ?: throw IllegalStateException("Não foi possível ler o arquivo.")

            val coreName = "libnative32emu_libretro_android.so"
            val coreFile = File(applicationInfo.nativeLibraryDir, coreName)
            if (!coreFile.exists()) throw IllegalStateException("Core Native32 não encontrado no APK para esta arquitetura.")

            val viewData = GLRetroViewData(this).apply {
                coreFilePath = coreFile.absolutePath
                gameFilePath = gameFile.absolutePath
                systemDirectory = filesDir.absolutePath
                savesDirectory = File(filesDir, "saves").apply { mkdirs() }.absolutePath
                shader = ShaderConfig.Default
                rumbleEventsEnabled = true
                preferLowLatencyAudio = true
            }
            root.removeAllViews()
            val view = GLRetroView(this, viewData)
            retroView = view
            root.addView(view, FrameLayout.LayoutParams(-1, -1))
            lifecycle.addObserver(view)
            view.requestFocus()
        } catch (e: Exception) {
            showHome("Falha ao iniciar o jogo:\n${e.message ?: "erro desconhecido"}")
        }
    }

    private fun queryDisplayName(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null) ?: return null
        cursor.use {
            val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            return if (it.moveToFirst() && index >= 0) it.getString(index) else null
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val view = retroView
        if (view != null && keyCode != KeyEvent.KEYCODE_BACK) {
            view.sendKeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            return true
        }
        return if (keyCode == KeyEvent.KEYCODE_BACK && view != null) {
            showHome()
            true
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        val view = retroView
        if (view != null) {
            view.sendKeyEvent(KeyEvent.ACTION_UP, keyCode)
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onGenericMotionEvent(event: android.view.MotionEvent?): Boolean {
        return retroView?.onGenericMotionEvent(event) ?: super.onGenericMotionEvent(event)
    }
}
