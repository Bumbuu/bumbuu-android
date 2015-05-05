package bumbuu.client;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.android.gms.common.*;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bumbuu.client.Model.*;

public class ChatActivity extends ActionBarActivity {
    final static String TAG = "Bumbuu";
    final static String SENDER_ID = "325640508732";
    final static String REG_ID_KEY = "registration_id";

    GoogleCloudMessaging gcm;

    public List<Post> posts = new ArrayList<>();

    MessageReceiver mr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatactivity);

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            if (getPref(REG_ID_KEY).isEmpty())
                registerInBackground();

            ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String msg = ((EditText)findViewById(R.id.editText)).getText().toString();
                    if (!msg.isEmpty()) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                HttpServer.newPost(new Post("Tyrone", msg));
                                return null;
                            }
                        }.execute();
                    }
                }
            });

            ListView lv = (ListView)findViewById(R.id.listView);
            class MsgRow extends ArrayAdapter<Post> {
                public MsgRow() {
                    super(ChatActivity.this, 0, posts);
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    Post p = getItem(position);
                    if (convertView == null)
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.listrow, parent, false);
                    ((TextView)convertView.findViewById(R.id.name)).setText(p.name);
                    ((TextView)convertView.findViewById(R.id.msg)).setText(p.msg);
                    return convertView;
                }
            }
            MsgRow adapter = new MsgRow();
            lv.setAdapter(adapter);

            IntentFilter i = new IntentFilter("com.google.android.c2dm.intent.RECEIVE");
            i.addCategory("bumbuu.client");
            mr = new MessageReceiver(adapter, posts);
            registerReceiver(mr, i);
        } else {
            Log.i(TAG, "play services apk not installed");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStop() {
        super.onPause();
        if (mr != null) {
            unregisterReceiver(mr);
            mr = null;
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                for (int i = 0; i < 3; ++i) {
                    try {
                        String reg_id = gcm.register(SENDER_ID);

                        if (reg_id.isEmpty())
                            continue;

                        if (HttpServer.newUser(reg_id) == false)
                            continue;

                        setPref(REG_ID_KEY, reg_id);

                        break;
                    } catch (IOException e) {
                        Log.i(TAG, "error while registering", e);
                    }
                }
                return null;
            }
        }.execute();
    }

    private boolean checkPlayServices() {
        int r = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (r != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(r)) {
                GooglePlayServicesUtil.getErrorDialog(r, this, 9000).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void setPref(String key, String value) {
        final SharedPreferences prefs = getSharedPreferences(this.getClass().getSimpleName(), Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
    }

    private String getPref(String key) {
        final SharedPreferences prefs = getSharedPreferences(this.getClass().getSimpleName(), Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }
}
