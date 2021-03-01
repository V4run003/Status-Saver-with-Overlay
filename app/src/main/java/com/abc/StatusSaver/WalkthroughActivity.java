package com.abc.StatusSaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abc.StatusSaver.Adapters.SlideAdapter;
import com.abc.StatusSaver.Fragments.SettingsFragment;

import java.util.List;

public class WalkthroughActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private LinearLayout dotLayout;
    private TextView[] dots;
    private int currentPage;
    private Button next_btn, back_btn;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);
        viewPager = findViewById(R.id.view_pager);
        dotLayout = findViewById(R.id.dot_layout);
        next_btn = findViewById(R.id.next_btn);
        back_btn = findViewById(R.id.back_btn);
        back_btn.setVisibility(View.GONE);
        int[] slideImages = {
                R.drawable.walk4,
                R.drawable.overlay_perm,
                R.drawable.walk2,
                R.drawable.walk3,
                //  R.raw.chat_anim2,
                //  R.raw.event_anim,
                //  R.raw.events
        };
        String[] SlideHeading = {
                "Welcome", "Bubbles", "Grant Permissions", "Turn OFF Battery Saving"
        };
        String[] slide_contents = {
                "Best User friendly Status Saving App \n" +
                        "Save Status on a single tap!",
                "Save Status easily from Within Whatsapp",
                "Accept Stats permission for proper Working of overlay",
                "Turn off app based battery Saver for best experience"
        };
        SlideAdapter slideAdapter = new SlideAdapter(this, SlideHeading, slide_contents, slideImages);
        viewPager.setAdapter(slideAdapter);
        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewListener);
        next_btn.setText("Next");

        next_btn.setOnClickListener(v -> {
            if (currentPage == 1){
                overlayPerm();
            } else if (currentPage == 2){

                statsPerm();
            } else if (currentPage == 3){
                batteryperm();
            }

            if (!TextUtils.equals(next_btn.getText(), "Finish"))
                viewPager.setCurrentItem(currentPage + 1);
            else {
                Intent intent = new Intent(WalkthroughActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
               intent.putExtra("walkthrough",true);
               startActivity(intent);
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(currentPage - 1);
            }
        });
    }

    private void batteryperm() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                addAutoStartup();
                miuibattery();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }

    private void miuibattery() {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"));
            intent.putExtra("package_name", getPackageName());
            intent.putExtra("package_label", getText(R.string.app_name));
            startActivity(intent);
        } catch (ActivityNotFoundException anfe) {
        }
    }

    private void addAutoStartup() {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if  (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc" , String.valueOf(e));
        }
    }


    private void statsPerm() {

        if(!hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    private void overlayPerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(
                this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        assert appOps != null;
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public void addDotsIndicator(int position) {
        dots = new TextView[4];
        dotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {

            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(40);
            dots[i].setPadding(5, 0, 5, 0);
            dots[i].setTextColor(getResources().getColor(R.color.light_accent));

            dotLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.disc_blue));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onPageSelected(int i) {

            addDotsIndicator(i);
            currentPage = i;

            if (i == 0) {
                next_btn.setText("Next");
                back_btn.setVisibility(View.GONE);
                back_btn.setAnimation(AnimationUtils.loadAnimation(WalkthroughActivity.this, R.anim.fade_out));
            } else if (i == dots.length - 1) {
                next_btn.setText("Finish");
                back_btn.setVisibility(View.VISIBLE);
            } else {
                next_btn.setText("Next");
                if (back_btn.getVisibility() == View.GONE) {
                    back_btn.setVisibility(View.VISIBLE);
                    back_btn.setAnimation(AnimationUtils.loadAnimation(WalkthroughActivity.this, R.anim.fade_in));
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

}