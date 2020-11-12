package com.cameltech.easrip.Activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cameltech.easrip.Adapter.AdapterDaftarSuratMasuk;
import com.cameltech.easrip.Adapter.AdapterDaftarSuratMasukPengguna;
import com.cameltech.easrip.Model.SuratMasuk;
import com.cameltech.easrip.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DtlSuratMasukActivity extends AppCompatActivity {

    private static final String TAG = "DtlSuratMasukActivty";

    TextView tvNama, tvJenis, tvNo, tvTgl, tvDeskripsi, tvUri, tvBox, tvInstansi,tvKategoriSurat;
    Button downld;
    DatabaseReference databaseReference;
    ActionBar actionBar;
    String dataPennguna, dataAdmin;
    String download, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dtl_arsip_io);

        tvNama = (TextView) findViewById(R.id.tvNmaDokumen);
        tvNo = (TextView) findViewById(R.id.tvNoDokumen);
        tvJenis = (TextView) findViewById(R.id.tvJenis);
        tvTgl = (TextView) findViewById(R.id.tvTgl);
        tvDeskripsi = (TextView) findViewById(R.id.tvDeskripsi);
        tvUri = (TextView) findViewById(R.id.tvUri);
        downld = (Button) findViewById(R.id.downlFile);
        tvBox = (TextView) findViewById(R.id.tvBox);
        tvInstansi = (TextView) findViewById(R.id.tvInstansi);
        tvKategoriSurat = (TextView) findViewById(R.id.kategoriSurat);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Intent i = getIntent();
        dataAdmin = i.getStringExtra(AdapterDaftarSuratMasuk.DATA);
        Log.d(TAG, "Data Pengguna : " + dataPennguna);
        dataPennguna = i.getStringExtra(AdapterDaftarSuratMasukPengguna.DATA);
        Log.d(TAG, "Data Admin : " + dataAdmin);
        if (dataPennguna != null) {
            readDBArsip(dataPennguna);
        }
        if (dataAdmin != null) {
            readDBArsip(dataAdmin);
        }
        initToolbar();

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
        databaseReference.child("SuratMasuk").orderByChild("namaSurat").equalTo(nama).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d(TAG, "DATA: " + ds.getValue().toString());
                    SuratMasuk arsip = ds.getValue(SuratMasuk.class);
                    tvNama.setText(arsip.getNamaSurat());
                    tvNo.setText(arsip.getNo());
                    tvInstansi.setText(arsip.getInstansi());
                    tvDeskripsi.setText(arsip.getDeskripsi());
                    tvTgl.setText(arsip.getTanggal());
                    tvUri.setText(arsip.getFile());
                    tvBox.setText(arsip.getMapArsip());
                    if(arsip.getKategori().equals("1")){
                        tvKategoriSurat.setText("Surat Masuk");
                    }
                    if(arsip.getKategori().equals("2")){
                        tvKategoriSurat.setText("Surat Keluar");
                    }
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
        actionBar.setTitle("Detail Surat");
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
        return new Intent(activity, DtlSuratMasukActivity.class);
    }
}
