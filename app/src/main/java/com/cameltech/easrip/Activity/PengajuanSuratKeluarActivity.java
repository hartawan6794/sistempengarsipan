package com.cameltech.easrip.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cameltech.easrip.Model.SuratKeluar;
import com.cameltech.easrip.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sdsmdg.tastytoast.TastyToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PengajuanSuratKeluarActivity extends AppCompatActivity {

    EditText tvEmail, tvNamaDok, tvNoDok, tvDeskripsi, tvInstansi, tvAlamatInstansi;
    EditText tvTanggal, tvLampiran, tvPerihal;
    TextView tvProses;
    Button simpan, upload;
    ImageButton btnCalender;
    String hasil;
    ProgressBar progressBar;
    private DatabaseReference databaseReference;
    FirebaseAuth auth;
    private ActionBar actionBar;
    private DatePickerDialog.OnDateSetListener datePickerDialog;
    Calendar newCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan_surat_keluar);

        tvEmail = (EditText) findViewById(R.id.email);
        tvNamaDok = (EditText) findViewById(R.id.nama);
        tvNoDok = (EditText) findViewById(R.id.nomorDok);
        tvDeskripsi = (EditText) findViewById(R.id.edket);
        tvProses = (TextView) findViewById(R.id.proses);
        tvInstansi = (EditText) findViewById(R.id.edInstansi);
        tvAlamatInstansi = (EditText) findViewById(R.id.edAlamatInstansi);
        tvPerihal = (EditText) findViewById(R.id.edPerihal);
        tvLampiran = (EditText) findViewById(R.id.edLampiran);
        tvTanggal = (EditText) findViewById(R.id.tvTanggal);

        btnCalender = (ImageButton) findViewById(R.id.btnCalender);
        simpan = (Button) findViewById(R.id.simpan);
        upload = (Button) findViewById(R.id.file);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        FirebaseUser user = auth.getCurrentUser();
        hasil = user.getEmail();
        tvEmail.setText(hasil);

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

                new DatePickerDialog(PengajuanSuratKeluarActivity.this, datePickerDialog, newCalendar.get(Calendar.YEAR)
                        , newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show();
                tvNoDok.requestFocus();
            }
        });

        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(PengajuanSuratKeluarActivity.this, datePickerDialog, newCalendar.get(Calendar.YEAR)
                        , newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show();
                tvNoDok.requestFocus();
            }
        });


        final SuratKeluar arsip = (SuratKeluar) getIntent().getSerializableExtra("data");

        if (arsip != null) {
            tvEmail.setText(arsip.getEmail());
            tvEmail.setEnabled(false);
            tvNoDok.setText(arsip.getNo());
            tvNoDok.setEnabled(false);
            tvInstansi.requestFocus();
            tvInstansi.setText(arsip.getInstansi());
            tvAlamatInstansi.setText(arsip.getAlamatInstansi());
            tvPerihal.setText(arsip.getPerihal());
            tvLampiran.setText(arsip.getLampiran());
            tvTanggal.setText(arsip.getTanggal());
            tvDeskripsi.setText(arsip.getDeskripsi());
            simpan.setText("Ubah Data");
            simpan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arsip.setEmail(tvEmail.getText().toString());
                    arsip.setInstansi(tvInstansi.getText().toString());
                    arsip.setAlamatInstansi(tvInstansi.getText().toString());
                    arsip.setPerihal(tvPerihal.getText().toString());
                    arsip.setLampiran(tvLampiran.getText().toString());
                    arsip.setDeskripsi(tvDeskripsi.getText().toString());
                    arsip.setNo(tvNoDok.getText().toString());
                    arsip.setTanggal(tvTanggal.getText().toString());
                    updateData(arsip);
                }
            });
        } else {

            simpan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEmpty(tvNoDok.getText().toString()) && !isEmpty(tvDeskripsi.getText().toString())) {
                        tambahBerkas(new SuratKeluar(tvEmail.getText().toString(), tvNoDok.getText().toString(), tvInstansi.getText().toString()
                                , tvAlamatInstansi.getText().toString(), tvLampiran.getText().toString(), tvPerihal.getText().toString(),
                                tvTanggal.getText().toString(),tvDeskripsi.getText().toString(),"0"));
                    } else
                        Snackbar.make(findViewById(R.id.simpan), "Data arsip tidak boleh kosong", Snackbar.LENGTH_LONG).show();

                }
            });

        }


        initToolbar();
    }

    private void updateData(SuratKeluar arsip) {
        databaseReference.child("SuratKeluar").child(arsip.getKey()).setValue(arsip)
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
        actionBar.setTitle("Form Ajuan Surat Keluar");
    }


    private void tambahBerkas(SuratKeluar arsip) {

        databaseReference.child("SuratKeluar").push().setValue(arsip).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                tvNamaDok.setText("");
                tvNoDok.setText("");
                tvDeskripsi.setText("");
                tvInstansi.setText("");
                TastyToast.makeText(getApplicationContext(), "Surat Berhasil di Upload", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
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


    public static Intent getActIntent(Activity activity) {
        return new Intent(activity, PengajuanSuratKeluarActivity.class);
    }
}
