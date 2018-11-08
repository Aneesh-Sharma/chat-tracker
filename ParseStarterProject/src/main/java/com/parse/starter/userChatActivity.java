package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class userChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    TextView userName;
    CircleImageView userImage;
    Intent intent;
    String sender, receiver;
    TextView textToSend;
    int player;
    private RecyclerView messageRecycler;
    private messageListAdapter messageAdapter;
    private ArrayList<DataSnapshot> messageList;
    private FirebaseDatabase database;
    DatabaseReference myRef;
    String dataObjName;

    public void sendText(View view) {
        if (!textToSend.getText().toString().equals("")) {
            ChatData chatData = new ChatData();
            chatData.setMessage(textToSend.getText().toString());
            chatData.setName(sender);
            myRef.push().setValue(chatData);
            textToSend.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.tic_tac_toe) {
            Intent intent = new Intent(getApplicationContext(), TicTacToeActivity.class);
            intent.putExtra("did", dataObjName);
            intent.putExtra("player", player);
            ArrayList<Integer> gameState = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                gameState.add(2);
            }
            database.getReference("Game").child(dataObjName).setValue(gameState);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        intent = getIntent();
        sender = FirebaseAuth.getInstance().getCurrentUser().getUid();
        receiver = intent.getStringExtra("id");
        database = FirebaseDatabase.getInstance();
        if (sender.compareTo(receiver) < 0) {
            dataObjName = sender + "_" + receiver;
            player = 0;
        } else {
            dataObjName = receiver + "_" + sender;
            player = 1;
        }
        myRef = database.getReference(dataObjName);
        //toolbar user name and image
        userName = (TextView) findViewById(R.id.user_chat_name);
        userImage = (CircleImageView) findViewById(R.id.user_chat_image);
        textToSend = (TextView) findViewById(R.id.textToSend);
        messageList = new ArrayList<DataSnapshot>();
        toolbar = (Toolbar) findViewById(R.id.user_chat_page_toolbar);
        setSupportActionBar(toolbar);
        userName.setText(intent.getStringExtra("userName"));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //     Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(userImage);
        database.getReference("users").child(receiver).child("imageUri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.getValue().toString()).into(userImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /////////////////////////////////
        //  Log.i("info", "talk");
        messageRecycler = (RecyclerView) findViewById(R.id.userMessage);
        messageAdapter = new messageListAdapter(messageList, FirebaseAuth.getInstance().getCurrentUser().getUid(), false);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        messageRecycler.setLayoutManager(linearLayoutManager);
        messageRecycler.setAdapter(messageAdapter);


        myRef.addChildEventListener(new ChildEventListener() {
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

    public void listDown(View view) {

    }
}