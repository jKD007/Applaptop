package com.lnkd.ckapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lnkd.ckapp.ChiTietActivity;
import com.lnkd.ckapp.Interface.ItemClickListener;
import com.lnkd.ckapp.R;
import com.lnkd.ckapp.model.SanPhamMoi;

import java.text.DecimalFormat;
import java.util.List;

public class DienThoaiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<SanPhamMoi> array;
    private static final int VIEW_TYPE_DATA=0;
    private static final int VIEW_TYPE_LOADING=1;

    public DienThoaiAdapter(Context context, List<SanPhamMoi> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_DATA){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dienthoai, parent, false);
            return new MyViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            SanPhamMoi sanPham = array.get(position);
            myViewHolder.tensp.setText(sanPham.getTensp().trim());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            myViewHolder.giagoc.setText("Giá gốc: " + decimalFormat.format(Double.parseDouble(sanPham.getGiagoc())) + "đ");
            myViewHolder.giagoc.setPaintFlags(((MyViewHolder) holder).giagoc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);;
            myViewHolder.giasp.setText("Giá khuyến mãi: " + decimalFormat.format(Double.parseDouble(sanPham.getGiasp())) + "đ");
            myViewHolder.mota.setText(sanPham.getMota());
            Glide.with(context).load(sanPham.getHinhanh()).into(myViewHolder.hinhanh);
            myViewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    if(!isLongClick){
                        //CLick
                        Intent intent = new Intent(context, ChiTietActivity.class);
                        intent.putExtra("chitiet", sanPham);
                        context.startActivity(intent);
                    }
                }
            });
        }else{
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return array.get(position) == null ? VIEW_TYPE_LOADING:VIEW_TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        return array.size();
    }



    public class LoadingViewHolder extends RecyclerView.ViewHolder{
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressbar);
        }
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tensp, giasp, mota, idsp, giagoc;
        ImageView hinhanh;
        private ItemClickListener itemClickListener;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tensp = itemView.findViewById(R.id.itemdt_ten);
            giagoc = itemView.findViewById(R.id.itemdt_giagoc);
            giasp = itemView.findViewById(R.id.itemdt_gia);
            mota = itemView.findViewById(R.id.itemdt_mota);
            hinhanh = itemView.findViewById(R.id.itemdt_image);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
