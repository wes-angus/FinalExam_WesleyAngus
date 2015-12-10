package ca.uoit.csci4100.samples.locationsample;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Wesley Angus on 12/8/2015.
 */
public class DownloadDataTask extends AsyncTask<String, Void, List<Bike>>
{
    private  DataDownloadedListener listener = null;
    private Exception exception = null;
    private Context context = null;
    private BikeDBHelper bikeDBHelper;

    public DownloadDataTask(Context context, DataDownloadedListener listener)
    {
        this.context = context;
        this.listener = listener;
        bikeDBHelper = new BikeDBHelper(context);
    }

    @Override
    protected List<Bike> doInBackground(String... params)
    {
        //List of bikes downloaded from the feed
        List<Bike> bikeData = new ArrayList<>();
        //Download list of new bike releases in background
        try
        {
            //Erase data from previous session
            bikeDBHelper.deleteAllBikes();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            URL url = new URL(params[0]);
            Document document = builder.parse(url.openStream());

            NodeList bikes = document.getElementsByTagName("station");
            //Loops through every entry (bike) in the feed
            for (int i = 0; i < bikes.getLength(); i++)
            {
                Node bikeNode = bikes.item(i);
                //Parse each bike in the NodeList
                if(bikeNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element bikeElement = (Element)bikeNode;
                    NodeList idElements = bikeElement.getElementsByTagName("id");
                    int bikeShareId = Integer.parseInt(idElements.item(0).getTextContent());
                    NodeList nameElements = bikeElement.getElementsByTagName("name");
                    String name = nameElements.item(0).getTextContent();
                    NodeList latElements = bikeElement.getElementsByTagName("lat");
                    double latitude = Double.parseDouble(latElements.item(0).getTextContent());
                    NodeList longElements = bikeElement.getElementsByTagName("long");
                    double longitude = Double.parseDouble(longElements.item(0).getTextContent());
                    NodeList nbBikesElements = bikeElement.getElementsByTagName("nbBikes");
                    int numBikes = Integer.parseInt(nbBikesElements.item(0).getTextContent());
                    String address = "";
                    Bike downloadedBike = new Bike(bikeShareId, latitude, longitude, name, numBikes, address);
                    address = getGeocodedBikeAddress(downloadedBike);

                    Bike addedBike = bikeDBHelper.createBike(bikeShareId, latitude, longitude, name, numBikes, address);
                    bikeData.add(addedBike);
                }
            }
        }
        catch (Exception e)
        {
            exception = e;
        }
        return bikeData;
    }

    private String getGeocodedBikeAddress(Bike bike)
    {
        Log.d("LocationSample", "getGeocodedBikeAddress("+ bike.getName() +")");

        // perform a reverse geocode to get the address
        if (Geocoder.isPresent())
        {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            try
            {
                // reverse geocode from current GPS position
                List<Address> results = geocoder.getFromLocation(bike.getLatitude(),
                        bike.getLongitude(), 1);

                if (results.size() > 0)
                {
                    Address match = results.get(0);
                    String address = match.getAddressLine(0);
                    bike.setAddress(address);
                    return address;
                }
                else
                {
                    Log.d("LocationSample", "No results found while reverse geocoding GPS location for bike station: "
                            + bike.getName());
                    return "";
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return "";
            }
        }
        else
        {
            Log.d("LocationSample", "No geocoder present");
            return "";
        }
    }

    public Exception getException()
    {
        return this.exception;
    }

    @Override
    protected void onPostExecute(List<Bike> result)
    {
        if(this.exception != null)
        {
            exception.printStackTrace();
        }

        //Sends the list of games from the feed to add to the database
        this.listener.dataDownloaded(result);
    }
}
