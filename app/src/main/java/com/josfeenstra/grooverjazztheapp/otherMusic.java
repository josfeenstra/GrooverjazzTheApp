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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class otherMusic extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabaseinst;
    String DATABASE_REF = "trackData";
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_music);

        // assign buttons to listeners
        ImageButton buttonBack3 = (ImageButton) findViewById(R.id.buttonBack3);
        buttonBack3.setOnClickListener(new onBack3Click());


        // assign buttons to listeners
        ImageButton buttonOtherSearch = (ImageButton) findViewById(R.id.buttonSearchOther);
        buttonOtherSearch.setOnClickListener(new onOtherSearchClick());

        // database
        mAuth = FirebaseAuth.getInstance();
        mDatabaseinst = FirebaseDatabase.getInstance();
        mDatabase = mDatabaseinst.getReference(DATABASE_REF);

    }


    private class onOtherSearchClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            EditText editOtherEmail = (EditText) findViewById(R.id.editOtherFavorites);
            String otherEmail = editOtherEmail.getText().toString();
            if (otherEmail.isEmpty()) {
                popup("Fill in the field.");
                return;
            }

            // convert it
            String userID = otherEmail.replace(".", "");

            // get the user's tracks
            ArrayList<Track> userTracks = fetchData(userID);
        }
    }


    //
    private class onBack3Click implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            // next intent
            Intent intent = new Intent(otherMusic.this, Home.class);
            intent.putExtra("restore1", false);
            startActivity(intent);
            finish();
        }
    }


    // set the array adapter, and handle onclick events
    public void set_data_array_adapter(ArrayList<Track> tracksToShow) {

        // convert for further usage
        final ArrayList<Track> tracks = tracksToShow;

        // make array adapter, find view, and assign adapter to view
        final ArrayAdapter trackAdapter = new TrackAdapter(this, tracksToShow);
        final ListView listView = (ListView) findViewById(R.id.listOtherTracks);
        listView.setAdapter(trackAdapter);

        // add listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView < ? > adapter, View view,int position, long arg) {
                //super.onItemClick(adapter, view, position, arg);
                //super.onItemClick(adapter, view, position, arg);
                onTrackClick(adapter, view, position, arg, tracks);
            }
        });
    }

    public ArrayList<Track> fetchData(String userId) {

        final String userID = userId;
        final ArrayList<Track> myTracks = new ArrayList<Track>();

        // try to retrieve same track object from database
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.w("FIREBASE", "number of entries " + snapshot.child("" + userID).getChildrenCount());
                for (DataSnapshot postSnapshot : snapshot.child("" + userID).getChildren()) {
                    Log.w("FIREBASE", "added one");
                    myTracks.add(postSnapshot.getValue(Track.class));

                }

                // fill listview with those tracks
                set_data_array_adapter(myTracks);
            }

            @Override
            public void onCancelled(DatabaseError error) {

                // Failed to read value
                Log.w("FIREBASE", "Failed to read value.", error.toException());
            }
        });
        Log.w("FIREBASE", "" + myTracks.size());
        return myTracks;
    }



    public void onTrackClick(AdapterView< ? > adapter, View view, int position, long arg, ArrayList<Track> tracks) {

        // use dialog screen for: removing song / deeplinking
        Track track = tracks.get(position);

        popup("do something with the song");
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

