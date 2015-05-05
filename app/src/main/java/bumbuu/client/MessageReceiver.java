package bumbuu.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.List;

import bumbuu.client.Model.Post;

public class MessageReceiver extends BroadcastReceiver {
    ArrayAdapter<?> a;
    List<Post> posts;

    public MessageReceiver(ArrayAdapter<?> a, List<Post> posts) {
        this.a = a;
        this.posts = posts;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

        String type = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(type)) {
            Bundle data = intent.getExtras();
            Log.i("Bumbuu", "new Post: " + data.toString());
            posts.add(new Post(data.getString("name"), data.getString("msg")));
            a.notifyDataSetChanged();
        }
        setResultCode(Activity.RESULT_OK);
    }
}
