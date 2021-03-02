package com.abc.StatusSaver.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.abc.StatusSaver.BuildConfig;
import com.abc.StatusSaver.MainActivity;
import com.abc.StatusSaver.R;
import com.abc.StatusSaver.Services.FloatingService;
import com.abc.StatusSaver.Services.FloatingViewService;
import com.abc.StatusSaver.Services.Isevice;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

import static com.abc.StatusSaver.R.drawable.setting_outline_cont;
import static com.abc.StatusSaver.R.drawable.settings_outline_focus;
import static com.abc.StatusSaver.Utils.Constants.MyPREFERENCES;

public class SettingsFragment extends Fragment {
    RelativeLayout shareApp;
    RelativeLayout focusableOverlay;
    Boolean isTuto;
    private Context mContext;
    private SharedPreferences darkModePref;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        SwitchMaterial darkMode = view.findViewById(R.id.themeid);
        SwitchMaterial bootup = view.findViewById(R.id.boot_id);
        focusableOverlay = view.findViewById(R.id.head1_cont);
        SwitchMaterial switchMaterial = view.findViewById(R.id.overlayid);
        shareApp = view.findViewById(R.id.shareApp);
        MainActivity activity = (MainActivity) getActivity();
        if (activity.Isstarted()!=null){
            isTuto = activity.Isstarted();
        } else
            isTuto = null;

        if (isTuto!=null){
            if (isTuto) {
                focusableOverlay.setBackground(getResources().getDrawable(settings_outline_focus));
                Animation anim = new AlphaAnimation(0.3f, 1.0f);
                anim.setDuration(500); //You can manage the blinking time with this parameter
                anim.setStartOffset(50);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setRepeatCount(Animation.INFINITE);
                focusableOverlay.startAnimation(anim);

            }
        } else
        {
            focusableOverlay.setBackground(getResources().getDrawable(setting_outline_cont));
        }



        SharedPreferences sharedpreferencesch = SettingsFragment.this.getActivity().getSharedPreferences
                (MyPREFERENCES, Context.MODE_PRIVATE);
        String ch = sharedpreferencesch.getString("Overlay", "null");
        switch (ch) {
            case "checked":
                switchMaterial.setChecked(true);
                break;
            case "unchecked":
                Intent intent = new Intent(mContext, Isevice.class);
                Intent intent2 = new Intent(mContext, FloatingViewService.class);
                Intent intent3 = new Intent(mContext, FloatingService.class);
                mContext.stopService(intent2);
                mContext.stopService(intent);
                mContext.stopService(intent3);
                switchMaterial.setChecked(false);
                break;
            case "null":
                switchMaterial.setChecked(false);
                break;
        }

        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isUnchecked();
        });

        darkModePref = SettingsFragment.this.getActivity().getSharedPreferences
                (MyPREFERENCES, Context.MODE_PRIVATE);
        String dark = darkModePref.getString("Uimode", "null");
        switch (dark) {
            case "checked":
                darkMode.setChecked(true);
                break;
            case "unchecked":
                darkMode.setChecked(false);
                break;
            case "null":
                darkMode.setChecked(false);
                break;
        }
        darkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            DarkMode();
        });

        shareApp.setOnClickListener(v -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Status Saver");
                String shareMessage = "\n\n*Introducing Status Saver*\n " +
                        "Use Whatsapp status saver within whatsapp\n"
                        +
                        "App Available in Play Store\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" +
                        BuildConfig.APPLICATION_ID + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share to"));
            } catch (Exception e) {
                //e.toString();
            }
        });

        SharedPreferences sharedpreferenceboot = SettingsFragment.this.getActivity().getSharedPreferences
                (MyPREFERENCES, Context.MODE_PRIVATE);
        String boot = sharedpreferenceboot.getString("boot", "null");
        switch (boot) {
            case "checked":
                bootup.setChecked(true);
                break;
            case "unchecked":
                bootup.setChecked(false);
                break;
            case "null":
                bootup.setChecked(true);
                break;
        }

        bootup.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Bootup();
        });

        return view;
    }

    private void Bootup() {
        SharedPreferences bootuppref = SettingsFragment.this.getActivity().getSharedPreferences
                (MyPREFERENCES, Context.MODE_PRIVATE);
        String boot = bootuppref.getString("boot", "null");
        if (boot.equals("checked")) {
            bootuppref = SettingsFragment.this.getActivity().getSharedPreferences
                    (MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorDark;
            editorDark = bootuppref.edit();
            editorDark.putString("boot", "unchecked");
            editorDark.apply();
        } else if (boot.equals("unchecked")) {
            bootuppref = SettingsFragment.this.getActivity().getSharedPreferences
                    (MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorDark;
            editorDark = bootuppref.edit();
            editorDark.putString("boot", "checked");
            editorDark.apply();

        } else if (boot.equals("null")) {
            bootuppref = SettingsFragment.this.getActivity().getSharedPreferences
                    (MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorDark;
            editorDark = bootuppref.edit();
            editorDark.putString("boot", "checked");
            editorDark.apply();
        }

    }

    private void DarkMode() {
        Intent intent = new Intent(mContext, Isevice.class);
        Intent intent2 = new Intent(mContext, FloatingViewService.class);
        Intent intent3 = new Intent(mContext, FloatingService.class);
        mContext.stopService(intent3);
        mContext.stopService(intent2);
        mContext.stopService(intent);
        ((MainActivity) Objects.requireNonNull(getActivity())).refreshMyData();

        darkModePref = SettingsFragment.this.getActivity().getSharedPreferences
                (MyPREFERENCES, Context.MODE_PRIVATE);
        String dark = darkModePref.getString("Uimode", "null");
        if (dark.equals("checked")) {
            darkModePref = SettingsFragment.this.getActivity().getSharedPreferences
                    (MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorDark;
            editorDark = darkModePref.edit();
            editorDark.putString("Uimode", "unchecked");
            editorDark.apply();
        } else if (dark.equals("unchecked")) {
            darkModePref = SettingsFragment.this.getActivity().getSharedPreferences
                    (MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorDark;
            editorDark = darkModePref.edit();
            editorDark.putString("Uimode", "checked");
            editorDark.apply();

        } else if (dark.equals("null")) {
            darkModePref = SettingsFragment.this.getActivity().getSharedPreferences
                    (MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorDark;
            editorDark = darkModePref.edit();
            editorDark.putString("Uimode", "checked");
            editorDark.apply();
        }
    }

    private void startiservice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(new Intent(mContext, Isevice.class));
        } else {
            mContext.startService((new Intent(mContext, Isevice.class)));
        }
    }

    private void isUnchecked() {
        SharedPreferences sharedpreferences1 = SettingsFragment.this.getActivity().getSharedPreferences
                (MyPREFERENCES, Context.MODE_PRIVATE);
        String aa = sharedpreferences1.getString("Overlay", "null");
        if (aa.equals("unchecked")) {
            startiservice();
            SharedPreferences sharedpreferences;
            SharedPreferences.Editor editor;
            sharedpreferences = SettingsFragment.this.getActivity().getSharedPreferences
                    (MyPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
            editor.putString("Overlay", "checked");
            editor.apply();
        } else if (aa.equals("checked")) {
            SharedPreferences sharedpreferences2;
            sharedpreferences2 = SettingsFragment.this.getActivity().getSharedPreferences
                    (MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor2;
            editor2 = sharedpreferences2.edit();
            editor2.putString("Overlay", "unchecked");
            editor2.apply();
            Intent intent = new Intent(mContext, Isevice.class);
            mContext.stopService(intent);
            Intent intent2 = new Intent(mContext, FloatingViewService.class);
            mContext.stopService(intent2);
            Intent intent3 = new Intent(mContext, FloatingService.class);
            mContext.stopService(intent3);

        } else if (aa.equals("null")) {
            SharedPreferences sharedpreferences2;
            sharedpreferences2 = SettingsFragment.this.getActivity().getSharedPreferences
                    (MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor2;
            editor2 = sharedpreferences2.edit();
            editor2.putString("Overlay", "checked");
            editor2.apply();
        }
    }

}
