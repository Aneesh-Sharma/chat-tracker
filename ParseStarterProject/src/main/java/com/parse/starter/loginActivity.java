package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class loginActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {
    EditText userName;
    EditText password;
    EditText email;
    TextView toggle;
    Button SignLog;
    RelativeLayout layout;
    ImageView logo;
    boolean isSignUp = true;
    private FirebaseAuth mAuth;
    ArrayList<String> m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName = (EditText) findViewById(R.id.userName);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        toggle = (TextView) findViewById(R.id.toggleButton);
        SignLog = (Button) findViewById(R.id.logInButton);
        layout = (RelativeLayout) findViewById(R.id.layout);
        logo = (ImageView) findViewById(R.id.logo);
        //FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();


        layout.setOnClickListener(loginActivity.this);
        logo.setOnClickListener(loginActivity.this);
        password.setOnKeyListener(loginActivity.this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userListIntent();
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void signUp(View view) {
        final String user, pass;
        user = email.getText().toString();
        pass = password.getText().toString();
        if (user.equals("") || pass.equals("")) {
            Toast.makeText(this, "userName and Password is required ", Toast.LENGTH_SHORT).show();
        } else {
            if (isSignUp) {
                FirebaseDatabase.getInstance().getReference("names").child("userNames").
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                                };
                                m = dataSnapshot.getValue(t);
                                if (m != null && m.contains(userName.getText().toString())) {
                                    Toast.makeText(loginActivity.this, "User name already exist", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (m == null) {
                                        m = new ArrayList<>();
                                    }

                                    FirebaseDatabase.getInstance().getReference("names").child("userNames").setValue(m);
                                    mAuth.createUserWithEmailAndPassword(user, pass)
                                            .addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        m.add(userName.getText().toString());
                                                      //  Log.d("info", "createUserWithEmail:success");
                                                        FirebaseUser log = mAuth.getCurrentUser();
                                                        FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).child("userName").setValue(userName.getText().toString());
                                                        FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).child("status").setValue("my status");
                                                        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance()
                                                                .getCurrentUser().getUid()).child("Latitude").setValue("29");
                                                        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance()
                                                                .getCurrentUser().getUid()).child("Longitude").setValue("33");
                                                        FirebaseDatabase.getInstance().getReference("users")
                                                                .child(mAuth.getCurrentUser().getUid()).child("imageUri").setValue("https://firebasestorage.googleapis.com/" +
                                                                "v0/b/watsapp-29247.appspot.com/o/default_image.jpg?alt=media&token=bd84ca05-da4c-45e1-91ef-959091cbe7b6");
                                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                .setDisplayName(userName.getText().toString())
                                                                .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/" +
                                                                        "v0/b/watsapp-29247.appspot.com/o/default_image.jpg?alt=media&token=bd84ca05-da4c-45e1-91ef-959091cbe7b6"))
                                                                .build();

                                                        log.updateProfile(profileUpdates)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.d("info", "User profile updated.");
                                                                        }
                                                                    }
                                                                });
                                                        userListIntent();
                                                    } else {
                                                        Log.w("info", "createUserWithEmail:failure", task.getException());
                                                        Toast.makeText(loginActivity.this, "Authentication failed " + task.getException(),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            } else {
                mAuth.signInWithEmailAndPassword(user, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("info", "signInWithEmail:success");
                                    userListIntent();
                                } else {
                                    Log.w("info", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(loginActivity.this, "Authentication failed " + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        }
    }

    public void toggle(View view) {
        if (isSignUp) {
            isSignUp = false;
            SignLog.setText("Login");
            toggle.setText("or Sign Up");
        } else {
            isSignUp = true;
            SignLog.setText("Sign Up");
            toggle.setText("or Login");
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
         //   signUp(view);
            View viewLogo = findViewById(R.id.logo);
            onClick(viewLogo);
        }
        return false;
    }


    public void onClick(View v) {
        if (v.getId() == R.id.logo || v.getId() == R.id.layout) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null) {
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

        }
    }

    public void userListIntent() {
        Intent intent = new Intent(getApplicationContext(), displayActivity.class);
        startActivity(intent);
    }

}
