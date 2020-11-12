package com.cameltech.easrip.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cameltech.easrip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sdsmdg.tastytoast.TastyToast;

public class ChangePasswordActivity extends AppCompatActivity {
    private static final String TAG = "ChangePasswordActivity";
    private TextInputEditText newPass, oldPass, confirmPass;
    private Button changePass, batal;
    TextView email;
    private ProgressBar progressBar;
    private FirebaseUser user;
    private String getPassword;
    FirebaseAuth firebaseAuth;
    String alias, old, getAlias;
    public static final String DATA = "ChangePasswordActivity";
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        newPass = (TextInputEditText) findViewById(R.id.edChangePass);
        oldPass = (TextInputEditText) findViewById(R.id.edOldPass);
        confirmPass = (TextInputEditText) findViewById(R.id.confirmPass);
        changePass = (Button) findViewById(R.id.btnChange);
        batal = (Button) findViewById(R.id.btnCancel);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        email = (TextView) findViewById(R.id.email);
        progressBar.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        initToolbar();

        Intent i = getIntent();
        getAlias = i.getStringExtra(PengaturanPenggunaActivity.DATA);
        email.setText(getAlias);

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNewPass();
            }
        });
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePasswordActivity.this, PengaturanPenggunaActivity.class));
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Ubah Kata Sandi");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                ;
            }
        });

    }

    private void changeNewPass() {

        if (TextUtils.isEmpty(oldPass.getText().toString())) {
            TastyToast.makeText(getApplicationContext(), "Masukan Kata Sandi Lama", TastyToast.LENGTH_LONG, TastyToast.WARNING);
            oldPass.requestFocus();
        } else if (TextUtils.isEmpty(newPass.getText().toString())) {
            TastyToast.makeText(getApplicationContext(), "Masukan Kata Sandi Baru", TastyToast.LENGTH_LONG, TastyToast.WARNING);
            newPass.requestFocus();
        } else if (TextUtils.isEmpty(confirmPass.getText().toString())) {
            TastyToast.makeText(getApplicationContext(), "Masukan Konfirmasi Kata Sandi", TastyToast.LENGTH_LONG, TastyToast.WARNING);
            confirmPass.requestFocus();
        } else {

            progressBar.setVisibility(View.VISIBLE);

            alias = user.getEmail();
            old = oldPass.getText().toString();
            getPassword = newPass.getText().toString();
            if (user != null) {
                AuthCredential authCredential = EmailAuthProvider
                        .getCredential(getAlias, old);

                user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (getPassword.equals(confirmPass.getText().toString())) {
                                user.updatePassword(getPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            TastyToast.makeText(getApplicationContext(), "Berhasil Di ubah", TastyToast.LENGTH_LONG, TastyToast.INFO);
                                            startActivity(new Intent(getApplicationContext(), LoginActivity.class).putExtra(DATA, alias));
                                            firebaseAuth.signOut();
                                            finish();
                                            Toast.makeText(getApplicationContext(), "Silahkan Login Kemabli", Toast.LENGTH_LONG).show();
                                        }
                                        if (!task.isSuccessful())
                                            Toast.makeText(ChangePasswordActivity.this, "Kata sandi harus 8 Karakter / Lebih, Kombinasi Huruf dan Angka", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                TastyToast.makeText(getApplicationContext(), "Kata Sandi Baru Tidak Sama", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                            }

                        }
                        if (!task.isSuccessful()) {
                            TastyToast.makeText(getApplicationContext(), "Kata Sandi Lama Salah", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });


            }
        }
    }

}

