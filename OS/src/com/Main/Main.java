package com.Main;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.controller.OSManager;
import com.view.UIDesign;

public class Main {

    public static void main(String[] args) {
        try {
            OSManager manager = new OSManager();
            UIDesign ui = new UIDesign(manager);
            ui.getOsManager().setUiDesign(ui);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}