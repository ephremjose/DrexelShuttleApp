package com.drexel.rephrem.drexelshuttleapp.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.drexel.rephrem.drexelshuttleapp.R;
import com.drexel.rephrem.drexelshuttleapp.db.DataAccess;
import com.drexel.rephrem.drexelshuttleapp.db.DataBaseHelperClass;

/*
 * Author 		: Renjith J Ephrem
 * Email  		: rje49@drexel.edu
 * Subject		: CS530 - Final Project.
 * Instructor 	: Dr. Erin Solovey
 * Layout XML   : activity_main.xml
 *
 * This Activity class corresponds to the home screen of the Drexel Shuttle App.
 * All the four buttons on the screen have associated methods that start the next corresponding activity.
 *
 */

public class MainActivity extends ActionBarActivity {

    public final static String Extra_Msg = "com.drexel.rephrem.drexelshuttleapp.MESSAGE";
    DataAccess dAccess  = new DataAccess();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.d("", " Calling DataBaseHelperClass to create the database. " );
        //DataBaseHelperClass is called to create the database when the applications is loaded.
        DataBaseHelperClass dbHelper = new DataBaseHelperClass(this);

        try {
            dbHelper.createDataBase();
        }
        catch (Exception e)
        {
            Log.d("", " Exception while creating DB. " + e.getMessage() );
        }

        setContentView(R.layout.activity_main);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

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


    /**
     * Method onClickBlue corresponds to Blue & Gold Route.
     * The ListStopsActivity is triggered with "blue" as the parameter.
     *
     * @param view
     */
    public void onClickBlue ( View view )
    {
        Intent intent = new Intent ( this, ListStopsActivity.class);

        intent.putExtra(Extra_Msg, "blue");
        startActivity(intent);

    }

    /**
     * Method onClickDragon corresponds to Dragon Route.
     * The ListStopsActivity is triggered with "dragon" as the parameter.
     *
     * @param view
     */
    public void onClickDragon ( View view )
    {
        Intent intent = new Intent ( this, ListStopsActivity.class);

        intent.putExtra(Extra_Msg, "dragon");
        startActivity(intent);

    }

    /**
     * Method onClickQueen corresponds to Queen's Lane Route.
     * The ListStopsActivity is triggered with "queen" as the parameter.
     *
     * @param view
     */
    public void onClickQueen ( View view )
    {
        Intent intent = new Intent ( this, ListStopsActivity.class);

        intent.putExtra(Extra_Msg, "queen");
        startActivity(intent);

    }


    /**
     * Method showFavorite corresponds to Favorite Button.
     * If a favorite stop is already set, it calls the ListStopsTimeActivity.
     * Else, the ShowFavoriteActivity with the instructions to set Favorite Stop is called.
     */

    public void showFavorite(View view) {

        String[] parameters = dAccess.getFavoriteStop();
        String route    = parameters[0];
        String favStop  = parameters[1];
        Log.d("", " Favorite Stop after calling getFavoriteStop : " + favStop + " Route : " + route );

        Intent intent = null;

        if(favStop != null && favStop.length()>0){
            intent = new Intent(this, ListStopsTimeActivity.class);
            intent.putExtra(Extra_Msg, parameters);
        }
        else {
            intent = new Intent(this, ShowFavoriteActivity.class);
            intent.putExtra(Extra_Msg, "");
        }

        startActivity(intent);
    }
}
