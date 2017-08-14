package com.orogersilva.voicerecognition

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), RecognitionListener {

    private val TAG = "MainActivity"

    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var recognizerIntent: Intent

    private val PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                var i = 1
                i++

            } else {

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_REQUEST_CODE)
            }

        } else {

            initializeComponents()
        }
    }

    override fun onStop() {

        super.onStop()

        speechRecognizer?.apply { destroy() }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            VOICE_IN_REQUEST_CODE -> {

                if (resultCode == Activity.RESULT_OK && data != null) {

                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                    showToastMessage("Audio error.")
                } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                    showToastMessage("No match.")
                }
            }
        }
    }*/

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {

            PERMISSION_REQUEST_CODE -> {

                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    initializeComponents()
                }
            }
        }
    }

    // region OVERRIDED METHODS

    override fun onReadyForSpeech(p0: Bundle?) {

        Log.i(TAG, "onReadyForSpeech")
    }

    override fun onRmsChanged(rmsDb: Float) {

        Log.i(TAG, "onRmsChanged: " + rmsDb)

        rmsProgressBar.progress = rmsDb.toInt()
    }

    override fun onBufferReceived(buffer: ByteArray?) {

        Log.i(TAG, "onBufferReceived: " + buffer)
    }

    override fun onPartialResults(p0: Bundle?) {

        Log.i(TAG, "onPartialResults")
    }

    override fun onEvent(p0: Int, p1: Bundle?) {

        Log.i(TAG, "onEvent")
    }

    override fun onBeginningOfSpeech() {

        Log.i(TAG, "onBeginningOfSpeech")

        rmsProgressBar.isIndeterminate = false
        rmsProgressBar.max = 10
    }

    override fun onEndOfSpeech() {

        Log.i(TAG, "onEndOfSpeech")

        rmsProgressBar.isIndeterminate = true
        activateSpeechToggleButton.isChecked = false
    }

    override fun onError(errorCode: Int) {

        val errorMessage = getErrorText(errorCode)

        Log.i(TAG, errorMessage)

        speechTextView.text = errorMessage

        activateSpeechToggleButton.isChecked = false
    }

    override fun onResults(results: Bundle?) {

        Log.i(TAG, "onResults")

        speechTextView.text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
    }

    // endregion

    private fun getErrorText(errorCode: Int): String {

        when (errorCode) {

            SpeechRecognizer.ERROR_AUDIO -> return "Audio recording error."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> return "Insufficient permissions."
            SpeechRecognizer.ERROR_NO_MATCH -> return "No match."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> return "RecognitionService busy."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> return "No speech input."
        }

        return "Didn't understand, please try again."
    }

    private fun initializeComponents() {

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(this)

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)

        activateSpeechToggleButton.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {

            override fun onCheckedChanged(compoundButton: CompoundButton?, isChecked: Boolean) {

                if (isChecked) {

                    rmsProgressBar.visibility = View.VISIBLE
                    rmsProgressBar.isIndeterminate = true

                    speechRecognizer?.startListening(recognizerIntent)

                } else {

                    rmsProgressBar.visibility = View.INVISIBLE
                    rmsProgressBar.isIndeterminate = false

                    speechRecognizer?.stopListening()
                }
            }
        })
    }
}