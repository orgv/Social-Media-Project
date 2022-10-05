package com.example.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Chat;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public static final int MSG_TYPE_SENT = 0;
    public static final int MSG_TYPE_RECEIVED = 1;

    private Context context;
    private List<Chat> chats;
    private String imageUrl;

    FirebaseUser firebaseUser;


    public MessageAdapter(Context context, List<Chat> chats, String imageUrl) {
        this.context = context;
        this.chats = chats;
        this.imageUrl = imageUrl;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.message_profile_pic);
            message = itemView.findViewById(R.id.message_content);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if (viewType == MSG_TYPE_SENT) {
            view = LayoutInflater.from(context).inflate(R.layout.in_chat_sent_item, parent, false);
        } else { // MSG_TYPE_RECEIVED
            view = LayoutInflater.from(context).inflate(R.layout.in_chat_received_item, parent, false);
        }
        return new MessageAdapter.MessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {

        Chat chat = chats.get(position);

        holder.message.setText(chat.getMessage());

        if (imageUrl.equals("default")) {
            holder.profilePic.setImageResource(R.drawable.default_profile_pic);
        } else {
            Glide.with(context).load(imageUrl).into(holder.profilePic);
        }

//        holder.fullName.setText(user.getFirstName() + " " + user.getLastName());
//
//        if (user.getImageUrl().equals("default")) {
//            holder.profilePic.setImageResource(R.drawable.default_profile_pic);
//        } else {
//            Glide.with(context).load(user.getImageUrl()).into(holder.profilePic);
//        }
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Bundle bundle = new Bundle();
//                bundle.putString("userIdField", user.getId());
//
//                //Navigation.createNavigateOnClickListener(R.id.inChatFragment, bundle);
//                System.out.println("HEZION");
//                //Navigation.findNavController(getActivity(), R.id.my_main_host_fragment).navigate(R.id.action_LoginF_to_MainF);
//                Navigation.findNavController(v).navigate(R.id.action_usersFragment_to_inChatFragment, bundle);
//                //Navigation.createNavigateOnClickListener(R.id.action_usersFragment_to_inChatFragment, bundle);
//            }
//        });
//

    }

    // TODO: Do input validation

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equals(firebaseUser.getUid()))
            return MSG_TYPE_SENT;
        else
            return MSG_TYPE_RECEIVED;
    }
}
