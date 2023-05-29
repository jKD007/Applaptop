package com.lnkd.ckapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lnkd.ckapp.retrofit.ApiBanHang;
import com.lnkd.ckapp.retrofit.RetrofitClient;
import com.lnkd.ckapp.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ResetPassActivity extends AppCompatActivity {
    EditText email;
    AppCompatButton btnreset;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        initView();
        initControl();
    }

    private void initControl() {
        btnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_email = email.getText().toString().trim();
                if(TextUtils.isEmpty(str_email)){
                    email.setError("Email trống!");
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    //reset pass on firebase
                    FirebaseAuth.getInstance().sendPasswordResetEmail(str_email).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Vui lòng kiểm tra Email của bạn để tiếp tục", Toast.LENGTH_LONG).show();
                        }
                        finish();
                    });

                }
            }
        });
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        email = findViewById(R.id.edtresetpass);
        btnreset = findViewById(R.id.btnresetpass);
        progressBar = findViewById(R.id.progressbar);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}