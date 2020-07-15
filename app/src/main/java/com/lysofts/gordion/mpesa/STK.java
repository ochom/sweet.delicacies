package com.lysofts.gordion.mpesa;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.model.LNMResult;
import com.androidstudy.daraja.util.TransactionType;

public class STK {
    Daraja daraja;
    String phone_number, amount, callback_url;
    public void push(Daraja daraja, String phone, int bill_amount, String order_number){
        this.phone_number = Utils.sanitizePhoneNumber(phone);
        this.amount = String.valueOf(bill_amount);
        this.daraja = daraja;
        this.callback_url = Constants.CALLBACK_URL+order_number+"/";

        Log.d("<<MPESA>>>>>", callback_url);
        LNMExpress lnmExpress = new LNMExpress(
                Constants.BUSINESS_SHORT_CODE,
                "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",  //https://developer.safaricom.co.ke/test_credentials
                TransactionType.CustomerBuyGoodsOnline, // TransactionType.CustomerPayBillOnline  <- Apply any of these two
                amount+"",
                phone_number+"",
                "174379",
                phone_number+"",
                callback_url,
                "Godior",
                "Goods Payment"
        );

        Log.d("<<MPESA>>>>>>>", "NOW PAYING");

        daraja.requestMPESAExpress(lnmExpress,
                new DarajaListener<LNMResult>() {

                    @Override
                    public void onResult(@NonNull LNMResult lnmResult) {
                        Log.i("<<MPESA>>>>>>>", lnmResult.ResponseDescription);
                    }

                    @Override
                    public void onError(String error) {
                        Log.i("<<MPESA>>>>>>>", error);
                    }
                }
        );
    }
}
