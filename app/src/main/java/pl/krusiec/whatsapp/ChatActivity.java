package pl.krusiec.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    String activeUser = "";
    ArrayList<String> messages = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    DatabaseReference dbMessages;

    public void sendChat(View view) {
        EditText chatEditText = findViewById(R.id.chatEditText);

        Map<String, String> messageMap = new HashMap<>();
        Date currentTime = Calendar.getInstance().getTime();

        messageMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        messageMap.put("recipient", activeUser);
        messageMap.put("message", chatEditText.getText().toString());
        messageMap.put("time", currentTime.toString());

        chatEditText.setText("");

        FirebaseDatabase.getInstance().getReference().child("messages").push().setValue(messageMap);

        Toast.makeText(this, "Message sent.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        activeUser = intent.getStringExtra("email");

        setTitle("Chat with " + activeUser);

        ListView chatListView = findViewById(R.id.chatListView);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        chatListView.setAdapter(arrayAdapter);

        dbMessages = FirebaseDatabase.getInstance().getReference("messages");

        dbMessages.orderByChild("time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                messages.clear();

                for (DataSnapshot object : dataSnapshot.getChildren()) {
                    Map<String, String> map;
                    map = (Map<String, String>) object.getValue();

                    if (map.get("sender").equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()) || map.get("recipient").equals(activeUser)
                    || map.get("sender").equals(activeUser) || map.get("recipient").equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        messages.add(map.get("message"));
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
