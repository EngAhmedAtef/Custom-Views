package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private var downloadURL: String? = null
    private var downloadStatus: Int? = null
    private lateinit var urlTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var okButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        getSystemService(NotificationManager::class.java).cancelAll()

        setDetailValues(intent)

        urlTextView = findViewById(R.id.fileNameText)
        statusTextView = findViewById(R.id.downloadStatusText)
        okButton = findViewById(R.id.okButton)

        val intent = Intent(applicationContext, MainActivity::class.java)
        okButton.setOnClickListener {
            startActivity(intent)
        }

        urlTextView.text = downloadURL
        statusTextView.text =
            if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) "Success" else "Fail"
        statusTextView.setTextColor(if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) Color.GREEN else Color.RED)
    }

    private fun setDetailValues(intent: Intent) {
        val extras: Bundle? = intent.extras
        extras?.let {
            downloadURL = extras.getString(MainActivity.nameKey)
            downloadStatus = extras.getInt(MainActivity.statusKey)
        }
    }

}
