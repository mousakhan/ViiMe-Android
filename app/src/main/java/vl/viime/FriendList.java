package vl.viime;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by mousakhan on 2017-09-26.
 */

public class FriendList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] name;
    private final String[] imageUrl;

    public FriendList(Activity context,
                      String[] name, String[] imageUrl) {
        super(context, R.layout.friend_list, name);
        this.context = context;
        this.name = name;
        this.imageUrl = imageUrl;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.friend_list, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        Glide.with(context)
                .load(imageUrl[position])
                .dontAnimate()
                .placeholder(R.drawable.empty_profile)
                .into(imageView);


        txtTitle.setText(name[position]);

//        imageView.setImageResource(imageId[position]);
        return rowView;
    }

}
