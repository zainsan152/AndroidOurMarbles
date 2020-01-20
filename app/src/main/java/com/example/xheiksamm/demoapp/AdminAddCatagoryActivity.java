package com.example.xheiksamm.demoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Button;

public class AdminAddCatagoryActivity extends AppCompatActivity {
    private Button Marble,Tile;
    private Button Logoutbtn,checkorderbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_catagory);


        Marble=(Button) findViewById(R.id.marbles);
        Tile=(Button) findViewById(R.id.tiles);

        Logoutbtn = (Button) findViewById(R.id.Admin_logout_btn);
        checkorderbtn=(Button) findViewById(R.id.Admin_New_Order_btn);


        checkorderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminAddCatagoryActivity.this,NewOrder.class);
                startActivity(intent);
            }
        });


        Logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminAddCatagoryActivity.this,Login1.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });









        Marble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminAddCatagoryActivity.this,AdminAddProduct.class);
                intent.putExtra("category","Marbles");
                startActivity(intent);
            }
        });
        Tile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminAddCatagoryActivity.this,AdminAddProduct.class);
                intent.putExtra("category","Tiles");
                startActivity(intent);
            }
        });


    }
}
