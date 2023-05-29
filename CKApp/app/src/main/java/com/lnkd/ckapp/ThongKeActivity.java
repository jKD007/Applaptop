package com.lnkd.ckapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lnkd.ckapp.retrofit.ApiBanHang;
import com.lnkd.ckapp.retrofit.RetrofitClient;
import com.lnkd.ckapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThongKeActivity extends AppCompatActivity {

    Toolbar toolbar;
    PieChart pieChart;
    BarChart barChart;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);

        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        initView();
        ActionToolBar();
        getdataChart();
    }

    private void getdataChart() {
        List<PieEntry> listdata = new ArrayList<>();
        compositeDisposable.add(apiBanHang.getthongke()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        thongKeModel -> {
                            if(thongKeModel.isSuccess()){

                                for(int i = 0; i<thongKeModel.getResult().size(); i++){
                                    String tensp = thongKeModel.getResult().get(i).getTensp();
                                    int tong = thongKeModel.getResult().get(i).getTong();
                                    listdata.add(new PieEntry(tong, tensp));
                                }
                                PieDataSet pieDataSet = new PieDataSet(listdata, "Thống kê");
                                PieData data = new PieData();
                                data.setDataSet(pieDataSet);
                                data.setValueTextSize(10f);
                                data.setValueFormatter(new PercentFormatter());
                                pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                                pieChart.setData(data);
                                pieChart.animateXY(2000,2000);
                                pieChart.setUsePercentValues(true);
                                pieChart.getDescription().setEnabled(false);
                                pieChart.invalidate();

                            }
                        },
                        throwable -> {
                            Log.d("logg", throwable.getMessage());
                        }
                ));
    }



    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        pieChart = findViewById(R.id.piechart);
        barChart = findViewById(R.id.barchart);
    }


    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}