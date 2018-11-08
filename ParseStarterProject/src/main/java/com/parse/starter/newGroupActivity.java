package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class newGroupActivity extends AppCompatActivity {

    Toolbar toolbar;
    public ArrayList<String> members, selectedMembers, grpNames;
    ListView memberList;
    ArrayAdapter<String> arrayAdapter;
    public FirebaseDatabase database;
    DatabaseReference myRef, myRef2;
    GroupMetaData grpMetaData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        grpNames = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        toolbar = (Toolbar) findViewById(R.id.new_group_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add group members");
        selectedMembers = new ArrayList<String>();
        members = new ArrayList<String>();
        myRef2 = database.getReference("users");
        myRef = database.getReference("groupsData");
        memberList = (ListView) findViewById(R.id.memberList);
        memberList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, members);
        memberList.setAdapter(arrayAdapter);
        /////////////
        myRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    members.add(dataSnapshot.child("userName").getValue().toString());
                    arrayAdapter.notifyDataSetChanged();
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
//
//        members.add("batman");
//        members.add("spider man");
//        members.add("super man");
        ////////////
        //   myRef = database.getReference("groups");

        selectedMembers.add(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        memberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                if (checkedTextView.isChecked()) {
                    Log.i("app", "checked " + checkedTextView.getText().toString());
                    selectedMembers.add(checkedTextView.getText().toString());
                } else {
                    selectedMembers.remove(selectedMembers.indexOf(checkedTextView.getText().toString()));
                }
            }
        });

    }

    public void addGroup(View view) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(newGroupActivity.this);
        alertDialog.setTitle("Name Your Group");
        alertDialog.setMessage("what's your group name?");
        final EditText input = new EditText(newGroupActivity.this);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference("names").child("grpNames")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                        };
                        ArrayList<String> m = dataSnapshot.getValue(t);
                        if (m != null && m.contains(input.getText().toString())) {
                            Toast.makeText(newGroupActivity.this, "Group name already exist", Toast.LENGTH_SHORT).show();
                        } else {
                            if (m == null) {
                                m = new ArrayList<>();
                            }
                            m.add(input.getText().toString());
                            FirebaseDatabase.getInstance().getReference("names").child("grpNames").setValue(m);
                            String key = (myRef.push()).getKey();
                            //       myRef.child(key).setValue(selectedMembers);
                            grpMetaData = new GroupMetaData();
                            grpMetaData.setGrpName(input.getText().toString());
                            grpMetaData.setGrpStatus("Your status");
                            grpMetaData.setImageUri("https://firebasestorage.googleapis.com/v0/b/watsapp-29247.appspot.com/o/default_image.jpg?alt=media&token=bd84ca05-da4c-45e1-91ef-959091cbe7b6");
                            grpMetaData.setMembers(selectedMembers);
                            myRef.child(key).setValue(grpMetaData);
//                myRef2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grp").push().setValue(input.getText().toString());
                            Intent intent = new Intent(getApplicationContext(), displayActivity.class);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
}

