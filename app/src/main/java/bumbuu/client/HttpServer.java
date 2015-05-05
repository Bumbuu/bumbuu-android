package bumbuu.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import bumbuu.client.Model.*;

public class HttpServer {
    static Gson gson = new Gson();
    static OkHttpClient http = new OkHttpClient();
    final static String SERVER = "http://uubmub.com:4567";

    public static List<Post> getPosts() throws IOException {
        Response r = getFromServer("/posts");
        if (r.isSuccessful()) {
            Type type = new TypeToken<List<Post>>(){}.getType();
            List<Post> posts = gson.fromJson(r.body().string(), type);
            return posts;
        } else {
            return new ArrayList<>();
        }
    }

    public static boolean newPost(Post post) {
        try {
            return postToServer("/newPost", post) == 201;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean newUser(String rid) {
        try {
            return postToServer("/newUser", new User(rid)) == 201;
        } catch (IOException e) {
            return false;
        }
    }

    private static <T> int postToServer(String path, T data) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(data));
        return http.newCall(
                new Request.Builder()
                        .url(SERVER + path)
                        .post(body)
                        .build()
        ).execute().code();
    }

    private static Response getFromServer(String path) throws IOException {
        return http.newCall(
                new Request.Builder()
                        .url(SERVER + path)
                        .build()
        ).execute();
    }
}
