package com.cameltech.easrip.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cameltech.easrip.Model.SuratMasuk;
import com.cameltech.easrip.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SuratMasukActivity extends AppCompatActivity implements View.OnClickListener {

    EditText tvNamaDok, tvNoDok, tvDeskripsi, tvmapArsip, tvInstansi, tvAlamatInstansi;
    EditText tvSifat, tvTanggal, tvLampiran, tvPerihal, tvNamaMapArsip;
    TextView tvProses, tvUri, tvNamaFile, kategoriSurat;
    Button simpan, upload;
    ImageButton btnCalender;
    private static final int PICK_PDF_REQUEST1 = 86;
    Uri uriPdf;
    String filePdf;
    String kategori;
    ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private ActionBar actionBar;
    private static final String TAG = "ArsipIoActivity";
    private DatePickerDialog.OnDateSetListener datePickerDialog;
    Calendar newCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surat_masuk);

        tvNamaDok = (EditText) findViewById(R.id.nama);
        tvNoDok = (EditText) findViewById(R.id.nomorDok);
        tvDeskripsi = (EditText) findViewById(R.id.edket);
        tvmapArsip = (EditText) findViewById(R.id.boxArsip);
        tvProses = (TextView) findViewById(R.id.proses);
        tvUri = (TextView) findViewById(R.id.uri1);
        tvNamaFile = (TextView) findViewById(R.id.namafile);
        kategoriSurat = (TextView) findViewById(R.id.kategoriSurat);
        tvInstansi = (EditText) findViewById(R.id.edInstansi);
        tvAlamatInstansi = (EditText) findViewById(R.id.edAlamatInstansi);
        tvSifat = (EditText) findViewById(R.id.edSifat);
        tvNamaMapArsip = (EditText) findViewById(R.id.namaBoxArsip);
        tvPerihal = (EditText) findViewById(R.id.edPerihal);
        tvLampiran = (EditText) findViewById(R.id.edLampiran);
        tvTanggal = (EditText) findViewById(R.id.tvTanggal);

        btnCalender = (ImageButton) findViewById(R.id.btnCalender);
        simpan = (Button) findViewById(R.id.simpan);
        upload = (Button) findViewById(R.id.file);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Intent i = getIntent();
        kategori = i.getStringExtra(DashboardAdmin.DATA);

        Log.d(TAG, "kategori: " + kategori);
        newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                newCalendar.set(Calendar.YEAR, year);
                newCalendar.set(Calendar.MONTH, month);
                newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setTanggal();
            }
        };

        tvTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(SuratMasukActivity.this, datePickerDialog, newCalendar.get(Calendar.YEAR)
                        , newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show();
                tvNoDok.requestFocus();
            }
        });

        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(SuratMasukActivity.this, datePickerDialog, newCalendar.get(Calendar.YEAR)
                        , newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show();
                tvNoDok.requestFocus();
            }
        });

        upload.setOnClickListener(this);


        initToolbar();

        final SuratMasuk arsip = (SuratMasuk) getIntent().getSerializableExtra("data");

        if (arsip != null) {
            tvNoDok.setText(arsip.getNo());
            tvNoDok.setEnabled(false);
            tvInstansi.requestFocus();
            tvInstansi.setText(arsip.getInstansi());
            tvAlamatInstansi.setText(arsip.getAlamatInstansi());
            tvSifat.setText(arsip.getSifat());
            tvPerihal.setText(arsip.getPerihal());
            tvLampiran.setText(arsip.getLampiran());
            tvNamaDok.setText(arsip.getNamaSurat());
            tvTanggal.setText(arsip.getTanggal());
            tvDeskripsi.setText(arsip.getDeskripsi());
            tvmapArsip.setText(arsip.getMapArsip());
            tvNamaMapArsip.setText(arsip.getNamaMapArsip());
            tvUri.setText(arsip.getFile());
            kategori = arsip.getKategori();
            if (kategori.equals("1")) {
                actionBar.setTitle("Form Ubah Surat");
                kategoriSurat.setText("Ubah Surat Masuk");
            }
            if (kategori.equals("2")) {
                actionBar.setTitle("Form Ubah Surat");
                kategoriSurat.setText("Ubah Surat Keluar");
            }
            simpan.setText("Ubah Data");
            simpan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arsip.setNamaSurat(tvNamaDok.getText().toString());
                    arsip.setInstansi(tvInstansi.getText().toString());
                    arsip.setAlamatInstansi(tvInstansi.getText().toString());
                    arsip.setSifat(tvSifat.getText().toString());
                    arsip.setPerihal(tvPerihal.getText().toString());
                    arsip.setLampiran(tvLampiran.getText().toString());
                    arsip.setDeskripsi(tvDeskripsi.getText().toString());
                    arsip.setNo(tvNoDok.getText().toString());
                    arsip.setTanggal(tvTanggal.getText().toString());
                    arsip.setFile(tvUri.getText().toString());
                    arsip.setMapArsip(tvmapArsip.getText().toString());
                    arsip.setNamaMapArsip(tvNamaMapArsip.getText().toString());
                    updateData(arsip);
                }
            });
        } else {

            simpan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEmpty(tvNamaDok.getText().toString()) && !isEmpty(tvNoDok.getText().toString())
                            && !isEmpty(tvDeskripsi.getText().toString())) {
                        tambahBerkas(new SuratMasuk(tvNoDok.getText().toString(), tvInstansi.getText().toString(), tvAlamatInstansi.getText().toString()
                                , tvSifat.getText().toString(), tvLampiran.getText().toString(), tvPerihal.getText().toString()
                                , tvTanggal.getText().toString(), tvNamaDok.getText().toString(), tvUri.getText().toString(), tvDeskripsi.getText().toString()
                                , tvmapArsip.getText().toString(), tvNamaMapArsip.getText().toString(), kategori));
                    } else
                        Snackbar.make(findViewById(R.id.simpan), "Data arsip tidak boleh kosong", Snackbar.LENGTH_LONG).show();

                }
            });

        }


    }

    private void updateData(SuratMasuk arsip) {
        databaseReference.child("SuratMasuk").child(arsip.getKey()).setValue(arsip)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(findViewById(R.id.simpan), "Data Berhasil di Ubah", Snackbar.LENGTH_LONG).setAction("Oke", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }).show();
                    }
                });
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        actionBar.setTitle("Form Pengisian Surat");
        kategoriSurat.setText("Tambah Surat");

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
            StorageReference riversRef = storage.getReferenceFromUrl("gs://earsip-354b7.appspot.com").child("filesIO").child(filePdf);
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


    void tambahBerkas(SuratMasuk arsip) {

        databaseReference.child("SuratMasuk").push().setValue(arsip).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                tvNamaDok.setText("");
                tvNoDok.setText("");
                tvNoDok.requestFocus();
                tvDeskripsi.setText("");
                tvInstansi.setText("");
                tvmapArsip.setText("");
                tvAlamatInstansi.setText("");
                tvSifat.setText("");
                tvLampiran.setText("");
                tvPerihal.setText("");
                tvNamaMapArsip.setText("");
                tvUri.setText("");
                Snackbar.make(findViewById(R.id.simpan), "Data berhasil ditambahkan", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void setTanggal() {
        String format = "dd - MMM - yyyy";
        Locale localeID = new Locale("id", "ID");
        SimpleDateFormat dF = new SimpleDateFormat(format, localeID);
        tvTanggal.setText(dF.format(newCalendar.getTime()));

    }

    private boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    @Override
    public void onClick(View v) {

        if (v == upload) {
            showFileChooserPDF();
        }
    }

    public static Intent getActIntent(Activity activity) {
        return new Intent(activity, SuratMasukActivity.class);
    }
}
