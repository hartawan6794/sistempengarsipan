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
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cameltech.easrip.Adapter.AdapterDaftarSuratKeluar;
import com.cameltech.easrip.Adapter.AdapterDaftarSuratKeluarPengguna;
import com.cameltech.easrip.Model.PembuatanSuratKeluar;
import com.cameltech.easrip.Model.SuratKeluar;
import com.cameltech.easrip.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;

public class DtlPembuatanSuratKeluarAcitivy extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DtlSuratKeluarAcitivy";
    TextView tvStatus, tvNo, tvTgl, tvDeskripsi, tvAlamatInstansi,
            tvInstansi, edStatus, tvMail, tvLampiran, tvPrihal, sttsUpload;
    TextView tvProses, tvUri, tvNamaFile, tvTitleUpload,tvKeterangan;
    Button sendToSekertaris, uploadFile,btnKeterangan;
    ProgressBar progressBar;
    LinearLayout line1, line2, line3;
    private static final int PICK_PDF_REQUEST1 = 86;
    Uri uriPdf;
    String filePdf;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    ActionBar actionBar;
    String dataPennguna, dataAdmin;
    String getMail,hasil;
    private String getId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dtl_pembuatan_surat_keluar_acitivy);

        initToolbars();
        tvNo = (TextView) findViewById(R.id.tvNoDokumen);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvTgl = (TextView) findViewById(R.id.tvTgl);
        tvDeskripsi = (TextView) findViewById(R.id.tvDeskripsi);
        tvAlamatInstansi = (TextView) findViewById(R.id.tvAlamatInstansi);
        tvInstansi = (TextView) findViewById(R.id.tvInstansi);
        edStatus = (TextView) findViewById(R.id.edStatus);
        tvLampiran = (TextView) findViewById(R.id.tvLampiran);
        tvPrihal = (TextView) findViewById(R.id.tvPerihal);
        tvMail = (TextView) findViewById(R.id.tvEmail);
        sttsUpload = (TextView) findViewById(R.id.sttsUpload);
        tvKeterangan = (TextView) findViewById(R.id.tvKeterangan);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sendToSekertaris = (Button) findViewById(R.id.simpan);
        btnKeterangan = (Button)findViewById(R.id.btnketerangan);
//        edKeterangan = findViewById(R.id.edKeterangan);

        line1 = (LinearLayout) findViewById(R.id.line1);
        line2 = (LinearLayout) findViewById(R.id.line2);
        line3 = (LinearLayout) findViewById(R.id.line3);
        tvTitleUpload = (TextView) findViewById(R.id.tittleUpload);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        getMail = user.getEmail();
        Log.d(TAG, "Email: " + getMail);

        if (getMail.equals("ahsanal.huda@gmail.com") || getMail.equals("ellykasim@gmail.com")) {
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
            line3.setVisibility(View.GONE);
            tvTitleUpload.setVisibility(View.VISIBLE);
            sendToSekertaris.setVisibility(View.VISIBLE);
            btnKeterangan.setVisibility(View.VISIBLE);
        }


        tvProses = (TextView) findViewById(R.id.proses);
        tvUri = (TextView) findViewById(R.id.uri1);
        tvNamaFile = (TextView) findViewById(R.id.namafile);

        sendToSekertaris = (Button) findViewById(R.id.kirimSurat);
        uploadFile = (Button) findViewById(R.id.uploadfile);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Intent i = getIntent();
        dataAdmin = i.getStringExtra(AdapterDaftarSuratKeluar.DATA);
        dataPennguna = i.getStringExtra(AdapterDaftarSuratKeluarPengguna.DATA);

        Log.d(TAG, "DATA : " + dataAdmin);
        Log.d(TAG, "DATA : " + dataPennguna);

        if (dataPennguna != null) {
            readArsip(dataPennguna);
        }
        if (dataAdmin != null) {
            readArsip(dataAdmin);
        }

        uploadFile.setOnClickListener(this);

    }

    public void download(View v) {
        final String uri = tvUri.getText().toString();
        final String tittle = tvInstansi.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.foldeer);
        builder.setTitle("E-Arsip Kpps Bimu Lampung");
        builder.setMessage("Ingin Mendownload File?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DownloadPdf(uri, tittle);
                Log.d(TAG, "Uri: " +tvUri.getText().toString());
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

    private void initToolbars() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Detail Surat Keluar");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void readArsip(String namaSurat) {
        databaseReference.child("PembuatanSuratKeluar").orderByChild("instansi").equalTo(namaSurat).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PembuatanSuratKeluar arsip = ds.getValue(PembuatanSuratKeluar.class);
                    tvNo.setText(arsip.getNo());
                    tvDeskripsi.setText(arsip.getDeskripsi());
                    tvTgl.setText(arsip.getTanggal());
                    tvInstansi.setText(arsip.getInstansi());
                    tvMail.setText(arsip.getEmail());
                    tvLampiran.setText(arsip.getLampiran());
                    tvPrihal.setText(arsip.getPerihal());
                    tvAlamatInstansi.setText(arsip.getAlamatInstansi());
                    edStatus.setText(arsip.getStatusPersetujuan());
                    tvUri.setText(arsip.getUri());
                    getId = ds.getKey();
                    tvKeterangan.setText(arsip.getKeterangan());
                    Log.d(TAG, "Keterangan : " + arsip.getKeterangan());
                    hasil = arsip.getStatusPersetujuan();
                    if (hasil.equals("1")) {
                        tvStatus.setText("Menunggu Persetujuan Sekertaris");
                    }
                    if (hasil.equals("2")) {
                        tvStatus.setText("Menunggu Persetujuan Direktur");
                    }
                    if (hasil.equals("3")) {
                        tvStatus.setText("Surat Disetujui");
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showFileChooserPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_PDF_REQUEST1);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_PDF_REQUEST1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriPdf = data.getData();
            filePdf = data.getData().getLastPathSegment();
            tvNamaFile.setText(filePdf);
            uploadFilePDF();
        } else {
        }

    }

    private void uploadFilePDF() {
        tvUri.setText(null);
        if (isEmpty(tvUri.getText().toString())) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference riversRef = storage.getReferenceFromUrl("gs://earsip-354b7.appspot.com").child("files").child(filePdf);
            riversRef.putFile(uriPdf)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot1) {
                            String imageUploadInfo = (taskSnapshot1.getDownloadUrl().toString());

                            tvUri.setText(imageUploadInfo);
                            tvNamaFile.setText(filePdf);
                            Toast.makeText(getApplicationContext(), "File Uploaded 1", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot1) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot1.getBytesTransferred()) / taskSnapshot1.getTotalByteCount();
                            progressBar.setProgress((int) progress);
                            tvProses.setText((int) progress + "%");

                        }
                    });
        } else {
        }
    }


    private boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }


    @Override
    public void onClick(View v) {
        if (v == uploadFile) {
            if (edStatus.getText().toString().equals("1")) {
                if (getMail.equals("ahsanal.huda@gmail.com") || getMail.equals("amel.021297@gmail.com")) {
                    showFileChooserPDF();
                }
                if (getMail.equals("ellykasim@gmail.com")) {
                    TastyToast.makeText(getApplicationContext(), "Menunggu Persetujuan oleh Sekertaris", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                }
            }
            if (edStatus.getText().toString().equals("2")) {
                if (getMail.equals("ahsanal.huda@gmail.com") || getMail.equals("amel.021297@gmail.com")) {
                    TastyToast.makeText(getApplicationContext(), "Menunggu Persetujuan Direktur Utama", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                }
                if (getMail.equals("ellykasim@gmail.com")) {

                    showFileChooserPDF();
                }
            }
        }
    }

    public void simpan(View v) {

        HashMap<String, Object> updateStatus = new HashMap<>();


        if (edStatus.getText().toString().equals("1")) {
            if (getMail.equals("ahsanal.huda@gmail.com") || getMail.equals("amel.021297@gmail.com")) {
                if (!isEmpty(tvUri.getText().toString())) {
                    edStatus.setText("2");
                    updateStatus.put("statusPersetujuan", edStatus.getText().toString());
                    updateStatus.put("uri", tvUri.getText().toString());
                    updateStatus.put("keterangan","-");
                    TastyToast.makeText(getApplicationContext(), "Surat Disetujui oleh Sekertaris", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);

                    databaseReference.child("PembuatanSuratKeluar").child(getId).updateChildren(updateStatus).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                        }
                    });
                }else{
                    TastyToast.makeText(getApplicationContext(),"Anda belum upload surat yang telah disetujui",TastyToast.LENGTH_LONG,TastyToast.INFO);
                }
            }
            if (getMail.equals("ellykasim@gmail.com")) {
                TastyToast.makeText(getApplicationContext(), "Menunggu Persetujuan oleh Sekertaris", TastyToast.LENGTH_LONG, TastyToast.WARNING);
            }
        }

        if (edStatus.getText().toString().equals("2")) {
            if (getMail.equals("ahsanal.huda@gmail.com") || getMail.equals("amel.021297@gmail.com")) {
                TastyToast.makeText(getApplicationContext(), "Menunggu Persetujuan Direktur Utama", TastyToast.LENGTH_LONG, TastyToast.WARNING);
            }
            if (getMail.equals("ellykasim@gmail.com")) {
                TastyToast.makeText(getApplicationContext(), "Surat Disetujui oleh Direktur Utama", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                if (!isEmpty(tvUri.getText().toString())) {
                    edStatus.setText("3");
                    updateStatus.put("statusPersetujuan", edStatus.getText().toString());
                    updateStatus.put("uri", tvUri.getText().toString());
                    updateStatus.put("keterangan","-");
                    TastyToast.makeText(getApplicationContext(), "Surat disetujui oleh direktur", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    databaseReference.child("PembuatanSuratKeluar").child(getId).updateChildren(updateStatus).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                        }
                    });
                }else{
                    TastyToast.makeText(getApplicationContext(),"Anda belum upload surat yang telah disetujui",TastyToast.LENGTH_LONG,TastyToast.INFO);
                }
            }
        }
    }

    public static Intent getActIntent(Activity activity) {
        return new Intent(activity, DtlPembuatanSuratKeluarAcitivy.class);
    }

    public void keterangan(View view) {

        if (edStatus.getText().toString().equals("1")) {
            if (getMail.equals("ahsanal.huda@gmail.com") || getMail.equals("amel.021297@gmail.com")) {
                tambahKeterangan();
            }
            if (getMail.equals("ellykasim@gmail.com")) {
                TastyToast.makeText(getApplicationContext(), "Menunggu Persetujuan oleh Sekertaris", TastyToast.LENGTH_LONG, TastyToast.WARNING);
            }
        }
        if (edStatus.getText().toString().equals("2")) {
            if (getMail.equals("ahsanal.huda@gmail.com") || getMail.equals("amel.021297@gmail.com")) {
                TastyToast.makeText(getApplicationContext(), "Menunggu Persetujuan Direktur Utama", TastyToast.LENGTH_LONG, TastyToast.WARNING);
            }
            if (getMail.equals("ellykasim@gmail.com")) {

                tambahKeterangan();
            }
        }

    }

    private void tambahKeterangan(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DtlPembuatanSuratKeluarAcitivy.this);
        alertDialog.setTitle("Keterangan");

        final EditText input = new EditText(DtlPembuatanSuratKeluarAcitivy.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.mipmap.icon);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                HashMap<String, Object> updateStatus = new HashMap<>();
                updateStatus.put("keterangan", input.getText().toString());
                databaseReference.child("PembuatanSuratKeluar").child(getId).updateChildren(updateStatus);
                startActivity(new Intent(getApplicationContext(), DaftarPembuatanSuratKeluarPenggunaActivity.class));
                TastyToast.makeText(getApplicationContext(),"Berhasil menambahkan keteranga" ,TastyToast.LENGTH_LONG,TastyToast.INFO);
            }
        });

        alertDialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

}
