package com.josfeenstra.grooverjazztheapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //FB
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        // assign buttons to listeners
        Button buttonOther = (Button) findViewById(R.id.buttonOther);
        buttonOther.setOnClickListener(new onOtherClick());

        Button buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new onLogoutClick());

        Button buttonViewProfile = (Button) findViewById(R.id.buttonViewProfile);
        buttonViewProfile.setOnClickListener(new onViewProfileClick());

        Button buttonConcerts = (Button) findViewById(R.id.buttonConcerts);
        buttonConcerts.setOnClickListener(new onConcertsClick());

        Button buttonMyConcerts = (Button) findViewById(R.id.buttonMyConcerts);
        buttonMyConcerts.setOnClickListener(new onMyConcertsClick());
    }

    // show the email of the logged in user
    public class onViewProfileClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            // get builder and view for the profile popup dialog
            AlertDialog.Builder suBuilder = new AlertDialog.Builder(Home.this);
            View pView = getLayoutInflater().inflate(R.layout.dialog_profile, null);

            // get the current user stuff
            String email = "";
            String name = "";
            FirebaseUser currUser = mAuth.getCurrentUser();
            if (currUser != null) {
                email = currUser.getEmail();
                name = currUser.getDisplayName();
            }

            // set textviews in the view to the users's values
            TextView emailView = (TextView) pView.findViewById(R.id.textEmail);
            TextView nameView = (TextView) pView.findViewById(R.id.textName);
            emailView.setText(email);
            nameView.setText(name);

            // add a close button
            suBuilder.setNegativeButton("close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            // show the profile popup dialog
            final AlertDialog dialog = suBuilder.create();
            dialog.setView(pView);
            dialog.setTitle("Profile");
            // show the dialog
            dialog.show();
        }
    }

    // log out user
    public class onLogoutClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mAuth.signOut();
            FirebaseUser user = mAuth.getCurrentUser();
            updateUI(user);
        }
    }

    // log out user
    public class onTODOClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            popup("comming soon!");
        }
    }


    public void updateUI(FirebaseUser user) {

        if (user != null) {
            //henk
        } else {
            Log.d("FIREBASE", "onAuthStateChanged: signed out");
            gotoLoginScreen();
            // clear password thingie
        }
    }


    public void gotoLoginScreen() {
        // next intent
        Intent intent = new Intent(Home.this, Login.class);
        intent.putExtra("restore1", false);
        startActivity(intent);
        finish();
    }

    /*
    Navigation: goto others
 */
    private class onOtherClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            // next intent
            Intent intent = new Intent(Home.this, otherMusic.class);
            intent.putExtra("restore1", false);
            startActivity(intent);
            finish();
        }
    }

    /*
        Navigation: goto Concerts
     */
    private class onConcertsClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            // next intent
            Intent intent = new Intent(Home.this, DiscoverMusic.class);
            intent.putExtra("restore1", false);
            startActivity(intent);
            finish();
        }
    }
    /*
        Navigation: goto MyConcerts
     */
    private class onMyConcertsClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            // next intent
            Intent intent = new Intent(Home.this, ShowMusic.class);
            intent.putExtra("restore1", false);
            startActivity(intent);
            finish();
        }
    }

    // hide the ugly toast sintax
    public void popup(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

}
