package vl.viime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_friends:
                // Clicking into friends list
                Intent friendsIntent = new Intent(HomeActivity.this, FriendsActivity.class);
                HomeActivity.this.startActivity(friendsIntent);
                return true;

            case R.id.action_profile:
                // Clicking profile page
                Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                HomeActivity.this.startActivity(profileIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}
