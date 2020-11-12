package com.cameltech.easrip.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.cameltech.easrip.Model.User;
import com.cameltech.easrip.data.SettingsAPI;
import com.cameltech.easrip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    FirebaseDatabase database;
    DatabaseReference users;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;
    TextInputEditText edemail, edpswd;
    Button btnlogin;
    SettingsAPI set;
    public String getEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        edemail = (TextInputEditText) findViewById(R.id.edEmail);
        edpswd = (TextInputEditText) findViewById(R.id.edPass);
        btnlogin = (Button) findViewById(R.id.btnLogin);
        set = new SettingsAPI(this);
        loadingBar = new ProgressDialog(this);

        Intent i = getIntent();
        getEmail = i.getStringExtra(ChangePasswordActivity.DATA);
        Log.d(TAG, "Email : " + getEmail);
        edemail.setText(getEmail);


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        final String email = edemail.getText().toString();
        String pass = edpswd.getText().toString();

        if (TextUtils.isEmpty(email)) {
            edemail.setError("Masukkan Email");
            edemail.requestFocus();
        }
        if (TextUtils.isEmpty(pass)) {
            edpswd.setError("Masukkan Password");
            edpswd.requestFocus();
        }

        loadingBar.setTitle("Masuk Aplikasi");
        loadingBar.setMessage("Mohon Tunggu, Sedang Menyiapkan Akun Anda");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            set.deleteAllSettings();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                User user = ds.getValue(User.class);
                                if(user!= null) {
                                    final String usrNme = user.getUsername();
                                    final String usrId = user.getKey();
                                    set.addUpdateSettings("myId", usrId);
                                    set.addUpdateSettings("myName", usrNme);
                                }
                            }

                            startActivity(new Intent(getApplicationContext(), DashboardAdmin.class));
                            finish();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    TastyToast.makeText(getApplicationContext(), "Login Gagal", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                    loadingBar.dismiss();
                }
            }
        });
    }


    public static Intent getActIntent(Activity activity) {
        // kode untuk pengambilan Intent
        return new Intent(activity, LoginActivity.class);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.foldeer);
        builder.setTitle("E-Arsip Kpps Bimu Lampung");
        builder.setMessage("Anda Ingin Keluar?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
