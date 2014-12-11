package com.drexel.rephrem.drexelshuttleapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.drexel.rephrem.drexelshuttleapp.R;
import com.drexel.rephrem.drexelshuttleapp.db.DataAccess;
import com.drexel.rephrem.drexelshuttleapp.vo.StopTimeVO;

import java.util.ArrayList;

/*
 * Author 		: Renjith J Ephrem
 * Email  		: rje49@drexel.edu
 * Subject		: CS530 - Final Project.
 * Instructor 	: Dr. Erin Solovey
 * Layout XML   : activity_list_complete_stop_times.xml
 *
 * This Activity class is used to list the complete timings for the stop that is selected in ListStopsTimeActivity.
 *
 */


public class ListCompleteStopTimesActivity extends Activity {

    String[] parameters = new String[2];
    String stopSelected = "";
    String route        = "";
    DataAccess dAccess  = new DataAccess();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent   = getIntent();
        parameters      = intent.getStringArrayExtra(MainActivity.Extra_Msg);
        route           = parameters[0];
        stopSelected    = parameters[1];
        Log.d("", " ListCompleteStopTimesActivity - stopSelected : " + stopSelected + " for route : " + route.toUpperCase() );

        View view                   = (View) getLayoutInflater().inflate(R.layout.activity_list_complete_stop_times, null);
        String[] timeArray          = getCompleteTimingsForStop(stopSelected);
        final boolean isStopFav     = dAccess.isStopFavorite(route, stopSelected);
        final ImageButton favButton = (ImageButton) view.findViewById(R.id.completeStopTimeScreen_favoriteButton);
        Button topButton            = (Button) view.findViewById(R.id.completeStopTimeScreen_button);
        ListView stopTimingView     = (ListView) view.findViewById(R.id.completeStopTimeScreen_ListView);

        if(isStopFav)
            favButton.setImageResource(R.drawable.button_pressed);

        Log.d("", " Extracted timeArray. Setting it into ListView. ");
        ListAdapter stopAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, timeArray );
        stopTimingView.setAdapter(stopAdapter);

        //Using an if-else-if ladder to decide the layout colour of the screen at time of loading bases on the 'route' variable.
        //Each route has its corresponding border_ui xml.
        if(route != null && "blue".equalsIgnoreCase(route)) {
            favButton.setBackgroundResource(android.R.color.holo_blue_dark);
            topButton.setBackgroundResource(android.R.color.holo_blue_dark);
            stopTimingView.setBackgroundResource(R.drawable.border_ui);
        }
        else if(route != null && "dragon".equalsIgnoreCase(route)){
            favButton.setBackgroundResource(android.R.color.holo_orange_dark);
            topButton.setBackgroundResource(android.R.color.holo_orange_dark);
            stopTimingView.setBackgroundResource(R.drawable.border_ui_orange);
        }
        else if(route != null && "queen".equalsIgnoreCase(route)){
            favButton.setBackgroundResource(android.R.color.holo_green_dark);
            topButton.setBackgroundResource(android.R.color.holo_green_dark);
            stopTimingView.setBackgroundResource(R.drawable.border_ui_green);
        }

        topButton.setText(stopSelected + "  |  COMPLETE LIST " );

        favButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(isStopFav) {
                    favButton.setImageResource(R.drawable.button_normal);
                    setFavoriteStop( stopSelected , true );
                    Toast.makeText(ListCompleteStopTimesActivity.this, "Favorite Stop Deleted!", Toast.LENGTH_LONG).show();
                }
                else {
                    favButton.setImageResource(R.drawable.button_pressed);
                    setFavoriteStop( stopSelected , false );
                    Toast.makeText(ListCompleteStopTimesActivity.this, "Favorite Stop Set!", Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });

        setContentView(view);

    }

    /**
     * getCompleteTimingsForStop uses dAccess object to fetch the complete list of shuttle timings
     * for the selected stop.
     *
     * @param stopSelected
     * @return
     */
    private String[] getCompleteTimingsForStop(String stopSelected) {

        Log.d("", " Calling getCompleteTimingsForStop from ListCompleteStopTimesActivity for : " + stopSelected);

        ArrayList<StopTimeVO> stopTimeVO = new ArrayList<StopTimeVO>();
        stopTimeVO = dAccess.getCompleteTimingsForStop(stopSelected);

        Log.d("", " Retreived stopTimeVO with size : " + stopTimeVO.size());
        String[] timeArray = new String[stopTimeVO.size()];

        for(int i = 0; i < stopTimeVO.size(); i++) {

            String fromDrexelTime = "";

            if(i==0)
                fromDrexelTime = " First Bus At :                                     ";
            else if(i == 1)
                fromDrexelTime = " More Bus Timings :                         ";

            else
                fromDrexelTime = "                                                              ";

            fromDrexelTime  += stopTimeVO.get(i).getFromDrexelTime();
            timeArray[i]     = fromDrexelTime + " hrs.";

        }
        Log.d("", " Exiting ListCompleteStopTimesActivity.getFutureTimingsForStop(). " );

        return timeArray;
    }

    /**
     * setFavoriteStop uses dAccess object to insert the selected stop as favorite stop.
     *
     * @param stopSelected
     * @param deleteFlag --> used to identify whether it is an operation to remove the fav stop or to set it.
     * @return
     */
    public boolean setFavoriteStop( String stopSelected , Boolean deleteFlag)
    {

        Log.d("", " Calling setFavoriteStop from ListCompleteStopTimesActivity for : " + stopSelected);

        boolean insertSuccess = false;

        insertSuccess = dAccess.setFavoriteStop(route, stopSelected, deleteFlag);
        Log.d("", " Set Favorite Stop : " + insertSuccess + " Exiting setFavoriteStop from ListCompleteStopTimesActivity. ") ;

        return insertSuccess;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_complete_stop_times, menu);
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
