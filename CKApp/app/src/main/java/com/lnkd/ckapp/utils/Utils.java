package com.lnkd.ckapp.utils;

import com.lnkd.ckapp.model.GioHang;
import com.lnkd.ckapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String BASE_URL="http://10.252.6.6:8080/banhang/";

    public static List<GioHang> manggiohang;
    public static List<GioHang> mangmuahang = new ArrayList<>();
    public static User user_current = new User();
}
