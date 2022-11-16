package com.udbna.objects;

import java.util.ArrayList;

public class Story {
    public String content = "";
    public ArrayList<Comment> comments = new ArrayList<>();
    public String date = "";
    public int commentCount = 0;
    public int heartCount = 0;
    public Person person = new Person();
}
