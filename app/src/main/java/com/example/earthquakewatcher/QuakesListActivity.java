package com.example.earthquakewatcher;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.earthquakewatcher.Model.EarthQuake;
import com.example.earthquakewatcher.Util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuakesListActivity extends AppCompatActivity {
    private ArrayList<String> arrayList;
    private ListView listView;
    private RequestQueue queue;
    private ArrayAdapter arrayAdapter;

    private List<EarthQuake> quakeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quakes_list);

        quakeList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listview);

        queue = Volley.newRequestQueue(this);

        arrayList = new ArrayList<>();


        getAllQuakes(Constants.URL);
    }

    void getAllQuakes(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                EarthQuake earthQuake = new EarthQuake();
                try {

                    JSONArray jsonArray = response.getJSONArray("features");
                    for (int i = 0; i < Constants.LIMIT; i++) {

                        JSONObject properties = jsonArray.getJSONObject(i).getJSONObject("properties");

                        //let's get coordinates

                        JSONObject geometry = jsonArray.getJSONObject(i).getJSONObject("geometry");

                        //get coordinates array
                        JSONArray coordinates = geometry.getJSONArray("coordinates");

                        double lon = coordinates.getDouble(0);
                        double lat = coordinates.getDouble(1);
                        //  Log.d("Lat", properties.getString("place"));


                        //Setup EarthQuake Object
                        earthQuake.setPlace(properties.getString("place"));
                        earthQuake.setType(properties.getString("type"));
                        earthQuake.setTime(properties.getLong("time"));
                        earthQuake.setLon(lon);
                        earthQuake.setLat(lat);


                        arrayList.add(earthQuake.getPlace());



                    }

                    arrayAdapter = new ArrayAdapter<>(QuakesListActivity.this, android.R.layout.simple_list_item_1,
                            android.R.id.text1, arrayList);
                    listView.setAdapter(arrayAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(getApplicationContext(), "Clicked " + position, Toast.LENGTH_LONG).show();
                        }
                    });
                    arrayAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }





            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(jsonObjectRequest);

    }
}
