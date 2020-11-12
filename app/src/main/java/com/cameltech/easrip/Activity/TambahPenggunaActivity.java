package com.cameltech.easrip.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cameltech.easrip.Model.Arsip;
import com.cameltech.easrip.Model.User;
import com.cameltech.easrip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.regex.Pattern;

public class TambahPenggunaActivity extends AppCompatActivity {

    private static final String TAG = "TambahPenggunaActivity";
    DatabaseReference user;
    FirebaseDatabase database;
    ProgressBar progressBar;
    EditText tvEmail, tvUser, tvPass, tvPemilik;
    Button btnAdd, btnCancel;
    FirebaseAuth firebaseAuth;
    ActionBar actionBar;
    TextView tvTittle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pengguna);

        tvEmail = (EditText) findViewById(R.id.tvRegisMail);
        tvUser = (EditText) findViewById(R.id.tvRegisUser);
        tvPass = (EditText) findViewById(R.id.tvRegisPass);
        tvPemilik = (EditText) findViewById(R.id.tvPemilik);
        btnAdd = (Button) findViewById(R.id.btnRegis);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        tvTittle = (TextView) findViewById(R.id.tvTittle);
        database = FirebaseDatabase.getInstance();
        user = database.getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        final User user = (User) getIntent().getSerializableExtra("data");

        initToolbar();

        if(user!=null){
         tvEmail.setText(user.getEmail());
         tvEmail.setEnabled(false);
         tvUser.setText(user.getUsername());
         tvPass.setVisibility(View.GONE);
         btnAdd.setText("Ubah");
         btnAdd.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 user.setUserNama(tvUser.getText().toString());
                 updateData(user);
             }
         });
        }else {
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    regeisUser();
                }
            });
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DBReadPenggunaActivity.class));
                finish();
            }
        });

    }

    private void regeisUser() {
        final String email = tvEmail.getText().toString();
        final String userAdd = tvUser.getText().toString();
        String pass = tvPass.getText().toString();
        final String imageUri = "https://firebasestorage.googleapis.com/v0/b/earsip-354b7.appspot.com/o/profile.png?alt=media&token=201047fc-4533-47f0-aee9-53efbbf1bad1";
        final String pemilik = tvPemilik.getText().toString();
        final String key = "";
        if (email.isEmpty()) {
            tvEmail.setError("Tambahkan Email");
            tvEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvEmail.setError("Masukan Email Dengan Benar");
            tvEmail.requestFocus();
            return;
        }

        if (userAdd.isEmpty()) {
            tvUser.setError("Masukan Nama Pengguna");
            tvUser.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            tvPass.setError("Masukan Kata Sandi");
            tvPass.requestFocus();
            return;
        }

        if (pass.length() < 8) {
            tvPass.setError("Password Harus 8 karakter / lebih");
            tvPass.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user1 = new User(key, email, pemilik, userAdd,imageUri);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        TastyToast.makeText(getApplicationContext(), "Berhasil Menambahkan Pengguna", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                                        final DatabaseReference getKeyValue = FirebaseDatabase.getInstance().getReference().child("Users");
                                        getKeyValue.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(ds.getKey().toString());
                                                    mDatabase.child("key").setValue(ds.getKey());
                                                    startActivity(new Intent(getApplicationContext(), DBReadPenggunaActivity.class));
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            TastyToast.makeText(getApplicationContext(), task.getException().getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR);

                        }

                    }
                });
    }

    private void updateData(User ubah) {
        user.child(ubah.getKey()).setValue(ubah)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        TastyToast.makeText(getApplicationContext(),"Nama Pengguna Berhasil Dirubah",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                    }
                });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Tambah Pengguna");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public static Intent getActIntent(Activity activity){
        return  new Intent(activity,TambahPenggunaActivity.class);
    }
}
