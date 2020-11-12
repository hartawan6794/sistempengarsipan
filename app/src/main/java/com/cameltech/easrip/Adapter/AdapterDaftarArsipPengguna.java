package com.cameltech.easrip.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cameltech.easrip.Activity.DtlArsipActivity;
import com.cameltech.easrip.Model.Arsip;
import com.cameltech.easrip.R;

import java.util.ArrayList;

public class AdapterDaftarArsipPengguna extends RecyclerView.Adapter<AdapterDaftarArsipPengguna.ViewHolder> {

    ArrayList<Arsip> daftarArsip;
    Context context;
    CardView kontener;
    public static final String DATA = "AdapterDaftarPenggunaAd";

    public AdapterDaftarArsipPengguna(Context ctx, ArrayList<Arsip> arsips){
        this.context = ctx;
        this.daftarArsip = arsips;
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvNamaArsip,tvTanggalArsip;

        ViewHolder(View v){
            super(v);

            tvNamaArsip = (TextView) v.findViewById(R.id.tvNmaDokumen);
            tvTanggalArsip = (TextView) v.findViewById(R.id.tvTanggal);
            kontener = (CardView) v.findViewById(R.id.kontener);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_arsip, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final String nama = daftarArsip.get(position).getNamaDok();
        String tgl = daftarArsip.get(position).getTglUpload();
        final String key = daftarArsip.get(position).getKey();
        holder.tvNamaArsip.setText(nama);
        holder.tvTanggalArsip.setText(tgl);

        kontener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(DtlArsipActivity.getActIntent((Activity)context).putExtra(DATA,holder.tvNamaArsip.getText()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return daftarArsip.size();
    }

}
