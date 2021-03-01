package com.abc.StatusSaver.Adapters;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.abc.StatusSaver.Model.GalleryModel;
import com.abc.StatusSaver.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<com.abc.StatusSaver.Adapters.GalleryAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<GalleryModel> filesList;

    public GalleryAdapter(Context context, ArrayList<GalleryModel> filesList) {
        this.context = context;
        this.filesList = filesList;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_cardrow,
                null, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull com.abc.StatusSaver.Adapters.GalleryAdapter.ViewHolder holder,
                                 final int position) {
        final GalleryModel files = filesList.get(position);
        final Uri uri = Uri.parse(files.getUri().toString());
        final File file = new File(uri.getPath());
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if(files.getUri().toString().endsWith(".mp4"))
        {
            holder.playIcon.setVisibility(View.VISIBLE);
        }else{
            holder.playIcon.setVisibility(View.INVISIBLE);
        }
        Glide.with(context)
                .load(files.getUri())
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.5f)
                .centerCrop()
                .into(holder.savedImage);
        holder.savedImage.setOnClickListener(v -> {
            if(files.getUri().toString().endsWith(".mp4")){
                Uri VideoURI = FileProvider.getUriForFile(context,
                        context.getApplicationContext().getPackageName() + ".provider",file);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setDataAndType(VideoURI, "video/*");
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {

                }
            }else if(files.getUri().toString().endsWith(".jpg")){
                Uri VideoURI = FileProvider.getUriForFile(context,
                        context.getApplicationContext().getPackageName() + ".provider",file);
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
        holder.repostID.setOnClickListener(v -> {
            Uri mainUri = Uri.fromFile(file);
            if(files.getUri().toString().endsWith(".jpg")){
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.setPackage("com.whatsapp");
                try {
                    context.startActivity(Intent.createChooser(sharingIntent, "Share Image using"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application found to open this file.",
                            Toast.LENGTH_LONG).show();
                }
            }else if(files.getUri().toString().endsWith(".mp4")){
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("video/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.setPackage("com.whatsapp");
                try {
                    context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application found to open this file.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.deleteID.setOnClickListener(v -> {
            String path = filesList.get(position).getPath();
            File file1 = new File(path);
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Delete?")
                    .setMessage("Do you want to delete this file")
                    .setPositiveButton("Delete", (dialogInterface, i) -> {
                        try {
                            if (file1.exists()) {
                                boolean del = file1.delete();
                                filesList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, filesList.size());
                                notifyDataSetChanged();
                                if(del){
                                    MediaScannerConnection.scanFile(context,
                                            new String[]{ path, path},
                                            new String[]{ "image/jpg","video/mp4"},
                                            new MediaScannerConnection.MediaScannerConnectionClient()
                                            {
                                                public void onMediaScannerConnected()
                                                {
                                                }
                                                public void onScanCompleted(String path1, Uri uri1)
                                                { Log.d("Video path: ", path1);
                                                }
                                            });
                                }
                            }
                            dialogInterface.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        });

        holder.shareID.setOnClickListener(v -> {
            Uri mainUri = Uri.fromFile(file);
            if(files.getUri().toString().endsWith(".jpg")){
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    context.startActivity(Intent.createChooser(sharingIntent,
                            "Share Image using"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application found to open this file.",
                            Toast.LENGTH_LONG).show();
                }
            }else if(files.getUri().toString().endsWith(".mp4")){
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("video/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application found to open this file.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView savedImage;
        private final ImageView playIcon;
       private final Button repostID;
        private final Button shareID;
        private final Button deleteID;
        public ViewHolder(View itemView) {
            super(itemView);
            savedImage = itemView.findViewById(R.id.ThumbimgView);
            playIcon = itemView.findViewById(R.id.playIcon_gallery);
            repostID =itemView.findViewById(R.id.repostbtn);
            shareID = itemView.findViewById(R.id.Share_btn);
            deleteID =  itemView.findViewById(R.id.Delete_btn);
        }
    }
}
