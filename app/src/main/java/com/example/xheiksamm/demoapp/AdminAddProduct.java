package com.example.xheiksamm.demoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddProduct extends AppCompatActivity {
private String CategoryName,Description,Price,Pname,saveCurrentDate,saveCurrentTime;
private ImageView InputProductImage;
private EditText InputProductName,InputProductDescription,InputProductPrice;
private Button AddProductButton;
private static final int Gallerypick=1;
private String productRandomkey,DownloadImageUri;
private Uri ImageUri;
private StorageReference ProductImageRef;
private DatabaseReference productRef;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        CategoryName= getIntent().getExtras().get("category").toString();


        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Product Image");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        InputProductImage=(ImageView) findViewById(R.id.select_product_image);
        AddProductButton=(Button) findViewById(R.id.product_include);
        InputProductName=(EditText)findViewById(R.id.product_name_input);
        InputProductDescription=(EditText)findViewById(R.id.product_description_input);
        InputProductPrice=(EditText)findViewById(R.id.product_price_input);
        loadingBar = new ProgressDialog(this);

        InputProductImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OpenGallery();
            }
        });

        AddProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidateProductData();
            }
        });
    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallerypick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallerypick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri=data.getData();
            InputProductImage.setImageURI(ImageUri);
        }
    }

    private void  ValidateProductData()
    {
        Description=InputProductDescription.getText().toString();
        Price=InputProductPrice.getText().toString();
        Pname=InputProductName.getText().toString();

        if(ImageUri == null)
        {
            Toast.makeText(this, "Product Image is mandatory....", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Product Description is Required", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Price))
        {

            Toast.makeText(this, "Product Price is required", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Pname))
        {
            Toast.makeText(this, "Product name is Required", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation()
    {
        loadingBar.setTitle("Adding New Product");
        loadingBar.setMessage("Dear Admin,please wait,while we are Adding new product......");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat(" MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomkey= saveCurrentDate + saveCurrentTime;

       final StorageReference filepath = ProductImageRef.child(ImageUri.getLastPathSegment() + productRandomkey + ".jpg");

       final UploadTask uploadTask = filepath.putFile(ImageUri);

       uploadTask.addOnFailureListener(new OnFailureListener()
       {
           @Override
           public void onFailure(@NonNull Exception e)
           {
               String  message = e.toString();
               Toast.makeText(AdminAddProduct.this, "Error" + message, Toast.LENGTH_SHORT).show();
               loadingBar.dismiss();
           }
       }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
       {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
           {
               Toast.makeText(AdminAddProduct.this, "Product Image Uploaded Successfully...", Toast.LENGTH_SHORT).show();

               Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                   @Override
                   public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {


                       if(!task.isSuccessful())
                       {
                           throw task.getException();
                       }
                       DownloadImageUri = filepath.getDownloadUrl().toString();
                       return filepath.getDownloadUrl();

                   }
               }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                   @Override
                   public void onComplete(@NonNull Task<Uri> task)
                   {
                       if(task.isSuccessful())
                       {
                           DownloadImageUri = task.getResult().toString();
                           Toast.makeText(AdminAddProduct.this, "Product Image save to Database Successfully....", Toast.LENGTH_SHORT).show();
                           SaveProductInfoToDatabase();
                       }

                   }
               });
           }
       });

    }

    private void SaveProductInfoToDatabase()
    {

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomkey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", DownloadImageUri);
        productMap.put("category", CategoryName);
        productMap.put("price", Price);
        productMap.put("pname", Pname);

        productRef.child(productRandomkey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Intent intent = new Intent(AdminAddProduct.this, AdminAddCatagoryActivity.class);
                            startActivity(intent);
                            loadingBar.dismiss();
                            Toast.makeText(AdminAddProduct.this, "Product is added Successfully...", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddProduct.this, "Error"+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
