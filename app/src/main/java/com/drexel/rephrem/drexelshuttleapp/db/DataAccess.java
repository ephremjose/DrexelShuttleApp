package com.drexel.rephrem.drexelshuttleapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.drexel.rephrem.drexelshuttleapp.vo.StopTimeVO;

import java.util.ArrayList;

/*
 * Author 		: Renjith J Ephrem
 * Email  		: rje49@drexel.edu
 * Subject		: CS530 - Final Project.
 * Instructor 	: Dr. Erin Solovey
 * Layout XML   : N/A
 *
 * This class contains all the methods that is used to interact with the SQLite database.
 *
 */

public class DataAccess extends ActionBarActivity {

    DataBaseHelperClass dbHelper = new DataBaseHelperClass(this);

    public DataAccess() {

    }

    /**
     * getCurrentTime is used to get the current local time, which is used to filter out the future bus timings.
     *
     * @return
     */
    public String getCurrentTime(){

        SQLiteDatabase sqlDB = null;

        Log.d("", " Reached getCurrentTime() in DataAccess. ");
        String currentTime = null;

        if(sqlDB == null || !sqlDB.isOpen() ) {
            Log.d("", " Opening DB as db class is null. ");
            sqlDB = dbHelper.openDataBase();
        }

        try{

            String selectCurrentTimeQ = " SELECT STRFTIME('%H','NOW','LOCALTIME') AS HOUR, STRFTIME('%M','NOW','LOCALTIME') AS MINUTE ";
            Log.d("", " Executing selectCurrentTimeQ " + selectCurrentTimeQ);

            Cursor cur          = sqlDB.rawQuery(selectCurrentTimeQ,null);
            if(cur.moveToFirst())
            {
                String currentHour     = (cur.getString(0));
                String currentMinute   = (cur.getString(1));

                currentTime         = currentHour + " " + currentMinute + " hrs";
                Log.d("", " Current time :  " + currentHour + " " + currentMinute + " hrs");
            }

        }catch(Exception ex)
        {
            ex.printStackTrace();
            Log.d("", " Exception in  getCurrentTime(). " + ex.getMessage());
        }
        finally {
            sqlDB.close();
        }

        Log.d("", " Exiting getCurrentTime() in DataAccess. ");

        return currentTime;
    }

    /**
     * getListOfStops is used to fetch all the stops corresponding to a bus route.
     *
     * @param routeName
     * @return
     */
    public String[] getListOfStops( String routeName ){

        Log.d("", " Reached getListOfStops() for route : " + routeName) ;

        SQLiteDatabase sqlDB    = null;
        String[] stopNameArr    = null;
        String whereClause      = "";
        String selectQuery      = "";
        String stopName         = "";
        int count               = 0;

        if(sqlDB == null || !sqlDB.isOpen() ) {
            Log.d("", " Opening DB as db class is null. ");
            sqlDB = dbHelper.openDataBase();
            //openDataBase();
        }

        try{

            if(routeName!=null && routeName.length() > 0)
            {
                if("blue".equalsIgnoreCase(routeName))
                    whereClause += " WHERE STOP_ID LIKE 'BG%' ";

                if("dragon".equalsIgnoreCase(routeName))
                    whereClause += " WHERE STOP_ID LIKE 'DR%' ";

                if("queen".equalsIgnoreCase(routeName))
                    whereClause += " WHERE STOP_ID LIKE 'QL%' ";
            }

            Log.d("", " Generated where clause : " + whereClause ) ;

            selectQuery += " SELECT STOP_NAME FROM STOPS_TABLE ";
            selectQuery += whereClause;
            selectQuery += " ORDER BY STOP_ID ";

            Log.d("", " Executing query : " + selectQuery ) ;

            Cursor cursor   = sqlDB.rawQuery(selectQuery, null);
            count           = cursor.getCount();

            Log.d("", " Number of Stop Names retreived = " + count ) ;
            int index = 0;

            if(count>0){
                stopNameArr = new String[count];

                Log.d("", " Fetched STOP_NAME. ");

                if(cursor.moveToFirst()){
                    do{

                        stopName = cursor.getString(0);

                        stopNameArr[index] = stopName.toUpperCase();
                        index++;

                    }while (cursor.moveToNext());
                }

            }
            else
            {
                Log.d("", " in ELSE loop ");
            }

        }catch(Exception ex)
        {
            ex.printStackTrace();
            Log.d("", " Exception in select query. . " + ex.getMessage());
        }
        finally {
            sqlDB.close();
        }

        Log.d("", " Exiting getListOfStops(). ");

        return stopNameArr;
    }


    /**
     * getFutureTimingsForStop is used to get all the future shuttle timings for a bus stop.
     * It first fetches all the timings for the stop and filters out the shuttle services that
     * have already passed through the stop in that day.
     *
     * @param stopName
     * @return
     */
    public ArrayList<StopTimeVO> getFutureTimingsForStop( String stopName ){

        Log.d("", " Reached getFutureTimingsForStop() for route : " + stopName) ;

        SQLiteDatabase sqlDB            = null;
        String[] timingsArr             = null;
        ArrayList<StopTimeVO> tempList  = new ArrayList<StopTimeVO>();
        ArrayList<StopTimeVO> finalList = new ArrayList<StopTimeVO>();
        String whereClause              = "";
        String selectQuery              = "";
        String selectCurrentTimeQ       = "";
        int currentHour                 = 0;
        int currentMinute               = 0;
        int count                       = 0;

        if(sqlDB == null || !sqlDB.isOpen() ) {
            Log.d("", " Opening DB as db class is null. ");
            sqlDB = dbHelper.openDataBase();
            //openDataBase();
        }

        try{

            whereClause += " WHERE STOP_ID = ( SELECT STOP_ID FROM STOPS_TABLE WHERE STOP_NAME = '" + stopName + "') ";
            Log.d("", " Generated where clause : " + whereClause ) ;

            selectQuery += " SELECT _ID, STOP_ID, FROM_DREXEL_TIME FROM STOP_TIMINGS_TABLE ";
            selectQuery += whereClause;
            selectQuery += " ORDER BY _ID ";

            Log.d("", " Executing query : " + selectQuery ) ;

            Cursor cursor   = sqlDB.rawQuery(selectQuery, null);
            count           = cursor.getCount();

            Log.d("", " Number of TIMINGS retreived = " + count ) ;
            int index = 0;
            int id = 0;
            String temp ="";
            if(count>0){

                StopTimeVO stopTime = null;

                Log.d("", " Fetched STOP TIME DETAILS. ");

                if(cursor.moveToFirst()){
                    do{

                        stopTime = new StopTimeVO();

                        id = cursor.getInt(0);

                        stopTime.set_id(id);
                        stopTime.setStopID(cursor.getString(1));
                        //stopTime.setFromDrexelFlag(cursor.getString(2));

                        temp = cursor.getString(2);
                        stopTime.setFromDrexelTime(temp);
                        //stopTime.setToDrexelTime(cursor.getString(4));

                        tempList.add(stopTime);

                       // Log.d("", " From Drexel Time for : " + id + " - " + temp + " length : " + temp.length());


                        index++;

                    }while (cursor.moveToNext());
                }

            }
            else
            {
                Log.d("", " in ELSE loop ");
                // insertUserNameFromDB();
            }

            selectCurrentTimeQ += " SELECT STRFTIME('%H','NOW','LOCALTIME') AS HOUR, STRFTIME('%M','NOW','LOCALTIME') AS MINUTE ";
            Log.d("", " Executing selectCurrentTimeQ " + selectCurrentTimeQ);

            Cursor cur          = sqlDB.rawQuery(selectCurrentTimeQ,null);
            if(cur.moveToFirst())
            {
                currentHour      = Integer.parseInt(cur.getString(0));
                currentMinute    = Integer.parseInt(cur.getString(1));

                Log.d("", " Current time :  " + currentHour + " " + currentMinute + " hrs");
            }

            String fromDrexelTime = "";
            String[] tempTimeArr = new String[2];
            int busTimeHour     = 0;
            int busTimeMinute   = 0;
            int finalIndex      = 0;
            for(index = 0; index < tempList.size() ; index++)
            {
                fromDrexelTime  = tempList.get(index).getFromDrexelTime();
                tempTimeArr     = fromDrexelTime.split(" ");
                busTimeHour     = Integer.parseInt(tempTimeArr[0]);
                busTimeMinute   = Integer.parseInt(tempTimeArr[1]);

                tempList.get(index).setFromDrexelTime(tempTimeArr[0] + ":" + tempTimeArr[1]);


                if(busTimeHour >= currentHour)
                {
                    if(busTimeHour > currentHour) {
                        finalList.add(tempList.get(index));
                        finalIndex++;
                        Log.d("", " Added --> Hours  " + busTimeHour + " Minutes : " + busTimeMinute );
                    }
                    if(busTimeHour == currentHour && busTimeMinute > currentMinute) {
                        finalList.add(tempList.get(index));
                        finalIndex++;
                        Log.d("", " Added --> Hours  " + busTimeHour + " Minutes : " + busTimeMinute );
                    }

                }

                if(busTimeHour == 0){

                    finalList.add(tempList.get(index));
                    finalIndex++;
                }

            }

        }catch(Exception ex)
        {
            ex.printStackTrace();
            Log.d("", " Exception in  select query. . " + ex.getMessage());
        }
        finally {
            sqlDB.close();
        }

        Log.d("", " Exiting getListOfStops(). ");

        return finalList;
    }


    /**
     * getCompleteTimingsForStop is used to get all the shuttle timings for a particular stop.
     *
     * @param stopName
     * @return
     */
    public ArrayList<StopTimeVO> getCompleteTimingsForStop( String stopName ){

        Log.d("", " Reached getFutureTimingsForStop() for route : " + stopName) ;

        SQLiteDatabase sqlDB            = null;
        ArrayList<StopTimeVO> finalList = new ArrayList<StopTimeVO>();
        String whereClause              = "";
        String selectQuery              = "";
        int count                       = 0;

        if(sqlDB == null || !sqlDB.isOpen() ) {
            Log.d("", " Opening DB as db class is null. ");
            sqlDB = dbHelper.openDataBase();
        }

        try{

            whereClause += " WHERE STOP_ID = ( SELECT STOP_ID FROM STOPS_TABLE WHERE STOP_NAME = '" + stopName + "') ";
            Log.d("", " Generated where clause : " + whereClause ) ;

            selectQuery += " SELECT _ID, STOP_ID, FROM_DREXEL_TIME FROM STOP_TIMINGS_TABLE ";
            selectQuery += whereClause;
            selectQuery += " ORDER BY _ID ";

            Log.d("", " Executing query : " + selectQuery ) ;

            Cursor cursor   = sqlDB.rawQuery(selectQuery, null);
            count           = cursor.getCount();

            Log.d("", " Number of TIMINGS retreived = " + count ) ;

            String temp ="";
            if(count>0){

                StopTimeVO stopTime = null;

                Log.d("", " Fetched STOP TIME DETAILS. ");

                if(cursor.moveToFirst()){
                    do{

                        stopTime = new StopTimeVO();

                        stopTime.set_id(cursor.getInt(0));
                        stopTime.setStopID(cursor.getString(1));
                        stopTime.setFromDrexelTime(cursor.getString(2));

                        finalList.add(stopTime);

                    }while (cursor.moveToNext());
                }

            }
            else
            {
                Log.d("", " in ELSE loop ");
            }

        }catch(Exception ex)
        {
            ex.printStackTrace();
            Log.d("", " Exception in  select query. . " + ex.getMessage());
        }
        finally {
            sqlDB.close();
        }

        Log.d("", " Exiting getListOfStops(). ");

        return finalList;
    }

    /**
     * setFavoriteStop uses deleteFlag to check if the operation requires to do an insert.
     * IF the deleteFlag is false, then an insert is performed.
     * Else, the existing fav stop is deleted.
     *
     * @param route
     * @param stopSelected
     * @param deleteFlag
     * @return
     */
    public boolean setFavoriteStop(String route, String stopSelected, Boolean deleteFlag){

        Log.d("", " Reached setFavoriteStop in DBAccess for route : " + route + " and stopSelected : " + stopSelected );
        boolean favSet = false;
        String dropRecordsFromFav   = "";
        String insertIntoFac        = "";

        SQLiteDatabase sqlDB = null;

        if( sqlDB == null || !sqlDB.isOpen() )
            sqlDB = dbHelper.openDataBase();

        try{

            Log.d("", " Deleting existing stop from FAVORITE_STOP_TABLE if any. " + deleteFlag );
            dropRecordsFromFav  += " DELETE FROM FAVORITE_STOP_TABLE ";
            sqlDB.execSQL(dropRecordsFromFav);
            Log.d("", " Existing stop from FAVORITE_STOP_TABLE deleted. " );

            String likeSQL = "";

            if(route != null && route.length()>0)
            {
                if("blue".equalsIgnoreCase(route))
                    likeSQL += " AND STOP_ID LIKE 'BG%' ";

                if("dragon".equalsIgnoreCase(route))
                    likeSQL += " AND STOP_ID LIKE 'DR%' ";

                if("queen".equalsIgnoreCase(route))
                    likeSQL += " AND STOP_ID LIKE 'QL%' ";
            }

            if(!deleteFlag) {
                insertIntoFac += " INSERT INTO FAVORITE_STOP_TABLE SELECT 1, STOP_ID FROM STOPS_TABLE WHERE STOP_NAME = '" + stopSelected + "' ";
                insertIntoFac += likeSQL;
                Log.d("", " inserting  " + insertIntoFac);

                sqlDB.execSQL(insertIntoFac);

                Log.d("", " Favorite Stop Set ");
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
            Log.d("", " Exception in  select query. . " + ex.getMessage());
            return false;
        }

        finally
        {
            dbHelper.close();
        }

        return true;
    }

    /**
     * isStopFavorite method checks whether the selected stop is already set a favorite.
     * It is used to make the star icon appear illuminated while loading the time for any stop
     * to indicate that it is already set a fav stop.
     *
     * @param route
     * @param stopSelected
     * @return
     */
    public boolean isStopFavorite( String route, String stopSelected ){

        SQLiteDatabase sqlDB = null;

        Log.d("", " Reached isStopFavorite in DBAccess. ");
        boolean isFav   = false;
        int count       = 0;

        if(sqlDB == null || !sqlDB.isOpen() ) {
            Log.d("", " Opening DB as db class is null. ");
            sqlDB = dbHelper.openDataBase();
        }

        try{

            String selectFavStopQuery = " SELECT COUNT(*) FROM FAVORITE_STOP_TABLE WHERE STOP_ID = ( SELECT STOP_ID FROM STOPS_TABLE WHERE STOP_NAME = '" + stopSelected + "' ";

            String likeSQL = "";

            if(route != null && route.length()>0)
            {
                if("blue".equalsIgnoreCase(route))
                    likeSQL += " AND STOP_ID LIKE 'BG%' ";

                if("dragon".equalsIgnoreCase(route))
                    likeSQL += " AND STOP_ID LIKE 'DR%' ";

                if("queen".equalsIgnoreCase(route))
                    likeSQL += " AND STOP_ID LIKE 'QL%' ";
            }

            selectFavStopQuery += likeSQL;
            selectFavStopQuery += " ) ";

            Log.d("", " Executing selectFavStopQuery " + selectFavStopQuery);

            Cursor cur          = sqlDB.rawQuery(selectFavStopQuery,null);
            if(cur.moveToFirst())
            {
                count = cur.getInt(0);

                Log.d("", " Favorite Stop Count :  " + count );
            }

            isFav = ( count == 0 )? false : true;

            Log.d("", " Favorite Stop? :  " + isFav );

        }catch(Exception ex)
        {
            ex.printStackTrace();
            Log.d("", " Exception in  getCurrentTime(). " + ex.getMessage());
        }
        finally {
            sqlDB.close();
        }
        return isFav;
    }


    /**
     * This method is used to fetch the current record that is present in the FAVORITE_STOP table.
     *
     * @return
     */
    public String[] getFavoriteStop( ){

        SQLiteDatabase sqlDB = null;

        Log.d("", " Reached getFavoriteStop in DBAccess. ");
        String[] parameters = new String[2];
        String favStop      = "";
        String stopID       = "";
        String route        = "";

        if(sqlDB == null || !sqlDB.isOpen() ) {
            Log.d("", " Opening DB as db class is null. ");
            sqlDB = dbHelper.openDataBase();
        }

        try{

            String selectFavStopQuery = " SELECT STOP_NAME, STOP_ID FROM STOPS_TABLE WHERE STOP_ID = (SELECT STOP_ID FROM FAVORITE_STOP_TABLE) ";
            Log.d("", " Executing selectFavStopQuery " + selectFavStopQuery);

            Cursor cur          = sqlDB.rawQuery(selectFavStopQuery,null);
            if(cur.moveToFirst())
            {
                favStop = cur.getString(0);
                stopID  = cur.getString(1);
            }

            if(stopID != null && stopID.indexOf("BG")>-1)
            {
                route = "blue";
            }
            if(stopID != null && stopID.indexOf("DR")>-1)
            {
                route = "dragon";
            }
            if(stopID != null && stopID.indexOf("QL")>-1)
            {
                route = "queen";
            }

            parameters[0] = route;
            parameters[1] = favStop;

            String temp = (favStop!=null && favStop.length()>0) ?favStop:"Not Set";

            Log.d("", " Favorite Stop : " + temp );

        }catch(Exception ex)
        {
            ex.printStackTrace();
            Log.d("", " Exception in  getCurrentTime(). " + ex.getMessage());
        }
        finally {
            sqlDB.close();
        }

        Log.d("", " Exiting getFavoriteStop in DBAccess. ");

        return parameters;
    }




}





