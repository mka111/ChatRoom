package com.example.chatapp.Fragments;

import com.example.chatapp.Notifications.MyResponse;
import com.example.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAf2b3hAo:APA91bHfY_Vk5swjv7mzkx96i-c6jbSIjuC3ZNh1200xVKlZw0swg6XFtIpPKS5XMbrAkdPZMPm4AfDoDwFSErcrLqoh3zTHegCFO3UyT9PezG45DlpR-FnR9rwHp3ykPJEuHAEhZo6y"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);



}
