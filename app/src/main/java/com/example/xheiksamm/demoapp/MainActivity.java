package com.example.xheiksamm.demoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.xheiksamm.demoapp.Model.Users;
import com.example.xheiksamm.demoapp.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button joinnowButton, loginButton;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       loginButton=(Button) findViewById(R.id.main_login_btn);
        joinnowButton= (Button) findViewById(R.id.main_join_now_btn);
        loadingBar = new ProgressDialog(this);
        Paper.init(this);
       loginButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(),Login1.class);
               startActivity(intent);
           }
       });
        joinnowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),register2Activity.class);
                startActivity(intent);
            }
        });
        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswardKey = Paper.book().read(Prevalent.UserPasswardKey);

        if(UserPhoneKey != "" && UserPasswardKey != "")
        {
            if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswardKey))
            {
                AllowAccess(UserPhoneKey,UserPasswardKey);

                loadingBar.setTitle("Already Logged in ");
                loadingBar.setMessage("Please wait......");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }

        }
    }

    private void AllowAccess(final String phone, final String passward)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child("Users").child(phone).exists())
                {

                    Users usersData = dataSnapshot.child("Users").child(phone).getValue(Users.class);

                    assert usersData != null;
                    if(usersData.getPhone().equals(phone))
                    {
                        if(usersData.getPassward().equals(passward))
                        {

                                Toast.makeText(MainActivity.this, "logged in Successfully.....", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(MainActivity.this, Home2Activity.class);
                                Prevalent.currentonlineUser = usersData;
                                startActivity(intent);

                        }
                    }


                }
                else
                {
                    Toast.makeText( MainActivity.this,"Account with this"+ phone + "number not Exist",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }
}
