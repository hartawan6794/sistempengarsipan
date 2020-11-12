package com.cameltech.easrip.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cameltech.easrip.Model.User;
import com.cameltech.easrip.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("unchecked")
public class PengaturanPenggunaActivity extends AppCompatActivity {


    public static final String DATA = "PengaturanPenggunaActiv";
    private static final String TAG = "PengaturanPenggunaActiv";
    private FirebaseDatabase database;
    private DatabaseReference users;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    String alias;
    EditText tvEmail, tvUser;
    TextView clickedImage;
    Button update, cancel;
    CircleImageView profile;
    ActionBar actionBar;
    String uri = "";
    private String myUrl = "";
    String cheker = "";
    private Uri imageUri;
    TextView changePass;
    String getId;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan_pengguna);

        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Pictures");
        firebaseAuth = FirebaseAuth.getInstance();
        tvEmail = (EditText) findViewById(R.id.tvEmail);
        tvUser = (EditText) findViewById(R.id.tvUser);
        profile = (CircleImageView) findViewById(R.id.profile);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        update = (Button) findViewById(R.id.btnUpdate);
        cancel = (Button) findViewById(R.id.btnCancel);
        changePass = (TextView) findViewById(R.id.updatePass);
        clickedImage = (TextView) findViewById(R.id.tvTittle);
        initToolbas();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        alias = user.getEmail();
        getId = user.getUid();
        users = FirebaseDatabase.getInstance().getReference().child("Users");
        users.orderByChild("email").equalTo(alias).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);

                    tvEmail.setText(user.getEmail());
                    tvUser.setText(user.getUsername());
                    tvUser.setEnabled(true);
                    Picasso.get().load(user.getUserImage()).into(profile);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PengaturanPenggunaActivity.this, ChangePasswordActivity.class).putExtra(DATA, tvEmail.getText().toString()));
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cheker.equals("clicked")) {
                    userInfoSave();
                } else {
                    updateOnlyUserInfo();
                }
            }
        });


        clickedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cheker = "clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(PengaturanPenggunaActivity.this);
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profile.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Eror. Coba Lagi.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PengaturanPenggunaActivity.this, PengaturanPenggunaActivity.class));
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void userInfoSave() {

        if (TextUtils.isEmpty(tvUser.getText().toString())) {
            Toast.makeText(this, "Nama Wajib di Isi", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(tvEmail.getText().toString())) {

        } else if (cheker.equals("clicked")) {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Silahkan Tunggu, Sedang Memperbarui Akun Anda");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference imageRef = storageReference
                    .child(tvUser.getText().toString() + ".jpg");
            uploadTask = imageRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                myUrl = downloadUri.toString();

                                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Users");

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("email", tvEmail.getText().toString());
                                userMap.put("username", tvUser.getText().toString());
                                userMap.put("userImage", myUrl);
                                dataRef.child(getId).updateChildren(userMap);

                                progressDialog.dismiss();
                                startActivity(new Intent(PengaturanPenggunaActivity.this, DashboardAdmin.class));
                                TastyToast.makeText(getApplicationContext(), "Berhasil Memperbarui Photo Profile Anda", TastyToast.LENGTH_LONG, TastyToast.INFO);
                                finish();
                            } else {
                                progressDialog.dismiss();
                                TastyToast.makeText(getApplicationContext(), "Gagal Memperbarui Photo Profile Anda", TastyToast.LENGTH_LONG, TastyToast.WARNING);

                            }
                        }
                    });
        } else {
            TastyToast.makeText(getApplicationContext(), "Gambar Belum di Pilih", TastyToast.LENGTH_LONG, TastyToast.WARNING);
        }
    }

    private void updateOnlyUserInfo() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Silahkan Tunggu, Sedang Memperbarui Akun Anda");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", tvEmail.getText().toString());
        userMap.put("username", tvUser.getText().toString());
        dataRef.child(getId).updateChildren(userMap);


        startActivity(new Intent(PengaturanPenggunaActivity.this, DashboardAdmin.class));
        TastyToast.makeText(getApplicationContext(), "Berhasil Memperbarui Info Profile", TastyToast.LENGTH_LONG, TastyToast.INFO);
        finish();

    }


    private void initToolbas() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile Pengguna");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
