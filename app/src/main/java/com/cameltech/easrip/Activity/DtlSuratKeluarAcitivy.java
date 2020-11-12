package com.cameltech.easrip.Activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cameltech.easrip.Adapter.AdapterDaftarSuratKeluar;
import com.cameltech.easrip.Adapter.AdapterDaftarSuratKeluarPengguna;
import com.cameltech.easrip.Model.PembuatanSuratKeluar;
import com.cameltech.easrip.Model.SuratKeluar;
import com.cameltech.easrip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class DtlSuratKeluarAcitivy extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DtlSuratKeluarAcitivy";
    TextView tvStatus, tvNo, tvTgl, tvDeskripsi, tvAlamatInstansi, tvInstansi, edStatus, tvMail, tvLampiran, tvPrihal;
    TextView tvProses, tvUri, tvNamaFile;
    Button sendToSekertaris, uploadFile;
    ProgressBar progressBar;
    private static final int PICK_PDF_REQUEST1 = 86;
    Uri uriPdf;
    String filePdf;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    ActionBar actionBar;
    String dataPennguna, dataAdmin;
    String getMail;
    private String getId, getTanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dtl_surat_keluar_acitivy);

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
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sendToSekertaris = (Button) findViewById(R.id.kirimSurat);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        getMail = user.getEmail();
        Log.d(TAG, "Email: " + getMail);


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

    private void sendOutBox(PembuatanSuratKeluar pembuatanSuratKeluar) {
        databaseReference.child("PembuatanSuratKeluar").push().setValue(pembuatanSuratKeluar)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        TastyToast.makeText(getApplicationContext(), "Surat Berhasil di Kirim", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                });

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
        databaseReference.child("SuratKeluar").orderByChild("instansi").equalTo(namaSurat).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SuratKeluar arsip = ds.getValue(SuratKeluar.class);
                    tvNo.setText(arsip.getNo());
                    tvDeskripsi.setText(arsip.getDeskripsi());
                    tvTgl.setText(arsip.getTanggal());
                    tvInstansi.setText(arsip.getInstansi());
                    tvMail.setText(arsip.getEmail());
                    tvLampiran.setText(arsip.getLampiran());
                    tvPrihal.setText(arsip.getPerihal());
                    tvAlamatInstansi.setText(arsip.getAlamatInstansi());
                    getId = ds.getKey();
                    Log.d(TAG, "Key : " + getId);
                    if (arsip.getStatusValid().equals("0")) {
                        tvStatus.setText("Menunggu Pembuatan Surat");
                    }
                    if (arsip.getStatusValid().equals("1")) {
                        tvStatus.setText("Menunggu Persetujuan Sekertaris");
                        sendToSekertaris.setVisibility(View.INVISIBLE);
                        uploadFile.setEnabled(false);
                    }
                    if (arsip.getStatusValid().equals("2")) {
                        tvStatus.setText("Menunggu Persetujuan Direktur");
                    }
                    if (arsip.getStatusValid().equals("3")) {
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
        if (v == uploadFile)
            showFileChooserPDF();
    }

    public void sendMail(View v) {

        sendOutBox(new PembuatanSuratKeluar(tvMail.getText().toString(), tvNo.getText().toString(), tvInstansi.getText().toString()
                , tvAlamatInstansi.getText().toString(), tvLampiran.getText().toString(), tvPrihal.getText().toString()
                , tvTgl.getText().toString(), "1", tvDeskripsi.getText().toString(), tvUri.getText().toString(),"-"));
        HashMap<String, Object> updateStatus = new HashMap<>();
        updateStatus.put("statusValid", "1");
        databaseReference.child("SuratKeluar").child(getId).updateChildren(updateStatus).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
    }

    public static Intent getActIntent(Activity activity) {
        return new Intent(activity, DtlSuratKeluarAcitivy.class);
    }
}
