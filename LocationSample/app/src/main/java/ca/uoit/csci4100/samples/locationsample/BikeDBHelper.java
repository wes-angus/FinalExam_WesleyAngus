package ca.uoit.csci4100.samples.locationsample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley Angus on 12/8/2015.
 */
public class BikeDBHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_FILENAME = "bikes.db";
    public static final String TABLE_NAME = "Bikes";

    String[] columns = new String[] { "_id", "bikeShareId", "latitude", "longitude", "name", "numBikes", "address" };

    public static final String CREATE_STMT = "CREATE TABLE Bikes (" +
            "_id integer primary key autoincrement," +
            " bikeShareId int not null," +
            " latitude double not null," +
            " longitude double not null," +
            " name text not null," +
            " numBikes int not null," +
            " address text null)";
    public static final String DROP_STMT = "DROP TABLE " + TABLE_NAME;

    private Context context;

    public BikeDBHelper(Context context)
    {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(CREATE_STMT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        //Should be changed if database will need to be upgraded
        database.execSQL(DROP_STMT);
        database.execSQL(CREATE_STMT);
    }

    public Bike createBike(int bikeShareId, double latitude, double longitude,
                           String name, int numBikes, String address)
    {
        // create the object
        Bike bike = new Bike(bikeShareId, latitude, longitude, name, numBikes, address);

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // insert the data into the database
        ContentValues values = new ContentValues();
        values.put(columns[1], bike.getBikeShareId());
        values.put(columns[2], bike.getLatitude());
        values.put(columns[3], bike.getLongitude());
        values.put(columns[4], bike.getName());
        values.put(columns[5], bike.getNumBikes());
        values.put(columns[6], bike.getAddress());
        long id = database.insert(TABLE_NAME, null, values);

        // assign the Id of the new database row as the Id of the object
        bike.setId(id);

        return bike;
    }

    public List<Bike> getAllBikes() {
        List<Bike> bikes = new ArrayList<>();

        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // retrieve the bike from the database
        Cursor cursor = database.query(TABLE_NAME, columns, "", new String[]{}, "", "", "");
        cursor.moveToFirst();
        do {
            // collect the bike data, and place it into a bike object
            long id = cursor.getLong(0);
            int bikeShareId = cursor.getInt(1);
            Double latitude = cursor.getDouble(2);
            Double longitude = cursor.getDouble(3);
            String name = cursor.getString(4);
            int numBikes = cursor.getInt(5);
            String address = cursor.getString(6);
            Bike bike = new Bike(bikeShareId, latitude, longitude, name, numBikes, address);
            bike.setId(id);

            // add the current bike to the list
            bikes.add(bike);

            // advance to the next row in the results
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        cursor.close();
        Log.i("DatabaseAccess", "getAllBikes():  num: " + bikes.size());

        return bikes;
    }
    public boolean updateBike(Bike bike) {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // update the data in the database
        ContentValues values = new ContentValues();
        values.put(columns[1], bike.getBikeShareId());
        values.put(columns[2], bike.getLatitude());
        values.put(columns[3], bike.getLongitude());
        values.put(columns[4], bike.getName());
        values.put(columns[5], bike.getNumBikes());
        values.put(columns[6], bike.getAddress());
        int numRowsAffected = database.update(TABLE_NAME, values, "_id = ?", new String[] { "" + bike.getId() });

        Log.i("DatabaseAccess", "updateBike(" + bike + "):  numRowsAffected: " + numRowsAffected);

        // verify that the bike was updated successfully
        return (numRowsAffected == 1);
    }

    public void deleteAllBikes() {
        // obtain a database connection
        SQLiteDatabase database = this.getWritableDatabase();

        // delete the bike
        int numRowsAffected = database.delete(TABLE_NAME, "", new String[] {});

        Log.i("DatabaseAccess", "deleteAllBikes():  numRowsAffected: " + numRowsAffected);
    }
}
