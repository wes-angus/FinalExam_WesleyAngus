package ca.uoit.csci4100.samples.locationsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements DataDownloadedListener
{
    private List<Bike> bikes;
    List<CharSequence> bikeStrings;
    private Spinner spinner;
    private BikeDBHelper dbHelper;
    private ArrayAdapter<CharSequence> spinnerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bikes = new ArrayList<>();
        bikeStrings  = new ArrayList<>();
        dbHelper = new BikeDBHelper(getApplicationContext());

        //Parses the new game releases feed in an AsyncTask
        String url = getString(R.string.data_url);
        DownloadDataTask task = new DownloadDataTask(getApplicationContext(), this);
        task.execute(url);
    }

    @Override
    public void dataDownloaded(List<Bike> bikes)
    {
        this.bikes = new ArrayList<>(bikes);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinnerAdapter = populateSpinner(spinner);

        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        Button mapBtn = (Button) findViewById(R.id.showMapBtn);
        saveBtn.setEnabled(true);
        mapBtn.setEnabled(true);
    }

    private ArrayAdapter<CharSequence> populateSpinner(Spinner spinner)
    {
        for (Bike bike : bikes)
        {
            bikeStrings.add(bike.toString());
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bikeStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showBikeInfo(bikes.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return adapter;
    }

    private void showBikeInfo(Bike bike)
    {
        if(bike != null)
        {
            Log.d("LocationSample", "setLocation(" + bike.getName() + ")");

            EditText nameField = (EditText) findViewById(R.id.nameField);
            nameField.setText(bike.getName());

            EditText idField = (EditText) findViewById(R.id.idField);
            idField.setText(Integer.toString(bike.getBikeShareId()));

            EditText addrField = (EditText) findViewById(R.id.addressField);
            addrField.setText(bike.getAddress());

            EditText latField = (EditText) findViewById(R.id.latField);
            latField.setText(Double.toString(bike.getLatitude()));

            EditText longField = (EditText) findViewById(R.id.longField);
            longField.setText(Double.toString(bike.getLongitude()));

            EditText numBikesField = (EditText) findViewById(R.id.numBikesField);
            numBikesField.setText(Integer.toString(bike.getNumBikes()));
        }
    }

    public void search(View v)
    {
        Intent showMapIntent = new Intent(MainActivity.this, ShowMapActivity.class);

        showMapIntent.putExtra("latitude", 43.6563589);
        showMapIntent.putExtra("longitude", -79.3909977);
        startActivity(showMapIntent);
    }

    public void saveBikeName(View view)
    {
        int spinner_i = spinner.getSelectedItemPosition();
        EditText nameField = (EditText) findViewById(R.id.nameField);
        bikes.get(spinner_i).setName(nameField.getText().toString());
        bikeStrings.set(spinner_i, bikes.get(spinner_i).toString());
        dbHelper.updateBike(bikes.get(spinner_i));
        spinnerAdapter.notifyDataSetChanged();
    }
}
