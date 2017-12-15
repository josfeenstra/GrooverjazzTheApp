package com.josfeenstra.grooverjazztheapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
public class DiscoverMusic extends AppCompatActivity {

    public final String APIPATH1 = "http://musicovery.com/api/V4/playlist.php?&fct=getfromtag&format=json&tag=";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabaseinst;
    String DATABASE_REF = "trackData";
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_music);

        // database
        mAuth = FirebaseAuth.getInstance();
        mDatabaseinst = FirebaseDatabase.getInstance();
        mDatabase = mDatabaseinst.getReference(DATABASE_REF);

        // assign buttons to listeners
        ImageButton buttonBack = (ImageButton) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new onBackClick());

        ImageButton buttonSearch = (ImageButton) findViewById(R.id.buttonSearchArtist);
        buttonSearch.setOnClickListener(new onSearchClick());


    }

    private class onSearchClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            // get artist name
            EditText artistNameView = (EditText) findViewById(R.id.editArtistname);
            String artistName = artistNameView.getText().toString();

            artistName = artistName.replaceAll( " ", "%20");

            if (artistName.isEmpty()) {
                popup("Please enter a name.");
            } else {
                // construct and send request
                String path = APIPATH1 + artistName;


                volleyRequest(path);


                popup("searching...");
            }


        }
    }

    // return to home
    private class onBackClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            // next intent
            Intent intent = new Intent(DiscoverMusic.this, Home.class);
            intent.putExtra("restore1", false);
            startActivity(intent);
            finish();
        }
    }

    // send a request for a json, if recieved back, fill the array
    private void volleyRequest(String url) {

        RequestQueue queue = Volley.newRequestQueue(this);

        // empty the array to store found JSON data in
        final ArrayList<Track> resultTracks = new ArrayList<Track>();
        resultTracks.clear();

        // Request a string response for recieving content
        JsonObjectRequest content_request = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Display the first 500 characters of the response string.
                Log.d("RECIEVED", "content response is: "+ response.toString());
                try {
                    // read the json
                    JSONObject root_json = response.getJSONObject("root");

                    // test if return was incorrect
                    if (root_json.getJSONObject("tracks").length() == 0) {
                        popup("Couldn't find any songs..");
                        set_data_array_adapter(null);
                        return;
                    }

                    // dig deeper into the json
                    JSONObject meta_json = root_json.getJSONObject("tracks");
                    JSONArray tracks_json = meta_json.getJSONArray("track");

                    // looping through All tracks
                    for (int i = 0; i < tracks_json.length(); i++) {

                        // regard a single track JSON object
                        JSONObject trackJSON = tracks_json.getJSONObject(i);

                        // extract data
                        String id       = trackJSON.getString("id");
                        String title    = trackJSON.getString("title");
                        String artist   = trackJSON.getJSONObject("artist").getString("name");
                        String genre    = trackJSON.getString("genre");

                        // convert data to Track class, and add that class to the array of classes
                        Track item = new Track(id, title, artist, genre);
                        resultTracks.add(item);
                    }

                    // after all data is extracted and converted, fill the display with these tracks
                    set_data_array_adapter(resultTracks);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", "NO CONTENT INFORMATION RECIEVED");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(content_request);
    }

    // set the array adapter, and handle onclick events
    public void set_data_array_adapter(ArrayList<Track> tracksToShow) {

        final ArrayList<Track> tracks = tracksToShow;

        // make array adapter, find view, and assign adapter to view
        final ArrayAdapter trackAdapter = new TrackAdapter(this, tracksToShow);
        final ListView listView = (ListView) findViewById(R.id.listTracks);
        listView.setAdapter(trackAdapter);

        // add listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView < ? > adapter, View view,int position, long arg) {
                //super.onItemClick(adapter, view, position, arg);
                onTrackClick(adapter, view, position, arg, tracks);
            }
        });
    }

    public void onTrackClick(AdapterView < ? > adapter, View view,int position, long arg, ArrayList<Track> tracks) {



        // get user
        final String userID = mAuth.getCurrentUser().getEmail().replace(".", "");

        // retrieve track object, and add user as a favorite
        final Track track = tracks.get(position);

        // try to retrieve same track object from database
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // see if user already possesses this track
                Track otherTrack = dataSnapshot.child("" + userID).child("track" + track.id).getValue(Track.class);

                // if not, add it
                if (otherTrack == null) {

                    // add object to database
                    mDatabase.child("" + userID).child("track" + track.id).setValue(track);

                    popup("Track added!");

                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                // Failed to read value
                Log.w("FIREBASE", "Failed to read value.", error.toException());
            }
        });
    }


    // set up layout of trackadapter
    public class TrackAdapter extends ArrayAdapter<Track> {
        public TrackAdapter(Context context, ArrayList<Track> tracks) {
            super(context, 0, tracks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Track track = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_entry, parent, false);
            }

            // Lookup view for data population
            TextView name = (TextView) convertView.findViewById(R.id.title);
            TextView artist = (TextView) convertView.findViewById(R.id.artist);
            TextView genre = (TextView) convertView.findViewById(R.id.genre);

            // Populate the data into the template view using the data object
            name.setText(track.title);
            artist.setText(track.artist);
            genre.setText(track.genre);

            return convertView;
        }
    }

    // hide the ugly toast sintax
    public void popup(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }



}
