package com.parse.starter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {


    public ChatFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ArrayList<String> users, status, ids, imagesUri;
        final ArrayList<Bitmap> images;
        DatabaseReference myRef;

        myRef = FirebaseDatabase.getInstance().getReference("users");
        users = new ArrayList<>();
        status = new ArrayList<>();
        images = new ArrayList<>();
        imagesUri = new ArrayList<>();
        ids = new ArrayList<>();
        View rootView = inflater.inflate(R.layout.activity_chat_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.userlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final listAdapter adapter = new listAdapter(users, status, images, false, ids, imagesUri);
        recyclerView.setAdapter(adapter);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    users.add(dataSnapshot.child("userName").getValue().toString());
                    Log.i("info", "users " + dataSnapshot.child("userName").getValue().toString());
                    ids.add(dataSnapshot.getKey());
                    if(dataSnapshot.child("status").getValue()!=null){
                        status.add(dataSnapshot.child("status").getValue().toString());
                    }
                    if(dataSnapshot.child("imageUri").getValue()!=null){
                        imagesUri.add(dataSnapshot.child("imageUri").getValue().toString());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Log.i("info", "users" + users.size());

        //recyclerView.clic


        return rootView;
    }
}
