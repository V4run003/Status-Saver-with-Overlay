package com.abc.StatusSaver.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.abc.StatusSaver.Adapters.StoryAdapter;
import com.abc.StatusSaver.Interface.AdlistenerInterface;
import com.abc.StatusSaver.Model.StoryModel;
import com.abc.StatusSaver.R;
import com.abc.StatusSaver.Utils.Constants;
import com.efaso.admob_advanced_native_recyvlerview.AdmobNativeAdAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Executors;

public class SaverFragment extends Fragment implements AdlistenerInterface {

    AdRequest adRequest2;
    private Context mContext;
    private RecyclerView recyclerView;
    private View view;
    private TextView nodata;
    private RelativeLayout gototop;
    private SwipeRefreshLayout recyclerLayout;
    private InterstitialAd InterstitialAd;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.saver_fragment, container, false);
        new Thread( new Runnable() { @Override public void run() {
            new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("99D32CE93ABCCE3BAEC035DEF9D69FB7"));
            adRequest2 = new AdRequest.Builder()
                    .addTestDevice("99D32CE93ABCCE3BAEC035DEF9D69FB7")
                    .build();
        } } ).start();

        nodata = view.findViewById(R.id.no_data);
        recyclerView = view.findViewById(R.id.recycler_view);
        gototop = view.findViewById(R.id.goto_top_btn);
        init();
        setUpRecyclerView();
        return view;

    }

    private void init() {

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerLayout = view.findViewById(R.id.swipeRecyclerView_saver);
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
            (new Handler()).postDelayed(() -> {
                recyclerLayout.setRefreshing(false);
                Toast.makeText(mContext, "Refreshed!",
                        Toast.LENGTH_SHORT).show();
            }, 2000);

        });
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        StoryAdapter recyclerViewAdapter = new StoryAdapter(mContext, getData(), this);
        recyclerView.setAdapter(recyclerViewAdapter);
        new Thread( new Runnable() { @SuppressLint("NotifyDataSetChanged")
        @Override public void run() {
            Boolean net = isNetworkAvailable();
            if (net) {

                AdmobNativeAdAdapter admobNativeAdAdapter = AdmobNativeAdAdapter.Builder
                        .with(
                                getString(R.string.native_ad_id),//Create a native ad id from admob console
                                recyclerViewAdapter,//The adapter you would normally set to your recyClerView
                                "medium"//Set it with "small","medium" or "custom"
                        )
                        .adItemIterval(5)//native ad repeating interval in the recyclerview
                        .build();
                recyclerView.setAdapter(admobNativeAdAdapter);

            } else {
                recyclerView.setAdapter(recyclerViewAdapter);
            }


            //set your RecyclerView adapter with the admobNativeAdAdapter



            recyclerView.setHasFixedSize(true);
        } } ).start();


    }

    private ArrayList<Object> getData() {
        ArrayList<Object> filesList = new ArrayList<>();

            StoryModel f;
            String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + Constants.FOLDER_NAME + "Media/.Statuses";
            File targetDirector = new File(targetPath);
            File[] files = targetDirector.listFiles();
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

    @Override
    public void ItemDetail(int downloadedTimes) {

        if (downloadedTimes == 3) {
            ProgressDialog pd = new ProgressDialog(mContext);
            pd.setMessage("Loading...");
            pd.show();

            com.google.android.gms.ads.interstitial.InterstitialAd.load(mContext, getString(R.string.interstitial_ad_id), adRequest2, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    InterstitialAd = interstitialAd;
                    pd.dismiss();


                    Log.i("aa", "onAdLoaded");
                    InterstitialAd.show(getActivity());
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.i("ab", loadAdError.getMessage());
                    InterstitialAd = null;
                    pd.dismiss();
                }
            });
        } else if (downloadedTimes == 6) {
            ProgressDialog pd = new ProgressDialog(mContext);
            pd.setMessage("Loading...");
            pd.show();
            com.google.android.gms.ads.interstitial.InterstitialAd.load(mContext, getString(R.string.interstitial_ad_id), adRequest2, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    InterstitialAd = interstitialAd;
                    pd.dismiss();


                    Log.i("aa", "onAdLoaded");
                    InterstitialAd.show(getActivity());
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.i("ab", loadAdError.getMessage());
                    InterstitialAd = null;
                    pd.dismiss();
                }
            });
        } else if (downloadedTimes == 12) {
            ProgressDialog pd = new ProgressDialog(mContext);
            pd.setMessage("Loading...");
            pd.show();

            com.google.android.gms.ads.interstitial.InterstitialAd.load(mContext, getString(R.string.interstitial_ad_id), adRequest2, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    InterstitialAd = interstitialAd;
                    pd.dismiss();


                    Log.i("aa", "onAdLoaded");
                    InterstitialAd.show(getActivity());
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.i("ab", loadAdError.getMessage());
                    InterstitialAd = null;
                    pd.dismiss();
                }
            });
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
