package com.example.xheiksamm.demoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xheiksamm.demoapp.Model.Users;
import com.example.xheiksamm.demoapp.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;


public class Login1 extends AppCompatActivity {
    private EditText InputPhoneNumber,InputPassward;
    private ProgressDialog loadingBar;
    private String parentDbName="Users";
    private TextView AdminLink,NotAdminLink;
    private CheckBox chkBoxRememberme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        final Button loginButton = (Button) findViewById(R.id.login_btn);
        InputPassward = (EditText) findViewById(R.id.login_passward_input);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        AdminLink=(TextView) findViewById(R.id.Admin_panal_tv);
        NotAdminLink=(TextView)findViewById(R.id.not_Admin_panal_tv);
        chkBoxRememberme = (CheckBox) findViewById(R.id.chkbox_remember_me);
        loadingBar = new ProgressDialog(this);
        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName="Admins";


            }
        });
        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName="Users";
            }
        });

    }

    private void LoginUser()
    {
        String passward = InputPassward.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        if(TextUtils.isEmpty(phone))
        {
           Toast.makeText(this,"please write your phone number....",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(passward))
        {
          Toast.makeText(this,"please write your passward....",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials......");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone,passward);
        }
    }

    private void AllowAccessToAccount(final String phone,final String passward)
    {
        if(chkBoxRememberme.isChecked())
        {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswardKey, passward);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    assert usersData != null;
                    if(usersData.getPhone().equals(phone))
                    {
                        if(usersData.getPassward().equals(passward))
                        {
                            if (parentDbName.equals("Admins"))
                            {
                                Toast.makeText(Login1.this, "Welcome Admin,you are logged in Successfully.....", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent1 = new Intent(Login1.this,AdminAddCatagoryActivity.class);
                                startActivity(intent1);

                            }
                            else if ( parentDbName.equals("Users"))
                            {
                                Toast.makeText(Login1.this, "logged in Successfully.....", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(Login1.this, Home2Activity.class);
                                Prevalent.currentonlineUser = usersData;
                                startActivity(intent);
                            }
                        }
                    }


                }
                else
                {
                    Toast.makeText( Login1.this,"Account with this"+ phone + "number not Exist",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });


    }
}
