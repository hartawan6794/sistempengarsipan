package com.cameltech.easrip.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.cameltech.easrip.Adapter.AdapterDaftarPembuatanSuratKeluarPengguna;
import com.cameltech.easrip.Adapter.AdapterDaftarSuratKeluarPengguna;
import com.cameltech.easrip.Model.PembuatanSuratKeluar;
import com.cameltech.easrip.Model.SuratKeluar;
import com.cameltech.easrip.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;

public class DaftarPembuatanSuratKeluarPenggunaActivity extends AppCompatActivity {

    private static final String TAG = "DaftarPembuatanSuratKel";
    private DatabaseReference databaseReference;
    private RecyclerView rvView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<PembuatanSuratKeluar> suratKeluars;
    EditText pencarian;
    ImageButton cari;
    public String hasil, data;
    private ActionBar actionBar;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_pembuatan_surat_keluar_pengguna);
        pencarian = (EditText) findViewById(R.id.pencarian);
        cari = (ImageButton) findViewById(R.id.cari);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        rvView = (RecyclerView) findViewById(R.id.rvMain);  databaseReference = FirebaseDatabase.getInstance().getReference();
        rvView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvView.setLayoutManager(layoutManager);
        hasil = pencarian.getText().toString();
        //cariArsip();
        initToolbar();
        cariArsipKeluar();

        cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasil = pencarian.getText().toString();
                cariArsipKeluar();
            }
        });
    }

    public void cariArsipKeluar() {
        databaseReference.child("PembuatanSuratKeluar").orderByChild("instansi").startAt(hasil).endAt(hasil + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.INVISIBLE);

                if (dataSnapshot.getValue() == null) {
                    TastyToast.makeText(getApplicationContext(), "Tidak ada daftar pembuatan surat", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                } else {
                    suratKeluars = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final PembuatanSuratKeluar arsip = ds.getValue(PembuatanSuratKeluar.class);
                        arsip.setKey(ds.getKey());
                        Log.d(TAG, "Surat Keluar: " + ds.getValue());
                        suratKeluars.add(arsip);
                    }
                    adapter = new AdapterDaftarPembuatanSuratKeluarPengguna(DaftarPembuatanSuratKeluarPenggunaActivity.this, suratKeluars);
                    rvView.setAdapter(adapter);
                }
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
        actionBar.setTitle("Daftar Surat Keluar");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }

    public static Intent getActIntent(Activity activity) {
        // kode untuk pengambilan Intent
        return new Intent(activity, DaftarPembuatanSuratKeluarPenggunaActivity.class);
    }
}
