package com.cameltech.easrip.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cameltech.easrip.Adapter.AdapterDaftarPenggunaApp;
import com.cameltech.easrip.Model.User;
import com.cameltech.easrip.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;

public class DBReadPenggunaActivity extends AppCompatActivity implements AdapterDaftarPenggunaApp.FirebaseDataListener {

    private static final String TAG = "DBReadPenggunaActivity";
    ImageView addUser;
    private DatabaseReference databaseReference;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<User> daftarusers;
    EditText pencarian;
    ImageButton cari;
    public String hasil;
    ActionBar actionBar;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbread_pengguna);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        addUser = (ImageView) findViewById(R.id.ivAddData);
        pencarian = (EditText) findViewById(R.id.pencarian);
        cari = (ImageButton) findViewById(R.id.cari);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.rvMain);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        hasil = pencarian.getText().toString();

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),TambahPenggunaActivity.class));
                finish();
            }
        });

        cariPengguna();

        initToolbar();
        cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasil = pencarian.getText().toString();
                cariPengguna();
            }
        });
    }

    private void cariPengguna() {
        databaseReference.child("Users").orderByChild("email").startAt(hasil).endAt(hasil + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                daftarusers= new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    final User user = ds.getValue(User.class);
                    user.setKey(ds.getKey());
                    daftarusers.add(user);
                }

                adapter = new AdapterDaftarPenggunaApp(daftarusers,DBReadPenggunaActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getDetails() + " " + databaseError.getMessage());

            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Daftar Pengguna App");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public static Intent getActIntent(Activity activity){
        return new Intent(activity, DBReadPenggunaActivity.class);
    }

    @Override
    public void onDeleteData(User user, int position) {

        if(databaseReference!=null){
            databaseReference.child("Users").child(user.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    TastyToast.makeText(getApplicationContext(),"Data Berhasil di Hapus",TastyToast.LENGTH_LONG,TastyToast.INFO);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
            });
        }

    }
}
