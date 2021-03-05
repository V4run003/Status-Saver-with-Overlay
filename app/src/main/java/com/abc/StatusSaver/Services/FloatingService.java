package com.abc.StatusSaver.Services;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.abc.StatusSaver.R;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

public class FloatingService extends Service implements FloatingViewListener {
    public static final String EXTRA_CUTOUT_SAFE_AREA = "cutout_safe_area";
    private static final String TAG = "FloatingService";
    private static final int NOTIFICATION_ID = 9083150;
    private static final int AA = 1;
    CircleImageView iconView;
    Context mContext;
    private FloatingViewManager mFloatingViewManager;

    private static Notification createNotification(Context context) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.default_floatingview_channel_id));
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(context.getString(R.string.chathead_content_title));
        builder.setContentText(context.getString(R.string.content_text));
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setCategory(NotificationCompat.CATEGORY_SERVICE);
        return builder.build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mFloatingViewManager != null) {
            return START_STICKY;
        }

        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        final LayoutInflater inflater = LayoutInflater.from(this);
        iconView = (CircleImageView) inflater.inflate(R.layout.widget_chathead, null, false);
        mFloatingViewManager = new FloatingViewManager(this, this);

        mFloatingViewManager.setSafeInsetRect((Rect) intent.getParcelableExtra(EXTRA_CUTOUT_SAFE_AREA));
        final FloatingViewManager.Options options = new FloatingViewManager.Options();
        options.overMargin = (int) (16 * metrics.density);
        mFloatingViewManager.addViewToWindow(iconView, options);
        iconView.setOnLongClickListener(v -> {
            Intent intent2 = new Intent(mContext, FloatingViewService.class);
            mContext.stopService(intent2);
            return false;
        });
        iconView.setOnClickListener(v -> {
            Boolean running = isMyServiceRunning(FloatingViewService.class);
            if (running){
                Intent intent2 = new Intent(mContext, FloatingViewService.class);
                mContext.stopService(intent2);
            } else {
                Intent is2 = new Intent(FloatingService.this, FloatingViewService.class);
                startService(is2);
                iconView.setImageResource(R.mipmap.ic_launcher);
            }





        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(NOTIFICATION_ID, createNotification(this));

        return START_REDELIVER_INTENT;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {

        String NOTIFICATION_CHANNEL_ID = "com.abc.statussaver";
        String channelName = "Status Saver";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_group_6)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public void onDestroy() {
        destroy();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onFinishFloatingView() {
        Intent intent = new Intent(mContext, FloatingViewService.class);
        mContext.stopService(intent);
        Toast.makeText(mContext, "Please exit Whatsapp to close Overlay",
                Toast.LENGTH_SHORT).show();
        stopSelf();
        Log.d(TAG, "deleted");
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onTouchFinished(boolean isFinishing, int x, int y) {
        iconView.animate().alpha(0.9f).setDuration(400);
        iconView.setImageResource(R.mipmap.ic_launcher);
        if (isFinishing) {
            Log.d(TAG, getString(R.string.deleted_soon));

        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {

                iconView.setImageResource(R.mipmap.ic_launcher);
                iconView.animate().alpha(0.4f).setDuration(400);
            }, 1500);

        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void destroy() {
        final Intent intent = new Intent(FloatingService.this, FloatingViewService.class);
        stopService(intent);
        if (mFloatingViewManager != null) {
            mFloatingViewManager.removeAllViewToWindow();
            mFloatingViewManager = null;
        }
    }
}
