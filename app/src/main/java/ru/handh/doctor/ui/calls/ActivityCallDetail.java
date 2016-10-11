package ru.handh.doctor.ui.calls;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.handh.doctor.R;
import ru.handh.doctor.io.network.responce.calls.DataCall;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Utils;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link FragmentCallsStart}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link FragmentCallDetail}.
 */
public class ActivityCallDetail extends AppCompatActivity {

    public Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Utils.getCustomColor(this, R.color.colorAccent));


        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(Utils.getCustomDrawable(R.drawable.ic_back, this));
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            DataCall call = getIntent().getParcelableExtra("dataCall");
            boolean isHaveSelect = getIntent().getBooleanExtra("isHaveSelect", false);
            currentLocation = getIntent().getExtras().getParcelable("location");
            FragmentCallDetail.newInstance(call, isHaveSelect, new FragmentCallDetail.InstanceCreatedCallback() {
                @Override
                public void onUpdate(FragmentCallDetail fragment) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
