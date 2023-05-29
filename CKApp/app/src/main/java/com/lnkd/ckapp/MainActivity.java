package com.lnkd.ckapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lnkd.ckapp.adapter.LoaiSpAdapter;
import com.lnkd.ckapp.adapter.SanPhamMoiAdapter;
import com.lnkd.ckapp.model.LoaiSp;
import com.lnkd.ckapp.model.SanPhamMoi;
import com.lnkd.ckapp.retrofit.ApiBanHang;
import com.lnkd.ckapp.retrofit.RetrofitClient;
import com.lnkd.ckapp.utils.Utils;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

Toolbar toolbarManHinhChinh;
ViewFlipper viewflipperManHinhChinh;
RecyclerView recycleviewManHinhChinh;
NavigationView navigationviewManHinhChinh;
ListView listviewManHinhChinh;
DrawerLayout drawerlayoutManHinhChinh;
LoaiSpAdapter loaiSpAdapter;
List<LoaiSp> mangloaisp;
CompositeDisposable compositeDisposable = new CompositeDisposable();
ApiBanHang apiBanHang;
List<SanPhamMoi> mangSpMoi;
SanPhamMoiAdapter spAdapter;
NotificationBadge badge;
FrameLayout frameLayout;
ImageView imgsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        Anhxa();
        ActionBar();
        getToken();
        ActionViewFlipper();

        if (isConnected(this)){

            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
        }else {
            Toast.makeText(getApplicationContext(), "Không có kết nối internet", Toast.LENGTH_LONG).show();
        }
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (!TextUtils.isEmpty(s)){
                            compositeDisposable.add(apiBanHang.updateToken(Utils.user_current.getId(),s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            messageModel -> {

                                            },
                                            throwable -> {
                                                Log.d("log", throwable.getMessage());
                                            }
                                    ));
                        }
                    }
                });

    }

    private void getEventClick() {
        listviewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent dienthoai = new Intent(getApplicationContext(), DienThoaiActivity.class);
                        dienthoai.putExtra("loai", 1);
                        startActivity(dienthoai);
                        break;
                    case 2:
                        Intent laptop = new Intent(getApplicationContext(), DienThoaiActivity.class);
                        laptop.putExtra("loai", 2);
                        startActivity(laptop);
                        break;
                    case 3:
                        Intent thongtin = new Intent(getApplicationContext(), ThongTinActivity.class);
                        startActivity(thongtin);
                        break;
                    case 4:
                        Intent lienhe = new Intent(getApplicationContext(), LienHeActivity.class);
                        startActivity(lienhe);
                        break;
                    case 5:
                        Intent donhang = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(donhang);
                        finish();
                        break;
                    case 6:
                        Intent quanly = new Intent(getApplicationContext(), QuanLyActivity.class);
                        startActivity(quanly);
                        break;
                    case 7:
                        Intent thongke = new Intent(getApplicationContext(), ThongKeActivity.class);
                        startActivity(thongke);
                        break;
                    case 8:
                        Paper.book().delete("user");
                        FirebaseAuth.getInstance().signOut();
                        Intent dangnhap = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(dangnhap);
                        finish();
                        break;
                }
            }
        });
    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if(sanPhamMoiModel.isSuccess()){
                                mangSpMoi = sanPhamMoiModel.getResult();
                                spAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                                recycleviewManHinhChinh.setAdapter(spAdapter);
                            }
                        }, throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối được với server"+throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaisp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if(loaiSpModel.isSuccess()){
                                //Toast.makeText(getApplicationContext(), loaiSpModel.getResult().get(0).getTensanpham(), Toast.LENGTH_LONG).show();
                                mangloaisp = loaiSpModel.getResult();
                                //khoi tao adpter

                                mangloaisp.add(new LoaiSp("Quản lý", "https://www.iconsdb.com/icons/preview/violet/manager-xxl.png"));
                                mangloaisp.add(new LoaiSp("Thống kê", "https://www.iconsdb.com/icons/preview/violet/chart-2-xxl.png"));
                                mangloaisp.add(new LoaiSp("Đăng xuất", "https://www.iconsdb.com/icons/preview/violet/account-logout-xxl.png"));
                                loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(),mangloaisp);
                                listviewManHinhChinh.setAdapter(loaiSpAdapter);
                            }
                        }
                ));
    }
    private void ActionViewFlipper() {
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://images.fpt.shop/unsafe/fit-in/1200x300/filters:quality(90):fill(white)/fptshop.com.vn/Uploads/Originals/2023/3/16/638145545567632827_F-C1_1200x300.png");
        mangquangcao.add("https://images.fpt.shop/unsafe/fit-in/1200x300/filters:quality(90):fill(white)/fptshop.com.vn/Uploads/Originals/2023/3/31/638158964819997774_F-C1_1200x300.png");
        mangquangcao.add("https://images.fpt.shop/unsafe/fit-in/1200x300/filters:quality(90):fill(white)/fptshop.com.vn/Uploads/Originals/2023/3/31/638158968388959283_F-C1_1200x300.png");

        for(int i=0;i<mangquangcao.size();i++){
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewflipperManHinhChinh.addView(imageView);
        }
        viewflipperManHinhChinh.setFlipInterval(3000);
        viewflipperManHinhChinh.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewflipperManHinhChinh.setInAnimation(slide_in);
        viewflipperManHinhChinh.setOutAnimation(slide_out);
    }

    private void ActionBar(){
        setSupportActionBar(toolbarManHinhChinh);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarManHinhChinh.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbarManHinhChinh.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerlayoutManHinhChinh.openDrawer(GravityCompat.START);
            }
        });
    }
    private void Anhxa() {
        imgsearch = findViewById(R.id.imgsearch);
        toolbarManHinhChinh = findViewById(R.id.toolbarManHinhChinh);
        viewflipperManHinhChinh = findViewById(R.id.viewflipperManHinhChinh);
        recycleviewManHinhChinh = findViewById(R.id.recycleviewManHinhChinh);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recycleviewManHinhChinh.setLayoutManager(layoutManager);
        recycleviewManHinhChinh.setHasFixedSize(true);
        navigationviewManHinhChinh = findViewById(R.id.navigationviewManHinhChinh);
        listviewManHinhChinh = findViewById(R.id.listviewManHinhChinh);
        drawerlayoutManHinhChinh = findViewById(R.id.drawerlayoutManHinhChinh);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);
        //khoi tao list
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
        if(Utils.manggiohang == null){
            Utils.manggiohang = new ArrayList<>();
        }else {
            int totalItem = 0;
            for(int i=0; i<Utils.manggiohang.size();i++){
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });

        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for(int i=0; i<Utils.manggiohang.size();i++){
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); // them quyen vao khong bi loi
        NetworkInfo mobie = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifi != null && wifi.isConnected()) || (mobie != null && mobie.isConnected())){
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}