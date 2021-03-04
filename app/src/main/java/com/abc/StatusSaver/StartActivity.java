package com.abc.StatusSaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.abc.StatusSaver.Fragments.SettingsFragment;

import static com.abc.StatusSaver.Utils.Constants.MyPREFERENCES;

public class StartActivity extends AppCompatActivity {
    SharedPreferences firstUppref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        SharedPreferences bootuppref = this.getSharedPreferences
                (MyPREFERENCES, Context.MODE_PRIVATE);
        Boolean boot = bootuppref.getBoolean("firstStart", true);
        if (boot){
            firstUppref =this.getSharedPreferences
                    (MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor;
            editor = firstUppref.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> startActivity(new Intent(StartActivity.this, WalkthroughActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)), 500);
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> startActivity(new Intent(StartActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)), 500);

        }

    }

}