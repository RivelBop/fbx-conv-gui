package com.rivelbop.fbxconvgui;
import javax.swing.*;

public class ShortCutsWindow{

    public static JFrame frame;

    public ShortCutsWindow() {

        // TAB shortcut
        JLabel tabText = new JLabel("TAB: hides UI");
        tabText.setBounds(0,0,32,32);

        // SPACE shortcut
        JLabel spaceText = new JLabel("SPACE: changes camera to rotating around the object");
        spaceText.setBounds(0,50,32,32);

        // Tilde shortcut
        JLabel tildeText = new JLabel("Tilde(`): Makes the file explorer window pop up");

        // makes window and adds components
        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(false);
        frame.setResizable(false);
        frame.setSize(300, 300);
        frame.setLayout(null);
        frame.add(tabText);
        frame.add(spaceText);
        frame.add(tildeText);
    }
}
