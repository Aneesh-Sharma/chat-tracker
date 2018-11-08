package com.parse.starter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class groupChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Intent intent;
    private TextView userName;
    private String id;
    public FirebaseDatabase database;
    private RecyclerView messageRecycler;
    private messageListAdapter messageAdapter;
    private ArrayList<DataSnapshot> messageList;
    TextView textToSend;
    public CircleImageView GrpImage;
    DatabaseReference myRef, myRef2;

    public void sendText(View view) {
        if(!textToSend.getText().toString().equals("")){
            ChatData chatData = new ChatData();
            chatData.setMessage(textToSend.getText().toString());
            chatData.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            myRef2.push().setValue(chatData);
            textToSend.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.group_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.group_info) {
            Intent intent = new Intent(getApplicationContext(), groupInfoActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        intent = getIntent();
        userName = (TextView) findViewById(R.id.group_chat_name);
        GrpImage = (CircleImageView) findViewById(R.id.group_chat_image);
        textToSend = findViewById(R.id.textToSend2);
        toolbar = (Toolbar) findViewById(R.id.group_chat_page_toolbar);
        messageList = new ArrayList<DataSnapshot>();
        setSupportActionBar(toolbar);

        messageRecycler = (RecyclerView) findViewById(R.id.grpUserMessage);
        messageAdapter = new messageListAdapter(messageList, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        messageRecycler.setLayoutManager(linearLayoutManager);
        messageRecycler.setAdapter(messageAdapter);

        id = intent.getStringExtra("id");
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        database = FirebaseDatabase.getInstance();
        myRef2 = database.getReference("grp" + id);
        myRef = database.getReference("groupsData").child(id);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userName.setText(snapshot.child("grpName").getValue().toString());
                Picasso.get().load(snapshot.child("imageUri").getValue().toString()).into(GrpImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                messageList.add(dataSnapshot);
                messageAdapter.notifyDataSetChanged();
                messageRecycler.scrollToPosition(messageList.size() - 1);
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
    }
}
