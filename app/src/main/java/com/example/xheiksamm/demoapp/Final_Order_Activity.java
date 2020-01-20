package com.example.xheiksamm.demoapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.xheiksamm.demoapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Final_Order_Activity extends AppCompatActivity
{
    private Button ConfirmOrderbtn;
    private EditText nameEditText,phoneEditText,addressEditText,cityEditText;

    private String totalAmount="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final__order_);

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price = $" + totalAmount, Toast.LENGTH_SHORT).show();

        ConfirmOrderbtn = (Button) findViewById(R.id.confirm_order_btn);
        nameEditText = (EditText) findViewById(R.id.shippment_name);
        phoneEditText = (EditText) findViewById(R.id.shippment_phone);
        addressEditText = (EditText) findViewById(R.id.shippment_address);
        cityEditText = (EditText) findViewById(R.id.shippment_city);

        ConfirmOrderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Check();
            }
        });
    }

    private void Check()
    {
        if(TextUtils.isEmpty(nameEditText.getText()))
        {
            Toast.makeText(this, "Plesae provide your full name...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneEditText.getText()))
        {
            Toast.makeText(this, "Plesae provide your Phone Number...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEditText.getText()))
        {
            Toast.makeText(this, "Plesae provide your Address...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cityEditText.getText()))
    {
        Toast.makeText(this, "Plesae provide your City name...", Toast.LENGTH_SHORT).show();
    }
    else
        {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder()
    {
        String saveCurrentDate, saveCurrentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM,dd.yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH,mm,ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());
        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentonlineUser.getPhone());
        HashMap<String , Object> orderMap = new HashMap<>();
        orderMap.put("totalAmount ",totalAmount);
        orderMap.put("name",nameEditText.getText().toString());
        orderMap.put("phone",phoneEditText.getText().toString());
        orderMap.put("date",saveCurrentDate);
        orderMap.put("time",saveCurrentTime);
        orderMap.put("address",addressEditText.getText().toString());
        orderMap.put("city",cityEditText.getText().toString());
        orderMap.put("state","Not Shipped");

       ordersRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task)
           {
               if(task.isSuccessful())
               {
                   FirebaseDatabase.getInstance().getReference().child("Cart List")
                           .child("User View")
                           .child(Prevalent.currentonlineUser.getPhone())
                           .removeValue()
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task)
                               {
                                   if(task.isSuccessful())
                                   {
                                       Toast.makeText(Final_Order_Activity.this, "Your final Order has been placed successfully.", Toast.LENGTH_SHORT).show();

                                       Intent intent = new Intent(Final_Order_Activity.this,Home2Activity.class);
                                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                       startActivity(intent);
                                       finish();
                                   }

                               }
                           });

               }

           }
       });
    }
}
