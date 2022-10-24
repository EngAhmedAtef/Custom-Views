package com.udacity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private val notificationID = 0
    private var downloadStatus: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Create the notification channel
        createNotificationChannel()

        // Receive the completion broadcast
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // Custom button onclick
        custom_button.setOnClickListener {
            if (URL.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Please select one of the options to download",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                custom_button.buttonState = ButtonState.Loading
                download()
            }
        }
    }

    // BroadcastReceiver object
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                val downloadManager =
                    getSystemService(DownloadManager::class.java) as DownloadManager
                val query = DownloadManager.Query()
                query.setFilterById(downloadID)
                val downloaded = downloadManager.query(query)
                if (downloaded.moveToFirst()) {
                    downloadStatus =
                        downloaded.getInt(downloaded.getColumnIndex(DownloadManager.COLUMN_STATUS))
                }
                getSystemService(NotificationManager::class.java).sendNotification(
                    applicationContext,
                    CHANNEL_ID
                )
                custom_button.buttonState = ButtonState.Completed
            }
        }
    }

    // Download function
    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request) // enqueue puts the download request in the queue.
    }

    // Radio buttons
    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.id) {
                R.id.glideButton -> if (checked) {
                    URL = glideURL
                }
                R.id.loadAppButton -> if (checked) {
                    URL = loadAppURL
                }
                R.id.retrofitButton -> if (checked) {
                    URL = retrofitURL
                }
            }
        }
    }

    // Notification
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
                description = getString(R.string.notification_description)
                title = getString(R.string.notification_title)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                notificationChannel
            )
        }
    }

    private fun NotificationManager.sendNotification(context: Context, channelId: String) {
        val detailIntent = Intent(applicationContext, DetailActivity::class.java).apply {
            putExtra(statusKey, downloadStatus)
            putExtra(nameKey, URL)
            cancel(notificationID)
        }
        val detailPendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationID,
            detailIntent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentText(getString(R.string.notification_description))
            .setContentTitle(getString(R.string.notification_title))
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_assistant_black_24dp,
                getString(R.string.button_name),
                detailPendingIntent
            )
            .build()

        notify(notificationID, notification)
    }

    // Companion object
    companion object {
        private var URL = ""
        private const val loadAppURL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val glideURL =
            "https://github.com/bumptech/glide"
        private const val retrofitURL =
            "https://github.com/square/retrofit"
        private const val CHANNEL_ID = "channelId"
        const val statusKey = "status"
        const val nameKey = "name"
    }

}
