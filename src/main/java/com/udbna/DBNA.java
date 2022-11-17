package com.udbna;

import com.udbna.objects.*;
import com.udbna.tools.WebReq;
import com.udbna.tools.WebSocket;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.TransportRequest;
import org.springframework.web.socket.sockjs.transport.TransportType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public class DBNA {
    WebReq req = new WebReq();
    public DBNA(String username, String password) {
        req.login(username,password);
    }
    public ArrayList<Person> getRecommended() {
        ArrayList<Person> persons = new ArrayList<>();
        String response = req.request("https://www.dbna.com/json/search/recommended");
        JSONObject object = new JSONObject(response);
        JSONArray array = object.getJSONArray("users");
        for(Object o : array) {
            JSONObject p = new JSONObject(o.toString());
            System.out.println(p);
            Person person = new Person();
            try {
                person.age = p.getInt("age");
            }catch (JSONException e) {
            }
            try {
                person.city = p.getString("loc");
            }catch (JSONException e) {
            }
            try {
                person.distance = p.getInt("dist");
            }catch (JSONException e) {
            }
            try {
                person.id = p.getString("id");
            }catch (JSONException e) {
            }
            try {
                person.isVerified = Boolean.parseBoolean(p.getString("fakecheck"));
            }catch (JSONException e) {
            }
            try {
                person.online = p.getString("onlineStatus");
            }catch (JSONException e) {
            }
            try {
                person.picture = p.getString("picture");
            }catch (JSONException e) {
            }
            try {
                person.username = p.getString("username");
            }catch (JSONException e) {
            }
            persons.add(person);
        }
        return persons;
    }
    public IPerson getMyself() {
        IPerson person = new IPerson();
        String response = req.getLoginResponse();
        JSONObject object = new JSONObject(response);
        JSONObject thumb = new JSONObject(object.get("thumb").toString());
        person.age = thumb.getInt("age");
        person.id = thumb.getString("id");
        person.city = thumb.getString("loc");
        person.picture = thumb.getString("picture");
        person.username = thumb.getString("username");
        person.language = object.getString("lang");
        return person;
    }
    public Settings getSettings() {
        Settings settings = new Settings();
        String response = req.getLoginResponse();
        JSONObject object = new JSONObject(response);
        JSONObject set = new JSONObject(object.get("settings").toString());
        settings.chatRangeRestriction = set.getBoolean("age");
        settings.rangeFrom = new JSONObject(set.get("range").toString()).getInt("from");
        settings.rangeTo = new JSONObject(set.get("range").toString()).getInt("to");
        settings.darkmode = set.getBoolean("darkmode");
        settings.imperial = set.getBoolean("imperial");
        settings.notifyactivity = set.getBoolean("notify:activity");
        settings.notifymessage = set.getBoolean("notify:message");
        settings.picturecomments = set.getBoolean("pictureComments");
        settings.picturesstream = set.getBoolean("picturesStream");
        settings.soundcontact = set.getBoolean("sound:contact");
        settings.soundmessage = set.getBoolean("sound:message");
        settings.soundnotify = set.getBoolean("sound:notify");
        settings.streamcomments = set.getBoolean("stream:comments");
        settings.streamcountry = set.getBoolean("stream:country");
        settings.streamdist = set.getBoolean("stream:dist");
        settings.streamdistpics = set.getBoolean("stream:distpics");
        settings.streampeeract = set.getBoolean("stream:peeract");
        settings.streampeers = set.getBoolean("stream:peers");
        settings.streamsponsored = set.getBoolean("stream:sponsored");
        settings.streamDistance = set.getInt("streamDistance");
        settings.wall = set.getBoolean("wall");
        return settings;
    }
    public MessageBoard getMessageBoard() {
        MessageBoard board = new MessageBoard();
        ArrayList<Story> stories = board.stories;
        String response = req.request("https://www.dbna.com/json/pulse/personal");
        JSONObject object = new JSONObject(response);
        for(Object o : object.getJSONArray("stories")) {
            JSONObject s = new JSONObject(o.toString());
            Story story = new Story();
            try {
                story.content = s.getString("body");
            }catch (JSONException ex) {
                //ToDo: Implement Pictures
                continue;
            }
            story.date = s.getString("date");
            story.heartCount = new JSONObject(s.get("hearts").toString()).getInt("count");
            story.commentCount = new JSONObject(s.get("comments").toString()).getInt("count");
            ArrayList<Comment> comments = story.comments;
            try {
                for (Object c : new JSONObject(s.get("comments").toString()).getJSONArray("stories")) {
                    JSONObject com = new JSONObject(c.toString());
                    Comment comment = new Comment();
                    comment.content = com.getString("body");
                    comment.date = com.getString("date");
                    comment.heartCount = new JSONObject(com.get("hearts").toString()).getInt("count");
                    JSONObject p = new JSONObject(com.get("user").toString());
                    Person person = comment.person;
                    try {
                        person.age = p.getInt("age");
                    }catch (JSONException ex) {
                    }
                    try {
                        person.distance = p.getInt("dist");
                    }catch (JSONException ex) {
                    }
                    try {
                        person.id = p.getString("id");
                    }catch (JSONException ex) {
                    }
                    try {
                        person.username = p.getString("username");
                    }catch (JSONException ex) {
                    }
                    try {
                        person.picture = p.getString("picture");
                    }catch (JSONException ex) {
                    }
                    try {
                        person.online = p.getString("onlineStatus");
                    }catch (JSONException ex) {
                    }
                    try {
                        person.city = p.getString("loc");
                    }catch (JSONException ex) {
                    }
                    comments.add(comment);
                }
            }catch (JSONException ex) {
                //No comments
            }
            stories.add(story);
        }
        try {
        JSONObject pin = new JSONObject(object.get("pinned").toString());
        Pinned p = board.pinned;
        ArrayList<Comment> comments = p.comments;
            for (Object c : new JSONObject(pin.get("comments").toString()).getJSONArray("stories")) {
                JSONObject com = new JSONObject(c.toString());
                Comment comment = new Comment();
                comment.content = com.getString("body");
                comment.date = com.getString("date");
                comment.heartCount = new JSONObject(com.get("hearts").toString()).getInt("count");
                JSONObject pe = new JSONObject(com.get("user").toString());
                Person person = comment.person;
                try {
                    person.age = pe.getInt("age");
                }catch (JSONException ex) {
                }
                try {
                    person.distance = pe.getInt("dist");
                }catch (JSONException ex) {
                }
                try {
                    person.id = pe.getString("id");
                }catch (JSONException ex) {
                }
                try {
                    person.username = pe.getString("username");
                }catch (JSONException ex) {
                }
                try {
                    person.picture = pe.getString("picture");
                }catch (JSONException ex) {
                }
                try {
                    person.online = pe.getString("onlineStatus");
                }catch (JSONException ex) {
                }
                try {
                    person.city = pe.getString("loc");
                }catch (JSONException ex) {
                }
                comments.add(comment);
            }
        p.content = pin.getString("body");
        p.date = pin.getString("date");
        p.heartCount = new JSONObject(pin.get("hearts").toString()).getInt("count");
        JSONObject pe = new JSONObject(pin.get("user").toString());
        Person person = p.person;
        try {
            person.distance = pe.getInt("dist");
        }catch (JSONException ex) {
        }
        try {
            person.isVerified = pe.getBoolean("fakecheck");
        }catch (JSONException ex) {
        }
        try {
            person.group = pe.getBoolean("group");
        }catch (JSONException ex) {
        }
        try {
            person.id = pe.getString("id");
        }catch (JSONException ex) {
        }
        try {
            person.city = pe.getString("loc");
        }catch (JSONException ex) {
        }
        try {
            person.picture = pe.getString("picture");
        }catch (JSONException ex) {
        }
        try {
            person.username = pe.getString("username");
        }catch (JSONException ex) {
        }
        }catch (JSONException ex) {
            //No comments
        }
        return board;
    }
    public Person getPersonWithId(String id) {
        return null;
    }
    //Unfinished
    public Messages getMessages() {
        //wss://www.dbna.com/chat-server/socket.io/?EIO=3&transport=websocket
        String connectionURL = "wss://www.dbna.com/chat-server/socket.io/?EIO=3&transport=websocket";
        WebSocket socket = new WebSocket(connectionURL, req.user, req.pass);
        Messages message = new Messages();
        String socketResponse = socket.sendCode("422[\"peers\",{}]").content;
        System.out.println(socketResponse);
        return message;
    }

}
