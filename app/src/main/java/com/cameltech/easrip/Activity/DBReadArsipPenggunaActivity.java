package com.cameltech.easrip.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.cameltech.easrip.Adapter.AdapterDaftarSuratMasukPengguna;
import com.cameltech.easrip.Adapter.AdapterDaftarArsipPengguna;
import com.cameltech.easrip.Adapter.AdapterDaftarSuratKeluarPengguna;
import com.cameltech.easrip.Model.Arsip;
import com.cameltech.easrip.Model.SuratKeluar;
import com.cameltech.easrip.Model.SuratMasuk;
import com.cameltech.easrip.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;

public class DBReadArsipPenggunaActivity extends AppCompatActivity {

    private static final String TAG = "DBReadArsipActivity";

    private DatabaseReference databaseReference;
    private RecyclerView rvView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Arsip> daftarArsip;
    private ArrayList<SuratMasuk> suratMasuks;
    private ArrayList<SuratKeluar> suratKeluars;
    EditText pencarian;
    ImageButton cari;
    public String hasil;
    private ActionBar actionBar;
    ProgressBar progressBar;
    Spinner kategori;
    private String[] jenisDok = {"Surat Perusahaan", "Surat Masuk", "Surat Keluar"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbread_arsip_pengguna);

        pencarian = (EditText) findViewById(R.id.pencarian);
        cari = (ImageButton) findViewById(R.id.cari);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        rvView = (RecyclerView) findViewById(R.id.rvMain);
        kategori = (Spinner) findViewById(R.id.spinKategor);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, "Database : " + databaseReference);
        rvView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvView.setLayoutManager(layoutManager);
        hasil = pencarian.getText().toString();
        cariArsip();
        initToolbar();

        cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hasil = pencarian.getText().toString();
                cariArsip();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DBReadArsipPenggunaActivity.this, R.layout.simple_kategori, R.id.test, jenisDok);
        kategori.setAdapter(adapter);
        kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (kategori.getSelectedItem() == "Surat Perusahaan") {
                    cariArsip();
                }
                if (kategori.getSelectedItem() == "Surat Masuk") {
                    cariArsipMasuk();
                }
                if (kategori.getSelectedItem() == "Surat Keluar") {
                    cariArsipKeluar();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void cariArsip() {
        databaseReference.child("arsip").orderByChild("namaDok").startAt(hasil).endAt(hasil + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.INVISIBLE);
                daftarArsip = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final Arsip arsip = ds.getValue(Arsip.class);
                    arsip.setKey(ds.getKey());
                    daftarArsip.add(arsip);
                }
                adapter = new AdapterDaftarArsipPengguna(DBReadArsipPenggunaActivity.this, daftarArsip);
                rvView.setAdapter(adapter);
                Log.d(TAG, "onDataChange: " + rvView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getDetails() + " " + databaseError.getMessage());

            }
        });
    }

    public void cariArsipMasuk() {
        databaseReference.child("SuratMasuk").orderByChild("kategori").equalTo("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.INVISIBLE);

                if (dataSnapshot.getValue() == null) {
                    TastyToast.makeText(getApplicationContext(), "Tidak Ada Surat Masuk", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                } else {
                    suratMasuks = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final SuratMasuk arsip = ds.getValue(SuratMasuk.class);
                        arsip.setKey(ds.getKey());
                        Log.d(TAG, "onDataChange: " + ds.getChildren());
                        suratMasuks.add(arsip);
                    }
                    adapter = new AdapterDaftarSuratMasukPengguna(DBReadArsipPenggunaActivity.this, suratMasuks);
                    rvView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getDetails() + " " + databaseError.getMessage());

            }
        });
    }

    public void cariArsipKeluar() {
        databaseReference.child("SuratMasuk").orderByChild("kategori").equalTo("2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.INVISIBLE);
                if (dataSnapshot.getValue() == null) {
                    TastyToast.makeText(getApplicationContext(), "Tidak Ada Surat Keluar", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                } else {
                    suratMasuks = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final SuratMasuk arsip = ds.getValue(SuratMasuk.class);
                        arsip.setKey(ds.getKey());
                        Log.d(TAG, "onDataChange: " + ds.getChildren());
                        suratMasuks.add(arsip);
                    }
                    adapter = new AdapterDaftarSuratMasukPengguna(DBReadArsipPenggunaActivity.this, suratMasuks);
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
        actionBar.setTitle("Daftar Arsip");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }

    public static Intent getActIntent(Activity activity) {
        // kode untuk pengambilan Intent
        return new Intent(activity, DBReadArsipPenggunaActivity.class);
    }

}
