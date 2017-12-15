package com.josfeenstra.grooverjazztheapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // instanciate authenticators, check if a user is logged in
        mAuth = FirebaseAuth.getInstance();

        // assign listeners
        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
        Button buttonSignup = (Button) findViewById(R.id.buttonSignupDialog);
        buttonLogin.setOnClickListener(new LoginClickListener());
        buttonSignup.setOnClickListener(new SignupDialogClickListener());

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    /*
        if a user is logged in, goto next screen, else, just empty out the login screen.
     */
    public void updateUI(FirebaseUser user) {

        if (user != null) {
            // there is a user signed in
            Log.d("FIREBASE", "onAuthStateChanged: signed in: " + user.getUid());
            popup("You are now logged in.");

            // goto next screen
            gotoNextActivity();


        } else {
            Log.d("FIREBASE", "onAuthStateChanged: signed out");

            // clear password thingie
        }
    }

    /*
    manage a singup request
     */
    private class SignupDialogClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {


            // get builder and view for the Sign Up popup dialog
            AlertDialog.Builder suBuilder = new AlertDialog.Builder(Login.this);
            View suView = getLayoutInflater().inflate(R.layout.dialog_signup, null);

            // get all buttons and textfields within dialog
            final EditText emailSU = (EditText) suView.findViewById(R.id.editEmailSU);
            final EditText passwordSU = (EditText) suView.findViewById(R.id.editPasswordSU);
            final EditText password2SU = (EditText) suView.findViewById(R.id.editPassword2SU);
            Button signupSU = (Button) suView.findViewById(R.id.buttonSignupSU);


            suBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            // show the signup popup dialog
            final AlertDialog dialog = suBuilder.create();
            dialog.setView(suView);

            // perform standard signup checks
            signupSU.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // get involved strings
                    String email = emailSU.getText().toString();
                    String password = passwordSU.getText().toString();
                    String password2 = password2SU.getText().toString();

                    // perform standard signup checks
                    if (email.isEmpty() ||
                            password.isEmpty() ||
                            password2.isEmpty()) {
                        // check if everything is filled in
                        popup("Please fill in all fields.");

                    } else if (false) {
                        // check if email is avalable
                        // TODO
                        popup("Email / username already taken.");
                    } else if (!password.equals(password2)) {
                        // check if passwords are the same
                        popup("Passwords do not match.");
                    } else {

                        // store user in firebase
                        createAccount(email, password);

                        // close dialog
                        dialog.dismiss();
                    }
                }
            });
            // show the dialog
            dialog.show();
        }
    }

    private class LoginClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            // get the fields
            EditText editEmail = (EditText) findViewById(R.id.editEmail);
            EditText editPassword = (EditText) findViewById(R.id.editPassword);
            if (editEmail.getText().toString().isEmpty() || editPassword.getText().toString().isEmpty() ) {
                // check if everything is filled in
                popup("Please fill in all fields.");
            } else {

                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();

                // signin user
                signIn(email, password);
            }


        }
    }

    /*
        Firebase, create an account, and go to updateUI
     */
    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FIREBASE", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FIREBASE", "createUserWithEmail:failure", task.getException());
                            popup("Please use a valid email. \nPassword should be at least 6 characters long.");
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    /*
        Firebase, sign in an account, and go to updateUI
     */
    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FIREBASE", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FIREBASE", "signInWithEmail:failure", task.getException());
                            popup("Invalid Username and/or password.");
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    // hide the ugly toast sintax
    public void popup(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void gotoNextActivity() {
        // next intent
        Intent intent = new Intent(Login.this, Home.class);
        intent.putExtra("restore1", false);
        startActivity(intent);
        finish();
    }

}
