package com.abc.StatusSaver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.abc.StatusSaver.R;

public class SlideAdapter extends PagerAdapter {
    private Context context;
    private String[] mHeadings;
    private String[] mContents;
    private int[] mImages;

    public SlideAdapter(Context context, String[] mHeadings, String[] mContents, int[] images) {
        this.context = context;
        this.mContents = mContents;
        this.mHeadings = mHeadings;
        this.mImages = images;
    }

    @Override
    public int getCount() {
        return mHeadings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView heading = view.findViewById(R.id.heading);
        TextView contents = view.findViewById(R.id.contents);
        imageView.setImageResource(mImages[position]);
        heading.setText(mHeadings[position]);
        contents.setText(mContents[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }

}
