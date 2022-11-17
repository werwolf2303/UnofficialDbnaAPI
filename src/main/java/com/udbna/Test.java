package com.udbna;

import com.udbna.objects.*;
import com.udbna.tools.WebReq;
import com.udbna.tools.WebSocket;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        DBNA dbna = new DBNA("", "");
        System.out.println(dbna.getMessageBoard().stories.get(0).content);
    }
}
