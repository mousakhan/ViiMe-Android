package vl.viime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by mousakhan on 2017-09-26.
 */

public class VenuesAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<Venue> venues;

    // 1
    public VenuesAdapter(Context context, List<Venue> venues) {
        this.mContext = context;
        this.venues = venues;
    }

    // 2
    @Override
    public int getCount() {
        return venues.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public Object getItem(int position) {
        return venues.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1
        final Venue venue = venues.get(position);

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.venue_layout, null);
        }

        // 3
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.logo);
        final TextView nameTextView = (TextView)convertView.findViewById(R.id.name);
        final TextView typeTextView = (TextView)convertView.findViewById(R.id.type_text);
        final TextView priceTextView = (TextView)convertView.findViewById(R.id.price_text);
        final TextView locationTextView = (TextView)convertView.findViewById(R.id.location_text);
        final TextView cuisineTextView = (TextView)convertView.findViewById(R.id.cuisine_text);



        // 4
        Glide.with(mContext)
                .load(venue.logo)
                .dontAnimate()
                .into(imageView);
        nameTextView.setText(venue.name);
        typeTextView.setText(venue.type);
        priceTextView.setText(venue.price);
        locationTextView.setText(venue.city);
        cuisineTextView.setText(venue.cuisine);

        return convertView;

    }


}
