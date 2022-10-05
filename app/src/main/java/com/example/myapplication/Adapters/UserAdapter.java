package com.example.myapplication.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> users;
    private boolean isChat;

    public UserAdapter(Context context, List<User> users, boolean isChat) {
        this.context = context;
        this.users = users;
        this.isChat = isChat;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView fullName;
        private ImageView civOn;
        private ImageView civOff;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.user_profile_pic);
            fullName = itemView.findViewById(R.id.user_full_name);
            civOn = itemView.findViewById(R.id.civ_on);
            civOff = itemView.findViewById(R.id.civ_off);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        User user = users.get(position);
        holder.fullName.setText(user.getFirstName() + " " + user.getLastName());

        if (user.getImageUrl().equals("default")) {
            holder.profilePic.setImageResource(R.drawable.default_profile_pic);
        } else {
            Glide.with(context).load(user.getImageUrl()).into(holder.profilePic);
        }

        if (isChat) {
            if (user.getStatus().equals("online")) {
                holder.civOn.setVisibility(View.VISIBLE);
                holder.civOff.setVisibility(View.GONE);
            } else {
                holder.civOn.setVisibility(View.GONE);
                holder.civOff.setVisibility(View.VISIBLE);
            }
        } else {
            holder.civOn.setVisibility(View.GONE);
            holder.civOff.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("userIdField", user.getId());

                bundle.putString("isCreatedFromNavComponent", "false");

                //Navigation.createNavigateOnClickListener(R.id.inChatFragment, bundle);
                System.out.println("HEZION");

                    /*

                    //Navigation.findNavController(v).navigate(R.id.inChatFragment, bundle);

                    */

                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.inChatFragment, bundle);


            }
        });


    }

    // TODO: Do input validation

    @Override
    public int getItemCount() {
        return users.size();
    }

}
