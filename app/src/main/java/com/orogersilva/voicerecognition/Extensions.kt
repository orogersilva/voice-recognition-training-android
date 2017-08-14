package com.orogersilva.voicerecognition

import android.support.v7.app.AppCompatActivity
import android.widget.Toast

/**
 * Created by orogersilva on 7/23/2017.
 */

fun AppCompatActivity.showToastMessage(message: String) {

    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}