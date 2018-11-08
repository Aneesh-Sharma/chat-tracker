package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.v4.content.ContextCompat.startActivity;

public class listAdapter extends RecyclerView.Adapter<listAdapter.listViewHolder> {

    private ArrayList<String> data, status;
    private ArrayList<Bitmap> image;
    private Boolean isGroup;
    private ArrayList<String> ids;
    private ArrayList<String> imageUri;

    public listAdapter(ArrayList<String> username, ArrayList<String> status, ArrayList<Bitmap> image,
                       boolean isGroup, ArrayList<String> ids, ArrayList<String> imageUri) {
        this.data = username;
        this.status = status;
        this.image = image;
        this.isGroup = isGroup;
        this.ids = ids;
        this.imageUri = imageUri;
    }

    @Override
    public listViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.listitem, parent, false);
        view.setOnClickListener(new MyOnClickListener());
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(listViewHolder holder, int position) {
        holder.user_name.setText(data.get(position));
        if (status.size() > position) {
            holder.user_status.setText(status.get(position));
        }
        if (imageUri.size() > position) {
          //  holder.user_image.setImageBitmap(image.get(position));
            Picasso.get().load(imageUri.get(position)).into( holder.user_image);
        }
//        if(isGroup){
//            Picasso.get().load(imageUri.get(position)).into( holder.user_image);
//        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class listViewHolder extends RecyclerView.ViewHolder {
        TextView user_name;
        TextView user_status;
        CircleImageView user_image;

        public listViewHolder(View itemView) {
            super(itemView);
            user_name = (TextView) itemView.findViewById(R.id.all_user_name);
            user_status = (TextView) itemView.findViewById(R.id.all_user_status);
            user_image = (CircleImageView) itemView.findViewById(R.id.all_user_image);
        }

//        @Override
//        public void onClick(View view) {
//            Log.i("info", "clicked ");
//            Intent intent=new Intent(view.getContext(),userChatActivity.class);
//            startActivity(view.getContext(),intent,null);
//        }
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent;
            // Log.i("info", "clicked " + ((TextView) v.findViewById(R.id.all_user_name)).getText().toString());
            String userName = ((TextView) v.findViewById(R.id.all_user_name)).getText().toString();
            if (isGroup) {
                intent = new Intent(v.getContext(), groupChatActivity.class);
            } else {
                intent = new Intent(v.getContext(), userChatActivity.class);
            }
            intent.putExtra("id", ids.get(data.indexOf(userName)));
            intent.putExtra("userName", userName);


            startActivity(v.getContext(), intent, null);
        }
    }

}
