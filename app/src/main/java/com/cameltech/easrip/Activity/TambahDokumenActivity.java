package com.cameltech.easrip.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cameltech.easrip.Model.Arsip;
import com.cameltech.easrip.Model.User;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TambahDokumenActivity extends AppCompatActivity implements View.OnClickListener {

    EditText tvEmail, tvNamaDok, tvNoDok, tvThnDok, tvDeskripsi, hsl, tvjnsArsip, tvmapArsip,tvNamaMap,tvTanggal;
    TextView tvProses, tvUri, tvNamaFile;
    Button simpan, upload;
    ImageButton btnCalender;
    String hasil;
    private static final int PICK_PDF_REQUEST1 = 86;
    Uri uriPdf;
    String filePdf;
    public String status = "0";
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    String alias;
    private DatabaseReference databaseReference;
    private ActionBar actionBar;
    private static final String TAG = "TambahDokumenActivity";
    Calendar newCalendar;
    private DatePickerDialog.OnDateSetListener datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_dokumen);
        initToolbar();

        tvEmail = (EditText) findViewById(R.id.email);
        tvNamaDok = (EditText) findViewById(R.id.nama);
        tvNoDok = (EditText) findViewById(R.id.nomorDok);
        tvThnDok = (EditText) findViewById(R.id.thnDok);
        tvDeskripsi = (EditText) findViewById(R.id.edket);
        hsl = (EditText) findViewById(R.id.hsl);
        tvjnsArsip = (EditText) findViewById(R.id.jenisArsip);
        tvmapArsip = (EditText) findViewById(R.id.boxArsip);
        tvNamaMap = (EditText) findViewById(R.id.namaBoxArsip);
        tvTanggal = (EditText) findViewById(R.id.tvTanggal);
        btnCalender = (ImageButton) findViewById(R.id.btnCalender);

        tvProses = (TextView) findViewById(R.id.proses);
        tvUri = (TextView) findViewById(R.id.uri1);
        tvNamaFile = (TextView) findViewById(R.id.namafile);

        simpan = (Button) findViewById(R.id.simpan);
        upload = (Button) findViewById(R.id.file);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        databaseReference = FirebaseDatabase.getInstance().getReference();

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
                new DatePickerDialog(TambahDokumenActivity.this,datePickerDialog,newCalendar.get(Calendar.YEAR)
                ,newCalendar.get(Calendar.MONTH),newCalendar.get(Calendar.DAY_OF_MONTH)).show();
                tvNamaDok.requestFocus();
            }
        });

        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(TambahDokumenActivity.this,datePickerDialog,newCalendar.get(Calendar.YEAR)
                ,newCalendar.get(Calendar.MONTH),newCalendar.get(Calendar.DAY_OF_MONTH)).show();
                tvNamaDok.requestFocus();
            }
        });

        upload.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        alias = user.getEmail();
        tvEmail.setText(alias);

        final Arsip arsip = (Arsip) getIntent().getSerializableExtra("data");

        if (arsip != null) {
            tvEmail.setText(arsip.getEmail());
            tvNoDok.setText(arsip.getNoDok());
            tvNamaDok.setText(arsip.getNamaDok());
            tvTanggal.setText(arsip.getTglUpload());
            tvThnDok.setText(arsip.getThnDokumen());
            tvDeskripsi.setText(arsip.getDeskripsi());
            tvUri.setText(arsip.getUriDok());
            tvjnsArsip.setText(arsip.getJenis());
            tvmapArsip.setText(arsip.getMapArsip());
            simpan.setText("Ubah Data");
            simpan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arsip.setEmail(tvEmail.getText().toString());
                    arsip.setNamaDok(tvNamaDok.getText().toString());
                    arsip.setDeskripsi(tvDeskripsi.getText().toString());
                    arsip.setNoDok(tvNoDok.getText().toString());
                    arsip.setTglUpload(tvTanggal.getText().toString());
                    arsip.setThnDokumen(tvThnDok.getText().toString());
                    arsip.setJenis(tvjnsArsip.getText().toString());
                    arsip.setUriDok(tvUri.getText().toString());
                    arsip.setMapArsip(tvmapArsip.getText().toString());
                    updateData(arsip);
                }
            });
        } else {

            simpan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEmpty(tvNamaDok.getText().toString()) && !isEmpty(tvNoDok.getText().toString())
                            && !isEmpty(tvThnDok.getText().toString()) && !isEmpty(tvDeskripsi.getText().toString())) {
                        tambahBerkas(new Arsip(tvEmail.getText().toString(), tvNamaDok.getText().toString(), tvNoDok.getText().toString()
                                , tvThnDok.getText().toString(), tvTanggal.getText().toString(), tvUri.getText().toString(), tvjnsArsip.getText().toString()
                                , tvDeskripsi.getText().toString(), tvmapArsip.getText().toString(),tvNamaMap.getText().toString()));
                    } else
                        Snackbar.make(findViewById(R.id.simpan), "Data arsip tidak boleh kosong", Snackbar.LENGTH_LONG).show();

                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(
                            tvEmail.getWindowToken(), 0);
                }
            });

        }

    }

    private void updateData(Arsip arsip) {
        databaseReference.child("arsip").child(arsip.getKey()).setValue(arsip)
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
        actionBar.setTitle("Tambah Arsip");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }

    private void setTanggal() {
        String format = "dd - MMM - yyyy";
        Locale localeID = new Locale("id", "ID");
        SimpleDateFormat dF = new SimpleDateFormat(format, localeID);
        tvTanggal.setText(dF.format(newCalendar.getTime()));

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


    private void tambahBerkas(Arsip arsip) {

        databaseReference.child("arsip").push().setValue(arsip).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                tvNamaDok.setText("");
                tvNoDok.setText("");
                tvThnDok.setText("");
                tvDeskripsi.setText("");
                Snackbar.make(findViewById(R.id.simpan), "Data berhasil ditambahkan", Snackbar.LENGTH_LONG).show();
            }
        });

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
        return new Intent(activity, TambahDokumenActivity.class);
    }
}
