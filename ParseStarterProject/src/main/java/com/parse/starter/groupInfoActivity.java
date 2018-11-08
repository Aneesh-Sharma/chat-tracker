package com.parse.starter;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class groupInfoActivity extends AppCompatActivity {
    public Toolbar toolbar;
    public Intent intent;
    public TextView groupNameView, groupStatusView;
    public ImageView imageView;
    public String grpName, grpStatus, imageUri;
    public ArrayList<String> members;
    public ListView grpMemberList;
    public ArrayAdapter<String> adapter;
    public FirebaseDatabase database;
    public DatabaseReference myRef;
    StorageTask mUploadTask;
    private StorageReference mStorageRef;
    public static final int PICK_IMAGE = 1;
    ArrayList<String> m;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        toolbar = (Toolbar) findViewById(R.id.group_info_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Group Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intent = getIntent();
        mStorageRef = FirebaseStorage.getInstance().getReference("images");
        grpMemberList = (ListView) findViewById(R.id.groupMembers);
        members = new ArrayList<String>();
        groupNameView = (TextView) findViewById(R.id.groupName);
        imageView = (ImageView) findViewById(R.id.groupImage);
        groupStatusView = (TextView) findViewById(R.id.groupStatus);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("groupsData").child(intent.getStringExtra("id"));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                grpName = snapshot.child("grpName").getValue().toString();
                grpStatus = snapshot.child("grpStatus").getValue().toString();
                imageUri = snapshot.child("imageUri").getValue().toString();
                groupNameView.setText(grpName);
                groupStatusView.setText(grpStatus);
                Picasso.get().load(imageUri).into(imageView);
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                };
                m = (ArrayList<String>) snapshot.child("members").getValue(t);
                members.clear();
                for (String s : m) {
                    members.add(s);
                    adapter.notifyDataSetChanged();
                }
                //Log.i("info", m.size() + " " + m.get(2));
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, members);
        grpMemberList.setAdapter(adapter);
    }


    public void changeName(View view) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(groupInfoActivity.this);
        alertDialog.setTitle("Group Name");
        alertDialog.setMessage("what's your Group Name?");
        final EditText input = new EditText(groupInfoActivity.this);
        alertDialog.setView(input);
        input.setText(grpName);
        alertDialog.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myRef.child("grpName").setValue(input.getText().toString());
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

    public void changeStatus(View view) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(groupInfoActivity.this);
        alertDialog.setTitle("Group Status");
        alertDialog.setMessage("what's your Group Status?");
        final EditText input = new EditText(groupInfoActivity.this);
        alertDialog.setView(input);
        input.setText(grpStatus);
        alertDialog.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myRef.child("grpStatus").setValue(input.getText().toString());
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImage();
            }
        }
    }

    public void changeImage(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                getImage();
            }
        } else {
            getImage();
        }
    }

    public void getImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent d) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri uri = d.getData();
            Picasso.get().load(uri).into(imageView);
            uploadFile(uri);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(Uri mImageUri) {
        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(intent.getStringExtra("id") + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(groupInfoActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri dlUri = uri;
                                    myRef.child("imageUri").setValue(dlUri.toString());
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(groupInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void exitGrp(View view) {
        m.remove(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        myRef.child("members").setValue(m);
        Intent intent = new Intent(getApplicationContext(), displayActivity.class);
        startActivity(intent);

    }
}
