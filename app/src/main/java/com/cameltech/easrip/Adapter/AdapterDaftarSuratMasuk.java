package com.cameltech.easrip.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cameltech.easrip.Activity.DBReadArsipActivity;
import com.cameltech.easrip.Activity.DtlSuratMasukActivity;
import com.cameltech.easrip.Activity.SuratMasukActivity;
import com.cameltech.easrip.Model.SuratMasuk;
import com.cameltech.easrip.R;

import java.util.ArrayList;

public class AdapterDaftarSuratMasuk extends RecyclerView.Adapter<AdapterDaftarSuratMasuk.ViewHolder> {

    ArrayList<SuratMasuk> daftarArsip;
    FirebaseDataListener listener;
    Context context;
    CardView kontener;
    public static final String DATA = "AdapterDaftarArsipIOAdm";

    public AdapterDaftarSuratMasuk(Context ctx, ArrayList<SuratMasuk> arsips){
        this.context = ctx;
        this.daftarArsip = arsips;
        listener = (DBReadArsipActivity)ctx;
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

        final String nama = daftarArsip.get(position).getNamaSurat();
        String tgl = daftarArsip.get(position).getTanggal();
        final String key = daftarArsip.get(position).getKey();
        holder.tvNamaArsip.setText(nama);
        holder.tvTanggalArsip.setText(tgl);

        kontener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(DtlSuratMasukActivity.getActIntent((Activity)context).putExtra(DATA,holder.tvNamaArsip.getText()));
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

                Button editBtn =(Button) dialog.findViewById(R.id.bt_edit_data);
                Button delleteBtn =(Button) dialog.findViewById(R.id.bt_delete_data);

                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        context.startActivity(SuratMasukActivity.getActIntent((Activity)context).putExtra("data",daftarArsip.get(position)));
                    }
                });
                delleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        listener.onDeleteData(daftarArsip.get(position),position);
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

    public interface FirebaseDataListener{
        void onDeleteData(SuratMasuk arsip, int position);
    }
}
