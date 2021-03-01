package com.abc.StatusSaver.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.abc.StatusSaver.Adapters.OverlayAdapter;
import com.abc.StatusSaver.MainActivity;
import com.abc.StatusSaver.Model.StoryModel;
import com.abc.StatusSaver.R;
import com.abc.StatusSaver.Utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class FloatingViewService extends Service {

    int xstart, ystart;
    private WindowManager mWindowManager;
    private View mFloatingView;
    private RecyclerView recyclerView;
    private Context mContext;
    private TextView nodata;
    private RelativeLayout gototop;
    private View expandedView;
    private View collapsedView;
    private SwipeRefreshLayout recyclerLayout;

    public FloatingViewService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCreate() {
        Bundle b = new Bundle();
        xstart = b.getInt("x");
        ystart = b.getInt("y");


        super.onCreate();
        mContext = this;
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(mContext).inflate(R.layout.layout_floating_widget, null);
        nodata = mFloatingView.findViewById(R.id.no_data);
        gototop = mFloatingView.findViewById(R.id.goto_top_btn);
        init();
        setUpRecyclerView();
        setFloating();
    }

    private void init() {
        recyclerView = mFloatingView.findViewById(R.id.recycler_view_overlay);
        recyclerLayout = mFloatingView.findViewById(R.id.swipeRecyclerView_overLay);
        recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            LinearLayoutManager myLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            assert myLayoutManager != null;
            int scrollPosition = myLayoutManager.findFirstVisibleItemPosition();
            if (scrollPosition > 3) {
                gototop.setVisibility(View.VISIBLE);
                gototop.setOnClickListener(v1 -> myLayoutManager.smoothScrollToPosition
                        (recyclerView, null, 0));
            } else gototop.setVisibility(View.INVISIBLE);
        });
        recyclerLayout.setOnRefreshListener(() -> {
            recyclerLayout.setRefreshing(true);
            setUpRecyclerView();
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerLayout.setRefreshing(false);

                }
            }, 2000);

        });
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        OverlayAdapter recyclerViewAdapter = new OverlayAdapter(mContext, getData());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
    }

    private ArrayList<Object> getData() {

        ArrayList<Object> filesList = new ArrayList<>();
        StoryModel f;
        String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + Constants.FOLDER_NAME + "Media/.Statuses";
        File targetDirector = new File(targetPath);
        File[] files = targetDirector.listFiles();
        //            noImageText.setVisibility(View.INVISIBLE);
        try {
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {

                    if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                        return -1;
                    } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                        return +1;
                    } else {
                        return 0;
                    }
                }
            });

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                f = new StoryModel();
                f.setName("Status: " + (i + 1));
                f.setUri(Uri.fromFile(file));
                f.setPath(files[i].getAbsolutePath());
                f.setFilename(file.getName());
                filesList.add(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int size = filesList.size();
        if (size < 1) {
            recyclerView.setVisibility(View.GONE);
            nodata.setVisibility(View.VISIBLE);
        }
        return filesList;


    }

    @SuppressLint("RtlHardcoded")
    private void setFloating() {
        WindowManager.LayoutParams params;

        if (Build.VERSION.SDK_INT > 25) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        //Add the view to the window.

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;


        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        //The root element of the collapsed view layout
        collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        expandedView = mFloatingView.findViewById(R.id.expanded_container);
        int heightExp = (int) (getResources().getDisplayMetrics().heightPixels * 0.5f);
        int widthExp = (int) (getResources().getDisplayMetrics().widthPixels * 0.7f);

        RelativeLayout.LayoutParams layoutParams;
        layoutParams = new RelativeLayout.LayoutParams(widthExp, heightExp);
        expandedView.setLayoutParams(layoutParams);


        //Set the close button
        ImageView closeButtonCollapsed = mFloatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(view -> {
            stopSelf();
            Toast.makeText(mContext, "Please exit Whatsapp to close Overlay",
                    Toast.LENGTH_SHORT).show();
        });

        //Set the close button
        ImageView closeButton = mFloatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(view -> {
            expandedView.animate().alpha(0f).setDuration(300);
            expandedView.setVisibility(View.GONE);
            stopSelf();
            final Intent intent2 = new Intent(FloatingViewService.this, FloatingService.class);
            ContextCompat.startForegroundService(FloatingViewService.this, intent2);
        });

        //Open the application on thi button click
        ImageView openButton = (ImageView) mFloatingView.findViewById(R.id.open_button);
        openButton.setOnClickListener(view -> {
            //Open the application  click.
            Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            //close the service and remove view from the view hierarchy
            stopSelf();
        });


        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.animate().alpha(0f).setDuration(300);
                                collapsedView.setVisibility(View.GONE);
                                expandedView.animate().alpha(1f).setDuration(300);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }


    @Override
    public void onDestroy() {
        Intent broadcastIntent = new Intent("ac.in.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
        super.onDestroy();
    }
}
