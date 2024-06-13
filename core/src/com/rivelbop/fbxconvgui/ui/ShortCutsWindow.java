package com.rivelbop.fbxconvgui.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Displays all shortcuts to the user.
 *
 * @author Philip Jerzak (RivelBop)
 */
public class ShortCutsWindow extends JFrame {
    public ShortCutsWindow() {
        super("Shortcuts");
        setLayout(new GridLayout(0, 1, 0, 16));
        add(new JLabel());

        add(new JLabel("  TAB : Hide UI.  "));
        add(new JLabel("  SPACE : Change camera control.  "));
        add(new JLabel("  TILDE(`) : Opens the fbx file explorer.  "));
        add(new JLabel("  SHIFT : Makes the camera move faster.  "));

        add(new JLabel());
        pack();

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setAlwaysOnTop(true);
        setVisible(false);
    }
}