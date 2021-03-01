package com.abc.StatusSaver.Adapters;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.abc.StatusSaver.Interface.AdlistenerInterface;
import com.abc.StatusSaver.Model.StoryModel;
import com.abc.StatusSaver.R;
import com.abc.StatusSaver.Utils.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Object> filesList;
    int i = 0;
    AdlistenerInterface adlistenerInterface;
    int AD_TYPE = 00;
    int CONTENT_TYPE = 11;

    public StoryAdapter(Context context, ArrayList<Object> filesList, AdlistenerInterface adlistenerInterface) {
        this.context = context;
        this.filesList = filesList;
        this.adlistenerInterface = adlistenerInterface;
    }

    @NonNull
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View contview = LayoutInflater
                            .from(parent.getContext()).inflate(R.layout.card_row,
                                    null,false);
                    return new ViewHolder(contview);



    }



    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

            final StoryModel files = (StoryModel) filesList.get(position);
            final Uri uri = Uri.parse(files.getUri().toString());
            AtomicReference<File> file = new AtomicReference<>(new File(uri.getPath()));
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            if (files.getUri().toString().endsWith(".mp4")) {
                holder.Playicon.setVisibility(View.VISIBLE);
            } else {
                holder.Playicon.setVisibility(View.INVISIBLE);
            }
            if (files.getUri().toString().endsWith(".mp4") || files.getUri().toString().endsWith(".jpg")) {
                Glide.with(context)
                        .load(files.getUri())
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(0.5f)
                        .centerCrop()
                        .into(holder.savedImage);
            }
            String filename = files.getFilename();
            String destPath_scn = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + Constants.SAVE_FOLDER_NAME + filename;
            if (new File(destPath_scn).exists()) {
                holder.downloadID.setVisibility(View.GONE);
                holder.savedText.setVisibility(View.INVISIBLE);
                holder.ShareID.setVisibility(View.VISIBLE);
            }

            holder.savedImage.setOnClickListener(v -> {
                if (files.getUri().toString().endsWith(".mp4")) {
                    Uri VideoURI = FileProvider.getUriForFile(context,
                            context.getApplicationContext().getPackageName()
                                    + ".provider", file.get());
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.setDataAndType(VideoURI, "video/*");
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No application found to open this file.",
                                Toast.LENGTH_LONG).show();
                    }
                } else if (files.getUri().toString().endsWith(".jpg")) {
                    Uri VideoURI = FileProvider.getUriForFile(context,
                            context.getApplicationContext().getPackageName()
                                    + ".provider", file.get());
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.setDataAndType(VideoURI, "image/*");
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No application found to open this file.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            holder.ShareID.setOnClickListener(v -> {
                Toast.makeText(context, "Sharing...",
                        Toast.LENGTH_SHORT).show();
                Uri mainUri = ((StoryModel) filesList.get(position)).getUri();
                if (files.getUri().toString().endsWith(".jpg")) {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("image/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Intent intent = Intent.createChooser(sharingIntent, "Share Image using");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        context.getApplicationContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No application found to open this file.",
                                Toast.LENGTH_LONG).show();
                    }
                } else if (files.getUri().toString().endsWith(".mp4")) {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("video/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent intent = Intent.createChooser(sharingIntent, "Share Video using");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    try {
                        context.getApplicationContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No application found to open this file.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            holder.downloadID.setOnClickListener(v -> {
                i = i + 1;
                adlistenerInterface.ItemDetail(i);
                holder.downloadID.startAnimation();
                checkFolder();
                final String path = ((StoryModel) filesList.get(position)).getPath();
                file.set(new File(path));
                String destPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + Constants.SAVE_FOLDER_NAME;
                File destFile = new File(destPath);
                try {
                    FileUtils.copyFileToDirectory(file.get(), destFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MediaScannerConnection.scanFile(
                        context,
                        new String[]{destPath + files.getFilename()},
                        new String[]{"*/*"},
                        new MediaScannerConnection.MediaScannerConnectionClient() {
                            public void onMediaScannerConnected() {
                            }

                            public void onScanCompleted(String path, Uri uri1) {
                                Log.d("path: ", path);
                            }
                        });
                int fillcolour = R.color.darkPurple;
                holder.downloadID.setForeground(null);
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_done_white_48dp);
                holder.downloadID.doneLoadingAnimation(fillcolour, bitmap);
                (new Handler()).postDelayed(() -> {
                    holder.downloadID.setVisibility(View.GONE);
                    holder.savedText.setVisibility(View.INVISIBLE);
                    holder.ShareID.setVisibility(View.VISIBLE);
                }, 1000);
            });
            holder.setIsRecyclable(false);

        }

    public void checkFolder() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        Constants.SAVE_FOLDER_NAME;
                File dir = new File(path);

                boolean isDirectoryCreated = dir.exists();
                if (!isDirectoryCreated) {
                    isDirectoryCreated = dir.mkdir();
                }
                if (isDirectoryCreated) {
                    Log.d("Folder", "Already Created");

                }
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position%5==0){
            return AD_TYPE;
        } else if (position==0){
            return CONTENT_TYPE;
        }
        else {
            return CONTENT_TYPE;
        }
    }




    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView savedImage;
        private final CircularProgressButton downloadID;
        private final ImageView Playicon;
        private final TextView savedText;
        private final CircularProgressButton ShareID;
        public ViewHolder(View itemView) {
            super(itemView);
            ShareID = itemView.findViewById(R.id.share_media);
            Playicon = itemView.findViewById(R.id.playIcon_saver);
            savedText = itemView.findViewById(R.id.saved_txt);
            savedImage = itemView.findViewById(R.id.mainImageView);
            downloadID = itemView.findViewById(R.id.downloadID);
        }
    }

    }




