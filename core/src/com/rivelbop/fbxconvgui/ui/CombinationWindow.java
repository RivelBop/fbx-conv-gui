package com.rivelbop.fbxconvgui.ui;

import com.rivelbop.fbxconvgui.FbxConvGui;
import com.rivelbop.fbxconvgui.utils.FbxConv;

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

        // Layout
        GridBagConstraints constraint = new GridBagConstraints();

        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.gridx = 1;
        constraint.gridy = 0;
        add(new JLabel("What would you like to do?", JLabel.CENTER), constraint);

        constraint.gridy = 1;
        add(new JLabel(" "), constraint);

        constraint.gridy = 2;
        add(new JLabel(" "), constraint);

        // Loads the model into the scene
        constraint.gridx = 0;
        constraint.gridy = 3;
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(e -> {
            super.setVisible(false);
            FbxConvGui.fileExplorer.loadSelectedModelToScene();
        });
        add(loadButton, constraint);

        // Combines the animations into one model
        constraint.gridx = 2;
        JButton combineButton = new JButton("Combine");
        combineButton.addActionListener(e -> {
            super.setVisible(false);
            // TODO: Combine the files and set model to that
            //FbxConv.combineG3DJ();
        });
        add(combineButton, constraint);

        pack();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setAlwaysOnTop(true);
        setVisible(false);
    }
}