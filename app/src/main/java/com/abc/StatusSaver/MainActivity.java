package com.abc.StatusSaver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.abc.StatusSaver.Fragments.GalleryFragment;
import com.abc.StatusSaver.Fragments.SaverFragment;
import com.abc.StatusSaver.Fragments.SettingsFragment;
import com.abc.StatusSaver.Interface.AdlistenerInterface;
import com.abc.StatusSaver.Services.FloatingService;
import com.abc.StatusSaver.Services.FloatingViewService;
import com.abc.StatusSaver.Services.Isevice;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.abc.StatusSaver.Utils.Constants.MyPREFERENCES;

public class MainActivity extends AppCompatActivity implements AdlistenerInterface {
    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    AdRequest adRequest2;
    int i = 0;
    Boolean started;
    private ViewPager viewPager;
    private BottomNavigationView navigationView;
    private TextView textView;
    private final ViewPager.OnPageChangeListener pageChangeListener =
            new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onPageSelected(int position) {
                    switch (position) {
                        case 0:
                            navigationView.setSelectedItemId(R.id.nav_status);
                            textView.setText("Status Saver");
                            break;
                        case 1:
                            navigationView.setSelectedItemId(R.id.nav_gallery);
                            textView.setText("Saved Status");
                            break;
                        case 2:
                            navigationView.setSelectedItemId(R.id.nav_settings);
                            textView.setText("Settings");
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            };
    private final BottomNavigationView.OnNavigationItemSelectedListener
            navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_status:
                    viewPager.setCurrentItem(0, true);
                    textView.setText("Status Saver");
                    return true;
                case R.id.nav_gallery:
                    viewPager.setCurrentItem(1, true);
                    textView.setText("Saved Status");
                    return true;
                case R.id.nav_settings:
                    viewPager.setCurrentItem(2, true);
                    textView.setText("Settings");
                    return true;

            }
            return false;
        }
    };
    private AdView adView;
    private InterstitialAd InterstitialAd;

    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            started = extras.getBoolean("walkthrough");
            Log.e("started", String.valueOf(started));
            // and get whatever type user account id is
        }

        MobileAds.initialize(this, initializationStatus -> {
        });
        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(String.valueOf(R.string.banner_adid));
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);
        init();
        initInterface();
        String requiredPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int checkVal = this.checkCallingOrSelfPermission(requiredPermission);
        if (checkVal == PackageManager.PERMISSION_DENIED) {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
               requestForPermission();
            }

        }
        if (!hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        {
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

        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        String aa = sharedpreferences.getString("Overlay", "null");
        switch (aa) {
            case "checked":
            case "null":
                if (Settings.canDrawOverlays(
                        this)) {
                    StartIService();
                }

                break;
            case "unchecked":
                if (Settings.canDrawOverlays(
                        this)) {
                    Intent intent = new Intent(MainActivity.this, Isevice.class);
                    stopService(intent);
                    Intent intent2 = new Intent(MainActivity.this, FloatingService.class);
                    stopService(intent2);
                    Intent intent3 = new Intent(MainActivity.this, FloatingViewService.class);
                    stopService(intent3);
                }
                break;
        }
        SharedPreferences darkModePref = this.getSharedPreferences
                (MyPREFERENCES, Context.MODE_PRIVATE);
        String dark = darkModePref.getString("Uimode", "null");
        if (dark.equals("checked")) {
            int currentNightMode = getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        } else if (dark.equals("unchecked")) {
            int currentNightMode = getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

    }


    private void requestForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    101
            );
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    101
            );
        }

    }

    private void StartIService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            startForegroundService(new Intent(this, Isevice.class));
        } else {
            startService((new Intent(this, Isevice.class)));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // getFile();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        //request for the permission
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void init() {
        viewPager = findViewById(R.id.view_pager);
        navigationView = findViewById(R.id.bottom_nav);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new SaverFragment());
        viewPagerAdapter.addFragment(new GalleryFragment());
        viewPagerAdapter.addFragment(new SettingsFragment());
        viewPager.setAdapter(viewPagerAdapter);
        textView = findViewById(R.id.title);
        if (started!=null) {
            if (started){
                viewPager.setCurrentItem(2);
            }
        } else {


        }
    }

    @Override
    protected void onRestart() {

        if (Settings.canDrawOverlays(
                this)) {
            StartIService();
        }


        super.onRestart();
    }

    @Override
    protected void onResume() {

        if (Settings.canDrawOverlays(
                this)) {
            StartIService();
        }

        super.onResume();
    }

    private void initInterface() {
        viewPager.addOnPageChangeListener(pageChangeListener);
        navigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

    }

    public void refreshMyData() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    @Override
    public void ItemDetail(int downloadedTimes) {

    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {

        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        String aa = sharedpreferences.getString("Overlay", "null");
        if (aa.equals("unchecked")) {
            if (Settings.canDrawOverlays(
                    this)) {
                Intent intent = new Intent(MainActivity.this, Isevice.class);
                stopService(intent);
                Intent intent2 = new Intent(MainActivity.this, FloatingService.class);
                stopService(intent2);
                Intent intent3 = new Intent(MainActivity.this, FloatingViewService.class);
                stopService(intent3);
            }

        }


        super.onDestroy();
        //Intent broadcastIntent = new Intent("ac.in.ActivityRecognition.RestartSensor");
        //sendBroadcast(broadcastIntent);


    }

    public Boolean Isstarted() {
        return started;
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
            if (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc", String.valueOf(e));
        }
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final ArrayList<Fragment> fragments;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {

            super(fm);
            this.fragments = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        void addFragment(Fragment fragment) {
            fragments.add(fragment);
        }
    }
}