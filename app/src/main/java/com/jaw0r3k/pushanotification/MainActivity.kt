package com.jaw0r3k.pushanotification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import kotlin.math.min
import kotlin.random.Random

class MainActivity : AppCompatActivity () {
    var activityResultLauncher: ActivityResultLauncher<Array<String>>? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
         activityResultLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!permissions.entries.any { it.key == Manifest.permission.POST_NOTIFICATIONS && !it.value }) {
                        Toast.makeText(baseContext, "Permission request denied", Toast.LENGTH_SHORT).show()
                    } else {
                        finish();
                    }
                }
            }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }

    fun pushNotification(view: View) {
        val CHANNEL_ID = "jaw0r3k";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher?.launch(Array(1) { Manifest.permission.POST_NOTIFICATIONS })
        }

        val text = findViewById<EditText>(R.id.editTextContent).text.toString()

        val maxLength = ("0" + findViewById<EditText>(R.id.maxLengthEdit).text.toString()).toInt()

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notifications)
            .setContentTitle(findViewById<EditText>(R.id.editTextName).text.toString())
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val channel = NotificationChannel(CHANNEL_ID, getString(R.string.notifications), NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = getString(R.string.notifications)
        }
        val nManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(channel)

        if (text.length > maxLength && maxLength != 0) {
            for (i in 0..text.length / maxLength) {
                builder.setContentText(text.substring(i * maxLength, min(((i + 1) * maxLength), text.length)));
                nManager.notify(Random.nextInt(), builder.build())
            }
        } else {
        nManager.notify(Random.nextInt(), builder.build())
        }
    }
}