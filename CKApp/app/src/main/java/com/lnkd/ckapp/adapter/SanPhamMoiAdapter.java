package com.lnkd.ckapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lnkd.ckapp.ChiTietActivity;
import com.lnkd.ckapp.Interface.ItemClickListener;
import com.lnkd.ckapp.R;
import com.lnkd.ckapp.model.EventBus.SuaXoaEvent;
import com.lnkd.ckapp.model.SanPhamMoi;
import com.lnkd.ckapp.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class SanPhamMoiAdapter extends RecyclerView.Adapter<SanPhamMoiAdapter.MyViewHolder> {
    Context context;
    List<SanPhamMoi> array;

    public SanPhamMoiAdapter(Context context, List<SanPhamMoi> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sp_moi, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SanPhamMoi sanPhamMoi = array.get(position);
        holder.txtten.setText(sanPhamMoi.getTensp());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtgiagoc.setText( decimalFormat.format(Double.parseDouble(sanPhamMoi.getGiagoc())) + "đ");
        holder.txtgiagoc.setPaintFlags(holder.txtgiagoc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.txtgia.setText( decimalFormat.format(Double.parseDouble(sanPhamMoi.getGiasp())) + "đ");
        Glide.with(context).load(sanPhamMoi.getHinhanh()).into(holder.imghinhanh);
        if (sanPhamMoi.getHinhanh().contains("https")){
            Glide.with(context).load(sanPhamMoi.getHinhanh()).into(holder.imghinhanh);
        } else {
            String hinh = Utils.BASE_URL+"images/" + sanPhamMoi.getHinhanh();
            Glide.with(context).load(hinh).into(holder.imghinhanh);
        }
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(!isLongClick){
                    //CLick
                    Intent intent = new Intent(context, ChiTietActivity.class);
                    intent.putExtra("chitiet", sanPhamMoi);
                    context.startActivity(intent);
                } else {
                    EventBus.getDefault().postSticky(new SuaXoaEvent(sanPhamMoi));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return array.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, View.OnLongClickListener {
        TextView txtten, txtgia, txtgiagoc;
        ImageView imghinhanh;
        private ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtgiagoc = itemView.findViewById(R.id.itemsp_giagoc);;
            txtgia = itemView.findViewById(R.id.itemsp_gia);
            txtten = itemView.findViewById(R.id.itemsp_ten);
            imghinhanh = itemView.findViewById(R.id.itemsp_image);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(0, 0, getAdapterPosition(), "Sửa");
            contextMenu.add(0, 0, getAdapterPosition(), "Xoá");

        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return false;
        }
    }
}
