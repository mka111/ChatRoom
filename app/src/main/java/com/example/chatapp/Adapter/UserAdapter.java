package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Fragments.ChatsFragment;
import com.example.chatapp.MainActivity;
import com.example.chatapp.MessageActivity;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.Users;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    private Context context;
    private List<Users> mUsers;
    private boolean ischat;
    String theLastMessage;

    //COnstructor


    public UserAdapter(Context context, List<Users> mUsers, boolean ischat) {
        this.context = context;
        this.mUsers = mUsers;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);


        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Users users = mUsers.get(position);
        holder.username.setText(users.getUsername());

        if(users.getImageURL().equals("default")){
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        } else{

            Glide.with(context).load(users.getImageURL()).into(holder.imageView);
        }
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();


        if(ischat && fuser!= null){
            lastMessage(users.getId(), holder.last_msg, holder.unread_msg);
        }else{
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat){

            if(users.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else{
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else{

            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userid", users.getId());
                context.startActivity(i);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView imageView;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;
        private TextView unread_msg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.userTextview);
            imageView = itemView.findViewById(R.id.userImageview);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            unread_msg = itemView.findViewById(R.id.unread_msg);



        }
    }

    private void lastMessage(final String userid, final TextView last_msg, final TextView unread_msg){

        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        theLastMessage = chat.getMessage();
                    }
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
                       // unread++;
                        /*last_msg.setTypeface(null, Typeface.BOLD);
                        last_msg.setText(theLastMessage);*/
                        //unread_msg.setText(String.valueOf(unread));
                    }
                }


                switch (theLastMessage){
                    case "default":
                        last_msg.setText("No message");
                        break;

                    default:
                        //last_msg.setTypeface(null, Typeface.NORMAL);
                        last_msg.setText(theLastMessage);
                        /*if(unread ==0){
                            last_msg.setTypeface(null, Typeface.NORMAL);
                            last_msg.setText(theLastMessage);

                        }else{
                            last_msg.setTypeface(null, Typeface.BOLD);
                            last_msg.setText(theLastMessage);
                            unread_msg.setText(String.valueOf(unread));


                        }*/
                        break;
                }

                theLastMessage = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}

