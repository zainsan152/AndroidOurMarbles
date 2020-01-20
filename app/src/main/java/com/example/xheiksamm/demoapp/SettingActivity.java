package com.example.xheiksamm.demoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xheiksamm.demoapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.w3c.dom.Text;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity
{

    private CircleImageView profileImageView;
    private EditText fullNameEditText,phoneNumberEditText,addressEditText;
    private TextView profileChangeTextbtn,closeTextbtn,saveTextbtn;

    private Uri imageUri;
    private String myUrl = "" ;
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profileImageView = (CircleImageView) findViewById(R.id.setting_profile_image);
        fullNameEditText = (EditText) findViewById(R.id.setting_full_name);
        phoneNumberEditText= (EditText) findViewById(R.id.setting_phone_number);
        addressEditText = (EditText) findViewById(R.id.setting_address);
        profileChangeTextbtn = (TextView) findViewById(R.id.profile_iamge_change_btn);
        closeTextbtn = (TextView) findViewById(R.id.close_setting_btn);
        saveTextbtn = (TextView) findViewById(R.id.update_setting_btn);


        userInfoDisplay(profileImageView,fullNameEditText,phoneNumberEditText,addressEditText);

        closeTextbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();

            }
        });

        saveTextbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingActivity.this);

            }
        });
    }

    private void updateOnlyUserInfo()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullNameEditText.getText().toString());
        userMap.put("phone", phoneNumberEditText.getText().toString());
        userMap.put("address", addressEditText.getText().toString());

        ref.child(Prevalent.currentonlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingActivity.this,Home2Activity.class));
        Toast.makeText(SettingActivity.this, "Profile info Update Successfully...", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error,try again...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent (SettingActivity.this,SettingActivity.class));
            finish();
        }

    }

    private void userInfoSaved()
    {

        if(TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is Required", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneNumberEditText.getText().toString()))
        {
            Toast.makeText(this, "Phone Number is Required", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Address is Required", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked"))
        {
            uploadImage();
        }

    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Upload Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if(imageUri != null)
        {
            final StorageReference fileRef = storageProfilePictureRef
                    .child(Prevalent.currentonlineUser.getPhone() + ".jpg");

            uploadTask= fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                 @Override
                 public void onComplete(@NonNull Task<Uri> task)
                 {
                     if(task.isSuccessful())
                     {
                         Uri downloadUrl = task.getResult();
                         myUrl = downloadUrl.toString();

                         DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                         HashMap<String, Object> userMap = new HashMap<>();
                         userMap.put("name", fullNameEditText.getText().toString());
                         userMap.put("phone", phoneNumberEditText.getText().toString());
                         userMap.put("address", addressEditText.getText().toString());
                         userMap.put("image", myUrl);

                         ref.child(Prevalent.currentonlineUser.getPhone()).updateChildren(userMap);

                         progressDialog.dismiss();

                         startActivity(new Intent(SettingActivity.this,Home2Activity.class));
                         Toast.makeText(SettingActivity.this, "Profile info Update Successfully...", Toast.LENGTH_SHORT).show();
                         finish();

                     }
                     else
                     {
                         progressDialog.dismiss();
                         Toast.makeText(SettingActivity.this, "Errors", Toast.LENGTH_SHORT).show();
                     }
                 }
             });
        }
        else
        {
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText phoneNumberEditText, final EditText addressEditText)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentonlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText((name));
                        phoneNumberEditText.setText((phone));
                        addressEditText.setText(address);

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
}
