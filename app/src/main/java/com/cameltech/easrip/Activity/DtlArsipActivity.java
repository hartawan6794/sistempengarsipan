package com.cameltech.easrip.Activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cameltech.easrip.Adapter.AdapterDaftarArsipAdmin;
import com.cameltech.easrip.Adapter.AdapterDaftarArsipPengguna;
import com.cameltech.easrip.Model.Arsip;
import com.cameltech.easrip.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DtlArsipActivity extends AppCompatActivity {

    private static final String TAG = "DtlArsipActivity";
    TextView tvNama, tvTahun, tvJenis, tvNo, tvTgl, tvDeskripsi, tvUri,tvBox;
    Button downld;
    DatabaseReference databaseReference;
    ActionBar actionBar;
    String dataPennguna, dataAdmin;
    String download, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dtl_arsip);

        initToolbar();

        tvNama = (TextView) findViewById(R.id.tvNmaDokumen);
        tvNo = (TextView) findViewById(R.id.tvNoDokumen);
        tvJenis = (TextView) findViewById(R.id.tvJenis);
        tvTahun = (TextView) findViewById(R.id.tvThn);
        tvTgl = (TextView) findViewById(R.id.tvTgl);
        tvDeskripsi = (TextView) findViewById(R.id.tvDeskripsi);
        tvUri = (TextView) findViewById(R.id.tvUri);
        downld = (Button) findViewById(R.id.downlFile);
        tvBox = (TextView) findViewById(R.id.tvBox);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Intent i = getIntent();
        dataPennguna = i.getStringExtra(AdapterDaftarArsipPengguna.DATA);
        Log.d(TAG, "Data Pengguna : " + dataPennguna);
        dataAdmin = i.getStringExtra(AdapterDaftarArsipAdmin.DATA);
        Log.d(TAG, "Data Admin : " + dataAdmin);
        if (dataPennguna == null) {
            readDBArsipAdmin(dataAdmin);
        }
        if (dataAdmin == null) {
            readDBArsip(dataPennguna);
        }

    }

    public void download(View v) {
        final String uri = tvUri.getText().toString();
        final String tittle = tvNama.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.foldeer);
        builder.setTitle("E-Arsip Kpps Bimu Lampung");
        builder.setMessage("Ingin Mendownload File?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DownloadPdf(uri, tittle);
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void DownloadPdf(String download, String title) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(download));
        String tempTitle = title.replace("", "");
        request.setTitle(tempTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, tempTitle + ".pdf");
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        request.setMimeType("application/pdf");
        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        downloadManager.enqueue(request);
    }

    private void readDBArsip(String nama) {
        databaseReference.child("arsip").orderByChild("namaDok").equalTo(nama).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Arsip arsip = ds.getValue(Arsip.class);
                    tvNama.setText(arsip.getNamaDok());
                    tvNo.setText(arsip.getNoDok());
                    tvDeskripsi.setText(arsip.getDeskripsi());
                    tvTgl.setText(arsip.getTglUpload());
                    tvTahun.setText(arsip.getThnDokumen());
                    tvUri.setText(arsip.getUriDok());
                    tvBox.setText(arsip.getMapArsip());
                    String jenis = arsip.getJenis();
                    if (jenis.equals("1")) {
                        tvJenis.setText("Surat Keputusan");
                    }
                    if (jenis.equals("2")) {
                        tvJenis.setText("Surat Umum");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readDBArsipAdmin(String nama) {
        databaseReference.child("arsip").orderByChild("namaDok").equalTo(nama).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Arsip arsip = ds.getValue(Arsip.class);
                    tvNama.setText(arsip.getNamaDok());
                    tvNo.setText(arsip.getNoDok());
                    tvDeskripsi.setText(arsip.getDeskripsi());
                    tvTgl.setText(arsip.getTglUpload());
                    tvTahun.setText(arsip.getThnDokumen());
                    tvUri.setText(arsip.getUriDok());
                    tvJenis.setText(arsip.getJenis());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Detail Arsip");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    public static Intent getActIntent(Activity activity) {
        return new Intent(activity, DtlArsipActivity.class);
    }

}
