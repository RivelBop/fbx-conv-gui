package com.rivelbop.fbxconvgui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.rivelbop.fbxconvgui.FbxConvGui;
import com.rivelbop.fbxconvgui.utils.FbxConv;
import com.rivelbop.fbxconvgui.utils.FbxConvModel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.rivelbop.fbxconvgui.FbxConvGui.model;

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
            FbxConvModel selectedModel = new FbxConvModel(FbxConvGui.fileExplorer.explorer.getSelectedFile());

            File g3dj = FbxConv.combineG3DJ(model.g3djHandle, selectedModel.g3djHandle);
            model.g3dj = g3dj;
            model.g3djHandle = new FileHandle(g3dj);

            try {
                FbxConv.G3DB_CONVERTER.convert(model.g3djHandle, true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            File g3db = new File(g3dj.getAbsolutePath().replace(".g3dj", ".g3db"));
            model.g3db = g3db;
            model.g3dbHandle = new FileHandle(g3db);
            Gdx.app.postRunnable(() -> model.reload());
            FbxConvGui.convUI.animationList.setItems(FbxConv.parseAnimations(model.g3djHandle));
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