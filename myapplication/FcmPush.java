package com.example.myapplication;

import com.example.myapplication.model.MyToken;
import com.example.myapplication.model.PushDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FcmPush {
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    String url = "https://fcm.googleapis.com/fcm/send";
    String serverKey =
            "AAAAwuo6KJk:APA91bE0f8tyzSPDnPjQubaz9Eqa-LnsYdblIzOeh0GaEyM12_dnBbjX1UnC1WSIRxQBKYsYSK12Dqi09fA9XFH-KoLTI_yefWT6BVVK7WsnuSAJ4R4tdwCbFcKUjUoZnnGt0htIhm_f";

    OkHttpClient okHttpClient;
    Gson gson;

    public FcmPush(){
        okHttpClient = new OkHttpClient();
        gson = new Gson();
    }

    public void sendMessage(String destinationUid, final String title, final String message){
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        MyToken myToken = documentSnapshot.toObject(MyToken.class);

                        String token = myToken.pushtoken;
                        PushDTO pushDTO = new PushDTO();
                        pushDTO.to = token;
                        pushDTO.notification.title = title;
                        pushDTO.notification.body = message;

                        RequestBody body = RequestBody.create(JSON, gson.toJson(pushDTO));
                        Request request = new Request.Builder().addHeader("Content-Type", "application/json")
                                .addHeader("Authorization", "key="+serverKey)
                                .url(url)
                                .post(body)
                                .build();

                        okHttpClient.newCall(request).enqueue(new Callback(){
                            @Override
                            public void onFailure(Call call, IOException e) {
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                            }
                        });
                    }
                });

    }

}
