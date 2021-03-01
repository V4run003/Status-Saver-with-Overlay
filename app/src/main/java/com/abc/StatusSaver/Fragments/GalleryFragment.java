package com.abc.StatusSaver.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.abc.StatusSaver.Adapters.GalleryAdapter;
import com.abc.StatusSaver.Model.GalleryModel;
import com.abc.StatusSaver.R;
import com.abc.StatusSaver.Utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GalleryFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout recyclerLayout;
    private View view;
    private TextView nodata;
    private RelativeLayout gototop;
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gallery_fragment, container, false);
        nodata = view.findViewById(R.id.no_data);
        gototop = view.findViewById(R.id.goto_top_btn);
        recyclerView = view.findViewById(R.id.gallery_recycler_view);
        initComponents();
        setUpRecyclerView();
        return view;


    }

    private void initComponents() {
        recyclerView = view.findViewById(R.id.gallery_recycler_view);
        recyclerLayout = view.findViewById(R.id.swipeRecyclerView_gallery);
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        GalleryAdapter recyclerViewAdapter = new GalleryAdapter(mContext, getData());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private ArrayList<GalleryModel> getData() {
        ArrayList<GalleryModel> filesList = new ArrayList<>();
        GalleryModel f;
        String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.SAVE_FOLDER_NAME;
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
                f = new GalleryModel();
                f.setName("Saved Status: " + (i + 1));
                f.setFilename(file.getName());
                f.setUri(Uri.fromFile(file));
                f.setPath(files[i].getAbsolutePath());
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
}
