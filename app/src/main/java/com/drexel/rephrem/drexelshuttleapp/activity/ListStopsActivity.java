package com.drexel.rephrem.drexelshuttleapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.drexel.rephrem.drexelshuttleapp.R;
import com.drexel.rephrem.drexelshuttleapp.db.DataAccess;


/*
 * Author 		: Renjith J Ephrem
 * Email  		: rje49@drexel.edu
 * Subject		: CS530 - Final Project.
 * Instructor 	: Dr. Erin Solovey
 * Layout XML   : activity_list_stops.xml
 *
 * This Activity class is used to list all the stops when a bus route is selected from the home screen.
 *
 */

public class ListStopsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();

        final String route = intent.getStringExtra(MainActivity.Extra_Msg);
        Log.d(""," Route Selected : " + route);

        View view                   = (View) getLayoutInflater().inflate(R.layout.activity_list_stops, null);
        Button topButton            = (Button) view.findViewById(R.id.listStopLayout_button);
        final ListView stopsView    = (ListView) view.findViewById(R.id.listStopLayout_ListView);

        Log.d(""," Calling DataAccess class : " + stopsView );

        DataAccess dAccess          = new DataAccess();
        String[] blueStopsArray     = dAccess.getListOfStops(route);

        Log.d(""," After Calling DataAccess class : ");

        //Casting the received blueStopsArray into the ListAdapter.
        ListAdapter stopAdapter     = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, blueStopsArray );

        //Using an if-else-if ladder to decide the layout colour of the screen at time of loading bases on the 'route' variable.
        //Each route has its corresponding border_ui xml.
        if(route != null && "blue".equalsIgnoreCase(route)) {
            topButton.setBackgroundResource(android.R.color.holo_blue_dark);
            topButton.setText("BLUE & GOLD ROUTE");
            stopsView.setBackgroundResource(R.drawable.border_ui);
        }
        else if(route != null && "dragon".equalsIgnoreCase(route)){
            topButton.setBackgroundResource(android.R.color.holo_orange_dark);
            topButton.setText("DRAGON ROUTE");
            stopsView.setBackgroundResource(R.drawable.border_ui_orange);
        }
        else if(route != null && "queen".equalsIgnoreCase(route)){
            topButton.setBackgroundResource(android.R.color.holo_green_dark);
            topButton.setText("QUEENS LANE");
            stopsView.setBackgroundResource(R.drawable.border_ui_green);
        }

        stopsView.setAdapter(stopAdapter);

        stopsView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String stopPicked = String.valueOf(stopsView.getItemAtPosition(position));

                Log.d(""," Calling method to fetch timings. ");
                showTimingsForStop(route,stopPicked);
            }
        });
        setContentView(view);
    }


    /**
     * showTimingsForStop method is used to call the ListStopsTimeActivity
     * with the route and selected stop as attributes.
     *
     * @param route
     * @param stopPicked
     */
    public void showTimingsForStop ( String route , String stopPicked )
    {

        Log.d(""," Reached showTimingsForStop(). ");

        String[] parameters = new String[2];
        parameters[0] = route;
        parameters[1] = stopPicked;

        Intent intent = new Intent ( this, ListStopsTimeActivity.class);
        intent.putExtra(MainActivity.Extra_Msg, parameters);

        startActivity(intent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_stops, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
