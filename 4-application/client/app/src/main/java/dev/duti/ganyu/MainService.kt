package dev.duti.ganyu

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainService : Service() {
    private lateinit var handler: Handler
    private lateinit var pollingThread: HandlerThread
    private val scope = CoroutineScope(Dispatchers.IO) // Background thread for network calls

    companion object {
        const val CHANNEL_ID = "MyServiceChannel"
    }

    private lateinit var handlerThread: HandlerThread
    private lateinit var serviceHandler: Handler

    override fun onCreate() {
        super.onCreate()
        Log.d("MainService", "Service onCreate called")
        createNotificationChannel()
        // Set up background thread and handler
        handlerThread = HandlerThread("MyServiceThread").apply { start() }
        serviceHandler = Handler(handlerThread.looper)
        pollingThread = HandlerThread("PollingThread").apply { start() }
        handler = Handler(pollingThread.looper)
    }

    // Create notification channel for Android 8+
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "My Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create and display the notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("My Service")
            .setContentText("Running...")
            .build()

        // Start as a foreground service
        startForeground(1, notification)

        // Schedule repeating task
        serviceHandler.postDelayed(object : Runnable {
            override fun run() {
                Log.d("MyService", "Background task running")
            }
        }, 0)
        startLongPolling()
        return START_STICKY // Restart if killed by the system
    }

    private fun startLongPolling() {
        scope.launch {
            while (true) {
                try {
                    val url = URL("https://tmp.duti.dev/poll?id=test")
                    val connection = url.openConnection() as HttpsURLConnection
                    connection.requestMethod = "GET"
                    connection.readTimeout = 60_000 // 60 seconds for long-poll

                    val responseCode = connection.responseCode
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        val response = connection.inputStream.bufferedReader().use { it.readText() }
                        Log.d("PollingService", "Received: $response")
                        sendNotification("Poll", response)
                    }
                } catch (e: Exception) {
                    Log.e("PollingService", "Polling failed" + e.message)
                }
                kotlinx.coroutines.delay(1000) // Wait 5 seconds before next poll
            }
        }
    }
    private fun sendNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Set your own icon here
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE) // Set as message
            .setAutoCancel(true)  // Dismiss on tap
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Add sound/vibration
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(100, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerThread.quit() // Stop the background thread
        pollingThread.quit()
        Log.d("MyService", "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null // Not a bound service
}