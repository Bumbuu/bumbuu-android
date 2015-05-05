package bumbuu.client;

public class Model {
    static class Post {
        String name, msg;

        public Post(String name, String msg) {
            this.name = name;
            this.msg = msg;
        }
    }

    static class User {
        String registration_id;

        public User(String registration_id) {
            this.registration_id = registration_id;
        }
    }
}