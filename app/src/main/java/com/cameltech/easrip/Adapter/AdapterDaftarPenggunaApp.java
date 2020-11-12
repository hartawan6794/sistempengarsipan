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

import com.cameltech.easrip.Activity.DBReadPenggunaActivity;
import com.cameltech.easrip.Activity.TambahDokumenActivity;
import com.cameltech.easrip.Activity.TambahPenggunaActivity;
import com.cameltech.easrip.Model.Arsip;
import com.cameltech.easrip.Model.User;
import com.cameltech.easrip.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterDaftarPenggunaApp extends RecyclerView.Adapter<AdapterDaftarPenggunaApp.ViewHolder> {
    private static final String TAG = "AdapterDaftarPenggunaAp";
    CardView kontener;
    ArrayList<User> daftarusers;
    FirebaseDataListener listener;
    private Context ctx;

    public AdapterDaftarPenggunaApp(ArrayList<User> users,Context context){
        listener = (DBReadPenggunaActivity)context;
        daftarusers = users;
        ctx = context;
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvEmail,tvUser;
        CircleImageView civUser;

        ViewHolder(View v){
            super(v);
            kontener = (CardView) v.findViewById(R.id.kontener);
            tvEmail = (TextView) v.findViewById(R.id.tvEmailPengguna);
            tvUser = (TextView) v.findViewById(R.id.tvUserName);
            civUser = (CircleImageView) v.findViewById(R.id.imageUser);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_pengguna,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String email = daftarusers.get(position).getEmail();
        String user = daftarusers.get(position).getUsername();
        holder.tvEmail.setText(email);
        holder.tvUser.setText(user);
        Picasso.get().load(daftarusers.get(position).getUserImage())
        .placeholder(R.drawable.person).into(holder.civUser);

        kontener.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {final Dialog dialog = new Dialog(ctx);
                dialog.setContentView(R.layout.dialog_view);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setTitle("Pilih Aksi");
                dialog.show();

                Button delleteBtn =(Button) dialog.findViewById(R.id.bt_delete_data);
                Button edit = (Button)dialog.findViewById(R.id.bt_edit_data);
                edit.setVisibility(View.GONE);
                delleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        listener.onDeleteData(daftarusers.get(position),position);
                    }
                });
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return daftarusers.size();
    }


    public interface FirebaseDataListener{
        void onDeleteData(User user, int position);
    }
}
