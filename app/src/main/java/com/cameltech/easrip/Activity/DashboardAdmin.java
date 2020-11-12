package com.cameltech.easrip.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cameltech.easrip.Model.User;
import com.cameltech.easrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardAdmin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    TextView tvUsrname, tvMail, tvJmlhUser, tvJmlhFile, tvUserLvl;
    TextView tvJumlahSuratMasuk, tvJumlahSuratKeluar;
    CardView btnPengguna, btnArsip;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String alias;
    private ActionBar actionBar;
    public static final String DATA = "DashboardAdmin";
    String userAlias;

    String[] permissions = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int REQUEST_MULTIPLE_PERMISSIONS = 117;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Dashboard Aplikasi");
        checkPermissions();

        tvUsrname = (TextView) findViewById(R.id.tvAdmin);
        tvJmlhFile = (TextView) findViewById(R.id.tvJmlahArsip);
        tvJumlahSuratMasuk = (TextView) findViewById(R.id.tvJmlahSuratMasuk);
        tvJumlahSuratKeluar = (TextView) findViewById(R.id.tvJmlahSuratKeluar);
        tvJmlhUser = (TextView) findViewById(R.id.tvJmlUser);
        tvUserLvl = (TextView) findViewById(R.id.tvUserLvl);
        btnPengguna = (CardView) findViewById(R.id.btnCounrPengguna);
        btnArsip = (CardView) findViewById(R.id.btnCounrArsip);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);
        tvMail = view.findViewById(R.id.tvEmail);
        final CircleImageView civ = view.findViewById(R.id.imageView);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        alias = user.getEmail();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.orderByChild("email").equalTo(alias).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);

                    tvUsrname.setText("Hallo, " + user.getUsername());
                    userAlias = user.getUsername();
                    tvMail.setText(userAlias);
                    Picasso.get().load(user.getUserImage()).placeholder(R.drawable.profile).into(civ);
                    if (user.getUserLvl().equals("kbg")) {
                        navigationView.getMenu().clear();
                        navigationView.inflateMenu(R.menu.activity_dashboard_predivisi_drawer);
                    }
                    if (user.getUserLvl().equals("sek") || user.getUserLvl().equals("dirut")) {
                        navigationView.getMenu().clear();
                        navigationView.inflateMenu(R.menu.activity_dashboard_sekertaris_drawer);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btnPengguna.setOnClickListener(this);

        btnArsip.setOnClickListener(this);

        countArsips();
        countUsers();


    }

    private void countUsers() {
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int size = (int) dataSnapshot.getChildrenCount() - 1;
                tvJmlhUser.setText(String.valueOf(size));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void countArsips() {
        databaseReference.child("arsip").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int size = (int) dataSnapshot.getChildrenCount();
                tvJmlhFile.setText(String.valueOf(size));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("SuratMasuk").orderByChild("kategori").equalTo("1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int size = (int) dataSnapshot.getChildrenCount();
                tvJumlahSuratMasuk.setText(String.valueOf(size));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("SuratMasuk").orderByChild("kategori").equalTo("2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int size = (int) dataSnapshot.getChildrenCount();
                tvJumlahSuratKeluar.setText(String.valueOf(size));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            startActivity(new Intent(getApplicationContext(), DashboardAdmin.class));
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);

        } else if (id == R.id.nav_pengguna) {
            startActivity(new Intent(DBReadPenggunaActivity.getActIntent(this)));

        } else if (id == R.id.nav_arsip) {
            if (alias.equals("amel.021297@gmail.com") || alias.equals("feri.budianto@gmail.com")) {
                startActivity(new Intent(DBReadArsipActivity.getActIntent((Activity) this)));
            } else {
                startActivity(new Intent(DBReadArsipPenggunaActivity.getActIntent((Activity) this)));
            }

        } else if (id == R.id.nav_umum) {
            startActivity(new Intent(getApplicationContext(), TambahDokumenActivity.class));

        } else if (id == R.id.nav_masuk) {
            startActivity(new Intent(getApplicationContext(), SuratMasukActivity.class).putExtra(DATA, "1"));

        } else if (id == R.id.nav_keluar) {
            startActivity(new Intent(getApplicationContext(), SuratMasukActivity.class).putExtra(DATA, "2"));

        } else if (id == R.id.nav_tentang) {
            startActivity(new Intent(getApplicationContext(), TentangActivity.class));

        } else if (id == R.id.nav_ajuan) {
            startActivity(new Intent(getApplicationContext(), PengajuanSuratKeluarActivity.class));
        } else if (id == R.id.nav_pengajuan) {
            if (alias.equals("amel.021297@gmail.com") || alias.equals("feri.budianto@gmail.com")) {
                startActivity(new Intent(DaftarPengajuanSuratKeluar.getActIntent((Activity) this)));
            } else {
                startActivity(new Intent(DaftarPengajuanSuratKeluarPengguna.getActIntent((Activity) this)));
            }
        } else if (id == R.id.nav_pembuatan) {
            if (alias.equals("amel.021297@gmail.com") || alias.equals("feri.budianto@gmail.com")) {
                startActivity(new Intent(DaftarPembuatanSuratKeluarActivity.getActIntent((Activity) this)));
            } else {
                startActivity(new Intent(DaftarPembuatanSuratKeluarPenggunaActivity.getActIntent((Activity) this)));
            }
        } else if (id == R.id.nav_exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setIcon(R.drawable.foldeer);
            builder.setTitle("E-Arsip Kpps Bimu Lampung");
            builder.setMessage("Anda Ingin Keluar Akun?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(getApplicationContext(), PengaturanPenggunaActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCounrArsip:
                if (alias.equals("amel.021297@gmail.com") || alias.equals("feri.budianto@gmail.com")) {
                    startActivity(new Intent(DBReadArsipActivity.getActIntent((Activity) this)));
                } else {
                    startActivity(new Intent(DBReadArsipPenggunaActivity.getActIntent((Activity) this)));
                }
                break;
            case R.id.btnCounrPengguna:
                if (userAlias.equals("Admin")||userAlias.equals("Staff SDI")) {
                    startActivity(new Intent(DBReadPenggunaActivity.getActIntent(this)));
                } else {
                    TastyToast.makeText(getApplicationContext(), "Tidak Memiliki Akses", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                }
                break;
        }
    }
}
