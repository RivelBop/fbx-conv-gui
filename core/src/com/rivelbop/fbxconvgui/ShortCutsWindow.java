package com.rivelbop.fbxconvgui;

import javax.swing.*;
import java.awt.*;

public class ShortCutsWindow extends JFrame {
    public ShortCutsWindow() {
        super("Shortcuts");
        setLayout(new GridLayout(3, 0, 0, 32));

        add(new JLabel("TAB : Hides UI"));
        add(new JLabel("SPACE : Changes camera to rotating around the object"));
        add(new JLabel("TILDE(`) : Makes the file explorer window pop up"));
        pack();

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setAlwaysOnTop(true);
        setVisible(false);
    }
}