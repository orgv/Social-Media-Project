package com.example.myapplication.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adapters.MessageAdapter;
import com.example.myapplication.Chat;
import com.example.myapplication.R;
import com.example.myapplication.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class InChatFragment extends Fragment {

    CircleImageView profilePicture;
    TextView fullname;

    MessageAdapter messageAdapter;
    List<Chat> chats;

    FirebaseUser firebaseUser;

    RecyclerView recyclerView;

    ImageButton sendBtn;
    EditText sendEt;

    String otherUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_in_chat, container, false);

        //String otherUserId = getArguments().getString("userIdField");
//        Bundle bundle = new Bundle();
//        bundle.putString("", user.getId());
//
//        //Navigation.createNavigateOnClickListener(R.id.inChatFragment, bundle);
//        System.out.println("HEZION");


        if (getArguments().getString("isCreatedFromNavComponent").equals("false")) {

            profilePicture = view.findViewById(R.id.in_chat_profile_pic);
            fullname = view.findViewById(R.id.in_chat_full_name);
            sendBtn = view.findViewById(R.id.in_chat_send_btn);
            sendEt = view.findViewById(R.id.in_chat_et_send);

            Toolbar toolbar = view.findViewById(R.id.in_chat_toolbar);
            toolbar.setTitle("");

            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("isCreatedFromNavComponent", "false");
                    Navigation.findNavController(view).navigate(R.id.usersFragment, bundle);
                }
            });


            recyclerView = view.findViewById(R.id.in_chat_recycler_view);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);

            otherUserId = getArguments().getString("userIdField");

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(otherUserId);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    fullname.setText(user.getFirstName() + " " + user.getLastName());
                    if (user.getImageUrl().equals("default")) {
                        profilePicture.setImageResource(R.drawable.default_profile_pic);
                    } else {
                        Glide.with(InChatFragment.this).load(user.getImageUrl()).into(profilePicture);
                    }

                    //readMessages(firebaseUser.getUid(), otherUserId, user.getImageUrl());
                    readMessages(FirebaseAuth.getInstance().getCurrentUser().getUid(), otherUserId, user.getImageUrl());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String msg = sendEt.getText().toString();
                    if (!msg.equals("")) {
                        sendMessage(firebaseUser.getUid(), otherUserId, msg);
                    } else {
                        Toast.makeText(activity, "Type something to send it!", Toast.LENGTH_SHORT).show();
                    }
                    sendEt.setText("");
                }
            });

        }
        return view;

    }

    private void sendMessage(String senderId, String receiverId, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        System.out.println("HEZION");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", senderId);
        hashMap.put("receiver", receiverId);
        hashMap.put("message", message);
        //hashMap.put("Timestamp", new Date());

        reference.push().setValue(hashMap);


        // add user to inchat fragment
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(otherUserId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef.child("id").setValue(otherUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child()

    }

    private void readMessages(String myId, String userId, String imageUrl) {

        chats = new ArrayList<>();


        DatabaseReference chatsReference = FirebaseDatabase.getInstance().getReference("Chats");
        chatsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) // for chat in chats
                {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if ((chat.getReceiver().equals(myId) && chat.getSender().equals(userId)) ||
                            (chat.getReceiver().equals(userId) && chat.getSender().equals(myId))) {
                        chats.add(chat);
                    }

                }
                messageAdapter = new MessageAdapter(getContext(), chats, imageUrl);
                recyclerView.setAdapter(messageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}