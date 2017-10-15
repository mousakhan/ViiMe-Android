package vl.viime;

/**
 * Created by mousakhan on 2017-10-14.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private List<User> mFriends;
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public UserAdapter(Context context, List<User> contacts) {
        mFriends = contacts;
        mContext = context;
    }
    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.user, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        User user = mFriends.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(user.username);

        ImageView profile = viewHolder.profileImage;

        if (!user.profile.equals("") && !(user.profile == null)) {
                Glide.with(mContext)
                        .load(user.profile)
                        .dontAnimate()
                        .placeholder(R.drawable.empty_profile)
                        .into(profile);
        }

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mFriends.size();
    }


    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public ImageView profileImage;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.username);
            profileImage = (ImageView) itemView.findViewById(R.id.profile_picture);
        }
    }
}