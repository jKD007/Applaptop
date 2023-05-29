package com.lnkd.ckapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lnkd.ckapp.retrofit.ApiBanHang;
import com.lnkd.ckapp.retrofit.RetrofitClient;
import com.lnkd.ckapp.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangKiActivity extends AppCompatActivity {
    EditText email, pass, repass, mobile, username;
    AppCompatButton button, btndangnhap2;
    ApiBanHang apiBanHang;
    FirebaseAuth firebaseAuth;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    String passwordPattern = "^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\\d]){1,})(?=(.*[\\W]){1,})(?!.*\\s).{6,20}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki);
        initView();
        initControl();
    }

    private void initControl() {


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dangKi();
            }
        });
    }

    private void dangKi() {
        String str_emai = email.getText().toString().trim();
        String str_pass = pass.getText().toString().trim();
        String str_repass = repass.getText().toString().trim();
        String str_mobile = mobile.getText().toString().trim();
        String str_user = username.getText().toString().trim();

        if (TextUtils.isEmpty(str_emai)) {
            email.setError("Email trống!");
        } else if (TextUtils.isEmpty(str_user)) {
            username.setError("Username trống!");
        } else if (TextUtils.isEmpty(str_pass)) {
            pass.setError("Password trống!");
        }else if (!str_pass.matches(passwordPattern)) {
            pass.setError("Mật khẩu phải chứa ít nhất 8 kí tự (gồm 1 kí tự thường, 1 kí tự viết hoa và 1 chữ số và 1 kí tự đặc biệt)");
        } else if (TextUtils.isEmpty(str_repass)) {
            repass.setError("Re-Password trống!");
        } else if (TextUtils.isEmpty(str_mobile)) {
            mobile.setError("Mobile trống!");
        } else {
            if (str_pass.equals(str_repass)) {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(str_emai, str_pass)
                        .addOnCompleteListener(DangKiActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user != null) {
                                        postData(str_emai, str_pass, str_user, str_mobile, user.getUid());
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Email đã tồn tại hoặc không thành công", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Pass chưa khớp", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void postData (String str_emai, String str_pass, String str_user, String str_mobile, String uid ){
            //post data
            compositeDisposable.add(apiBanHang.dangKi(str_emai, str_pass, str_user, str_mobile, uid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            userModel -> {
                                if (userModel.isSuccess()) {
                                    Toast.makeText(getApplicationContext(), "Đăng kí thành công!", Toast.LENGTH_LONG).show();
                                    Utils.user_current.setEmail(str_emai);
                                    Utils.user_current.setPass(str_pass);
                                    Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();

                            }
                    ));
        }

        private void initView () {
            apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

            email = findViewById(R.id.email);
            pass = findViewById(R.id.pass);
            repass = findViewById(R.id.repass);
            mobile = findViewById(R.id.mobile);
            username = findViewById(R.id.username);
            button = findViewById(R.id.btndangki);
            //btndangnhap2 = findViewById(R.id.btndangnhap2);
        }

        @Override
        protected void onDestroy () {
            compositeDisposable.clear();
            super.onDestroy();
        }
    }
