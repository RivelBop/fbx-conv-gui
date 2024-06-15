package com.rivelbop.fbxconvgui.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Pop up window asking user if they want to combine the animations of two separate models.
 *
 * @author David Jerzak (RivelBop)
 */
public class CombinationWindow extends JFrame {
    public CombinationWindow() {
        super("");
        setLayout(new GridBagLayout());

        GridBagConstraints constraint = new GridBagConstraints();

        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.gridx = 1;
        constraint.gridy = 0;
        add(new JLabel("What would you like to do?", JLabel.CENTER), constraint);

        constraint.gridy = 1;
        add(new JLabel(" "), constraint);

        constraint.gridy = 2;
        add(new JLabel(" "), constraint);

        constraint.gridx = 0;
        constraint.gridy = 3;
        JButton loadButton = new JButton("Load");
        add(loadButton, constraint);

        constraint.gridx = 2;
        JButton combineButton = new JButton("Combine");
        add(combineButton, constraint);

        pack();
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setAlwaysOnTop(true);
        setVisible(true);
    }
}