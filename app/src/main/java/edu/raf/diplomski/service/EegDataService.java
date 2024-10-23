package edu.raf.diplomski.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.raf.diplomski.R;
import edu.raf.diplomski.gui.MainActivity;
import edu.raf.diplomski.lsl.LslStream;

public class EegDataService extends Service {

    public static final String CHANNEL_ID = "EegDataForegroundServiceChannel";
    private ExecutorService executorService;
    private boolean isRunning = false;
    private LslStream selectedStream;

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        selectedStream = (LslStream) intent.getSerializableExtra("SELECTED_STREAM");
        startForegroundService();
        isRunning = true;

        // Start polling the stream in the background
        startPolling();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        executorService.shutdown();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // We're not binding this service
    }

    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Lsl Foreground Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class); // Replace with your activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("LSL Stream Processing")
                .setContentText("Processing data from LSL Stream")
                .setSmallIcon(R.drawable.logo_splash)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    private void startPolling() {
        executorService.submit(() -> {
            while (isRunning && selectedStream != null) {
                // Perform your LSL polling and calculation here
                double arousal = calculateArousal(selectedStream);
                double valence = calculateValence(selectedStream);

                // Store the data somewhere (SharedPreferences, database, or a static class)
                storeProcessedData(arousal, valence);

                try {
                    Thread.sleep(1000); // Poll every second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private double calculateArousal(LslStream stream) {
        // Perform arousal calculation logic here
        return Math.random(); // Placeholder
    }

    private double calculateValence(LslStream stream) {
        // Perform valence calculation logic here
        return Math.random(); // Placeholder
    }

    private void storeProcessedData(double arousal, double valence) {
        // Store the data so it is available later (SharedPreferences, database, or static class)
        // Example: storing in a static class (or SharedPreferences)
//        ProcessedData.setArousal(arousal);
//        ProcessedData.setValence(valence);
    }
}