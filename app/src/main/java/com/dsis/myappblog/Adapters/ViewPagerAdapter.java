package com.dsis.myappblog.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.dsis.myappblog.R;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    private int images[] = {
            R.drawable.logo,
            R.drawable.logo,
            R.drawable.logo
    };

    private int titles[] = {
            R.string.title_one,
            R.string.title_two,
            R.string.title_tree
    };

    private int desc[] = {
            R.string.desc_title_one,
            R.string.desc_title_two,
            R.string.desc_title_tree
    };

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull View container, int position) {
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.view_pager, (ViewGroup) container, false);

        ImageView imageView = v.findViewById(R.id.imgViewPager);
        TextView textTitle = v.findViewById(R.id.txtTitleViewPager);
        TextView textDesc = v.findViewById(R.id.txtDescViewPager);

        imageView.setImageResource(images[position]);
        textTitle.setText(titles[position]);
        textDesc.setText(desc[position]);

        ((ViewGroup) container).addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
