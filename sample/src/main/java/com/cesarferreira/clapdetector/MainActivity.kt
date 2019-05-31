package com.cesarferreira.clapdetector

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cesarferreira.clapdetector.library.ClapDetector
import java.util.Random

class MainActivity : AppCompatActivity() {

    private var files = intArrayOf(
        R.raw.meme1,
        R.raw.meme2,
        R.raw.meme3,
        R.raw.meme4,
        R.raw.meme5
    )
    internal var mp: MediaPlayer? = null
    // internal var claps = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mp = MediaPlayer.create(this.baseContext, files[0])
        startRecording()
        modifyText()
    }

    private fun startRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Version>=Marshmallow
                startAudioDispatcher()
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(
                        this,
                        "App required access to audio", Toast.LENGTH_SHORT
                    ).show()
                }
                requestPermissions(
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_AUDIO_PERMISSION_RESULT
                )
            }
        } else {
            //Version < Marshmallow
            startAudioDispatcher()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_AUDIO_PERMISSION_RESULT) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    applicationContext,
                    "Application will not have audio on record", Toast.LENGTH_SHORT
                ).show()
            } else {
                startAudioDispatcher()
            }
        }
    }

    private val clapDetector = ClapDetector()

    private fun startAudioDispatcher() {
        clapDetector.detectClapAnd {
            playAudio()
            modifyText()
        }
    }

    private fun playAudio() {
        val rnd = Random().nextInt(files.size)

        if (mp != null && !mp!!.isPlaying /*&& clapDetector.claps >= 2*/) {
            mp!!.release()
            mp = MediaPlayer.create(this, files[rnd])
            mp!!.setOnCompletionListener { modifyText() }
            mp!!.start()
        }
    }

    @SuppressLint("SetTextI18n")
    fun modifyText() {

        runOnUiThread {
            // updates the UI
            val text = findViewById<TextView>(R.id.centerText)
            if (clapDetector.claps == 0 && mp != null && !mp!!.isPlaying) {
                text.text = "\uD83D\uDC4F Meme Review \uD83D\uDC4F"
            } else if (clapDetector.claps == 1) {
                text.text = "\uD83D\uDC4F"
            } else {
                text.text = "\uD83D\uDC4F\uD83D\uDC4F"
            }
        }
    }

    companion object {

        private const val REQUEST_AUDIO_PERMISSION_RESULT = 1
    }
}

