package com.cameltech.easrip.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cameltech.easrip.Activity.DaftarPengajuanSuratKeluar;
import com.cameltech.easrip.Activity.DtlSuratKeluarAcitivy;
import com.cameltech.easrip.Activity.PengajuanSuratKeluarActivity;
import com.cameltech.easrip.Model.SuratKeluar;
import com.cameltech.easrip.R;

import java.util.ArrayList;

public class AdapterDaftarSuratKeluar extends RecyclerView.Adapter<AdapterDaftarSuratKeluar.ViewHolder> {

    ArrayList<SuratKeluar> daftarArsip;
    FirebaseDataListener listener;
    Context context;
    CardView kontener;
    public static final String DATA = "AdapterSuratKeluar";

    public AdapterDaftarSuratKeluar(Context ctx, ArrayList<SuratKeluar> suratKeluars) {
        context = ctx;
        daftarArsip = suratKeluars;
        listener = (DaftarPengajuanSuratKeluar) ctx;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView namaArsip, tvTanggal, tvStatus;

        ViewHolder(View v) {
            super(v);

            namaArsip = v.findViewById(R.id.tvNmaDokumen);
            tvTanggal = v.findViewById(R.id.tvTanggal);
            kontener = (CardView) v.findViewById(R.id.kontener);
            tvStatus = v.findViewById(R.id.tvStatus);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daftar_surat_keluar, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final String nama = daftarArsip.get(position).getInstansi();
        String tgl = daftarArsip.get(position).getTanggal();
        final String key = daftarArsip.get(position).getKey();
        String status = daftarArsip.get(position).getStatusValid();
        if (status.equals("0")) {
            holder.tvStatus.setText("Menunggu File Diupload");
        }
        if (status.equals("1")) {
            holder.tvStatus.setText("File Telah Diupload. Menunggu Persetujuan !");
        }

        holder.namaArsip.setText(nama);
        holder.tvTanggal.setText(tgl);

        kontener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(DtlSuratKeluarAcitivy.getActIntent((Activity) context).putExtra(DATA, holder.namaArsip.getText()));
            }
        });

        kontener.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_view);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setTitle("Pilih Aksi");
                dialog.show();

                Button editBtn = (Button) dialog.findViewById(R.id.bt_edit_data);
                Button delleteBtn = (Button) dialog.findViewById(R.id.bt_delete_data);

                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        context.startActivity(PengajuanSuratKeluarActivity.getActIntent((Activity) context).putExtra("data", daftarArsip.get(position)));
                    }
                });
                delleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        listener.onDeleteData(daftarArsip.get(position), position);
                    }
                });
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return daftarArsip.size();
    }

    public interface FirebaseDataListener {
        void onDeleteData(SuratKeluar arsip, int position);
    }

}
