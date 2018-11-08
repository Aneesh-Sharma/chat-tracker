package com.parse.starter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TicTacToeActivity extends AppCompatActivity {
    int player;
    int activePlayer = 0;
    boolean gameActive = true;
    String[] coins = {"YELLOW", "RED"};
    DatabaseReference myRef;
    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    int[][] winPositions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};

    public void play(ImageView coin, int coinTapped) {
        coin.setTranslationY(-1000f);
        coin.setTranslationX(-1000f);
        gameState[coinTapped] = activePlayer;
        if (activePlayer == 0) {
            coin.setImageResource(R.drawable.yellow);
            activePlayer = 1;
        } else {
            coin.setImageResource(R.drawable.red);
            activePlayer = 0;
        }
        coin.animate().translationXBy(1000f).translationYBy(1000f).rotation(360).setDuration(300);
        Log.i("INFO", coinTapped + " ");

        for (int[] winPosition : winPositions) {
            if (gameState[winPosition[0]] == gameState[winPosition[1]] && gameState[winPosition[1]]
                    == gameState[winPosition[2]] && gameState[winPosition[1]] != 2) {
                TextView winnerMessage = (TextView) findViewById(R.id.winnerMessage);
                winnerMessage.setText(coins[gameState[winPosition[1]]] + " WINS!");
                Log.i("INFO", coins[gameState[winPosition[1]]]);
                LinearLayout result = (LinearLayout) findViewById(R.id.result);
                result.setVisibility(View.VISIBLE);
                gameActive = false;
                return;
            }
        }
        boolean gameEnd = true;
        for (int i = 0; i < gameState.length; i++) {
            if (gameState[i] == 2) {
                gameEnd = false;
            }
        }
        if (gameEnd) {
            TextView winnerMessage = (TextView) findViewById(R.id.winnerMessage);
            winnerMessage.setText("GAME DRAW");
            LinearLayout result = (LinearLayout) findViewById(R.id.result);
            result.setVisibility(View.VISIBLE);
            gameActive = false;
        }
    }

    public void dropIn(View view) {

        ImageView coin = (ImageView) view;
        int coinTapped = Integer.parseInt(coin.getTag().toString());
        if (gameState[coinTapped] == 2 && gameActive) {
            myRef.child(String.valueOf(coinTapped)).setValue(activePlayer);
            // play(coin, coinTapped);
        }
    }

    public void playAgain(View view) {
        LinearLayout result = (LinearLayout) findViewById(R.id.result);
        result.setVisibility(View.INVISIBLE);
        GridLayout gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        activePlayer = 0;
        gameActive = true;
        for (int i = 0; i < gameState.length; i++) {
            gameState[i] = 2;
        }
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            ((ImageView) gridLayout.getChildAt(i)).setImageResource(0);
        }
        ArrayList<Integer> gState = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            gState.add(2);
        }
        myRef.setValue(gState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);
        Intent intent = getIntent();
        player = intent.getIntExtra("player", 0);
        String did = intent.getStringExtra("did");
        myRef = FirebaseDatabase.getInstance().getReference("Game").child(did);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (Integer.parseInt(data.getValue().toString()) != gameState[Integer.parseInt(data.getKey())]) {
                        Log.i("zxc", "onDataChange: " + data.getKey());
                        int resID = getResources().getIdentifier("imageView" + data.getKey(), "id", getPackageName());
                        ImageView imageView = findViewById(resID);
                        play(imageView, Integer.parseInt(data.getKey()));
                    }
                    if (Integer.parseInt(data.getValue().toString()) == 2) {
                        gameState[Integer.parseInt(data.getKey())] = 2;
                        int resID = getResources().getIdentifier("imageView" + data.getKey(), "id", getPackageName());
                        ImageView imageView = findViewById(resID);
                        imageView.setImageResource(0);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
