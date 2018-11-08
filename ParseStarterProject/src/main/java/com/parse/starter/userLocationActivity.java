package com.parse.starter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parse.Parse;

public class userLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String target;
    String senderLatitude, senderLogitude, reciverLatitude, reciverLongitude;
    DatabaseReference myRef, myRef2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        target = intent.getStringExtra("target");
        myRef = FirebaseDatabase.getInstance().getReference("users").child(target);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear();
                reciverLatitude = dataSnapshot.child("Latitude").getValue().toString();
                reciverLongitude = dataSnapshot.child("Longitude").getValue().toString();
                LatLng sydney = new LatLng(Double.parseDouble(reciverLatitude),
                        Double.parseDouble(reciverLongitude));
                mMap.addMarker(new MarkerOptions().position(sydney).title(dataSnapshot.child("userName").getValue().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRef2 = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                senderLatitude = dataSnapshot.child("Latitude").getValue().toString();
                senderLogitude = dataSnapshot.child("Longitude").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void directions(View view) {
        String uri = "http://maps.google.com/maps?saddr=" + senderLatitude + "," + senderLogitude + "&daddr=" + reciverLatitude + "," + reciverLongitude;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }
}
