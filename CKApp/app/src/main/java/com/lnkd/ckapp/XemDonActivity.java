package com.lnkd.ckapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lnkd.ckapp.R;
import com.lnkd.ckapp.adapter.DonHangAdapter;
import com.lnkd.ckapp.model.DonHang;
import com.lnkd.ckapp.model.EventBus.DonHangEvent;
import com.lnkd.ckapp.model.NotiSendData;
import com.lnkd.ckapp.retrofit.ApiBanHang;
import com.lnkd.ckapp.retrofit.ApiPushNofication;
import com.lnkd.ckapp.retrofit.RetrofitClient;
import com.lnkd.ckapp.retrofit.RetrofitClientNoti;
import com.lnkd.ckapp.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class XemDonActivity extends AppCompatActivity {
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    RecyclerView redonhang;
    Toolbar toolbar;
    DonHang donHang;
    int tinhtrang;
    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_don);
        initView();
        initToolbar();
        getOrder();

    }

    private void getOrder() {
        compositeDisposable.add(apiBanHang.xemDonHang(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            DonHangAdapter adapter = new DonHangAdapter(getApplicationContext(), donHangModel.getResult());
                            redonhang.setAdapter(adapter);
                        },
                        throwable -> {

                        }

                ));

    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        redonhang = findViewById(R.id.recycleview_donhang);
        toolbar = findViewById(R.id.toolbar);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        redonhang.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }



    private void showCustomDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_donhang, null);
        Spinner spinner = view.findViewById(R.id.spinner_dialog);
        AppCompatButton btndongy = view.findViewById(R.id.dongy_dialog);
        List<String> list = new ArrayList<>();
        list.add("Đơn hàng đang được xử lí");
        list.add("Đơn hàng đã được chấp nhận");
        list.add("Đơn hàng đã giao cho đơn vị vận chuyển");
        list.add("Đã hoàn thành đơn hàng");
        list.add("Đơn hàng đã hủy");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
        spinner.setSelection(donHang.getTrangthai());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tinhtrang = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btndongy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capNhatDonHang();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

    }

    private void capNhatDonHang() {
        compositeDisposable.add(apiBanHang.updateOrder(donHang.getId(), tinhtrang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            getOrder();
                            dialog.dismiss();
                            pushNotiToUser();
                        },
                        throwable -> {

                        }
                ));
    }
    private void pushNotiToUser() {
        //gettoken
        compositeDisposable.add(apiBanHang.gettoken(0,donHang.getIduser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()){
                                for(int i = 0; i<userModel.getResult().size();i++){
                                    Map<String, String> data = new HashMap<>();
                                    data.put("title", "thong bao");
                                    data.put("body", trangThaiDon(tinhtrang));
                                    NotiSendData notiSendData = new NotiSendData(userModel.getResult().get(i).getToken(), data);
                                    ApiPushNofication apiPushNofication = RetrofitClientNoti.getInstance().create(ApiPushNofication.class);
                                    compositeDisposable.add(apiPushNofication.sendNofitication(notiSendData)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    notiRespone -> {

                                                    },
                                                    throwable -> {
                                                        Log.d("logg", throwable.getMessage());
                                                    }
                                            ));
                                }
                            }
                        },throwable -> {
                            Log.d("loggg", throwable.getMessage());
                        }
                ));
    }

    private String trangThaiDon(int status){
        String result = "";
        switch (status){
            case 0:
                result = "Đơn hàng đang được xử lí";
                break;
            case 1:
                result = "Đơn hàng đã được chấp nhận";
                break;
            case 2:
                result = "Đơn hàng đã giao cho đơn vị vận chuyển";
                break;
            case 3:
                result = "Đã hoàn thành đơn hàng";
                break;
            case 4:
                result = "Đơn hàng đã hủy";
                break;
        }

        return result;
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void evenDonHang(DonHangEvent event){
        if(event != null){
            donHang = event.getDonHang();
            showCustomDialog();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}