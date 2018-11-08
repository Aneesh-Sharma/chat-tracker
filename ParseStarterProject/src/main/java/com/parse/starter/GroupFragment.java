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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {

    public GroupFragment() {
    }

    public ArrayList<String> users, status, ids;
    public ArrayList<String> images;
    public listAdapter adapter;
    public FirebaseDatabase database;
    DatabaseReference myRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        users = new ArrayList<>();
        status = new ArrayList<>();
        images = new ArrayList<>();
        ids = new ArrayList<>();
        View rootView = inflater.inflate(R.layout.activity_group_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.groupList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new listAdapter(users, status, new ArrayList<Bitmap>(), true, ids, images);
        recyclerView.setAdapter(adapter);
//        users.add("mcu");
//        users.add("dc universe");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("groupsData");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                users.add(dataSnapshot.getKey());
//                adapter.notifyDataSetChanged();
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                ArrayList<String> m = (ArrayList<String>) dataSnapshot.child("members").getValue(t);

                if(m.contains(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
                    users.add(dataSnapshot.child("grpName").getValue().toString());
                    status.add(dataSnapshot.child("grpStatus").getValue().toString());
                    ids.add(dataSnapshot.getKey());
                    images.add(dataSnapshot.child("imageUri").getValue().toString());
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


        return rootView;
    }
}
