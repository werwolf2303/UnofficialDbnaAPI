package com.udbna;

import com.udbna.objects.*;
import com.udbna.tools.WebReq;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        DBNA dbna = new DBNA("", "");
        MessageBoard board = dbna.getMessageBoard();
        for(Story story : board.stories) {
            System.out.println(story.content);
        }
    }
}
