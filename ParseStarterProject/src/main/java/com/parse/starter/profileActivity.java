package com.parse.starter;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class profileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    TextView userName;
    TextView userStatus;
    ImageView userImage;
    public static final int PICK_IMAGE = 1;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    public FirebaseUser user;
    StorageTask mUploadTask;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.profile_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userName = (TextView) findViewById(R.id.username);
        userImage = (ImageView) findViewById(R.id.userimage);
        userStatus = (TextView) findViewById(R.id.userstatus);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("images");
        // userName.setText(ParseUser.getCurrentUser().getUsername().toString());
        userName.setText(mAuth.getCurrentUser().getDisplayName());
        FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid())
                .child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        userStatus.setText(dataSnapshot.getValue().toString());
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                }
        );
        Log.i("info", "" + mAuth.getCurrentUser().getDisplayName());
//       if(ParseUser.getCurrentUser().get("status")!=null){
//            userStatus.setText((String)ParseUser.getCurrentUser().get("status"));
//        }
        Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).into(userImage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent d) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri uri = d.getData();
            Picasso.get().load(uri).into(userImage);
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
            final StorageReference fileReference = mStorageRef.child(mAuth.getCurrentUser().getUid() + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(profileActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //myRef.child("imageUri").setValue(uri.toString());
                                    FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).child("imageUri").setValue(uri.toString());
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri)
                                            .build();
                                    mAuth.getCurrentUser().updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("info", "User image updated.");
                                                    }
                                                }
                                            });
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(profileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeName(View view) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(profileActivity.this);
        alertDialog.setTitle("User Name");
        alertDialog.setMessage("what's your Name?");
        final EditText input = new EditText(profileActivity.this);
        alertDialog.setView(input);
        input.setText(mAuth.getCurrentUser().getDisplayName());
        alertDialog.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userName.setText(input.getText().toString());
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(input.getText().toString())
                        .build();

                mAuth.getCurrentUser().updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("info", "User name updated.");
                                }
                            }
                        });
                FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).child("userName").setValue(input.getText().toString());

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
//

    public void changeStatus(View view) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(profileActivity.this);
        alertDialog.setTitle("User Status");
        alertDialog.setMessage("what's your Status?");
        final EditText input = new EditText(profileActivity.this);
        alertDialog.setView(input);
        input.setText(userStatus.getText().toString());
        alertDialog.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userStatus.setText(input.getText().toString());
                FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).child("status").setValue(input.getText().toString());

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
