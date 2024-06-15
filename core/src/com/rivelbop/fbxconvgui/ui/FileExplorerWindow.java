package com.rivelbop.fbxconvgui.ui;

import com.badlogic.gdx.Gdx;
import com.rivelbop.fbxconvgui.FbxConvGui;
import com.rivelbop.fbxconvgui.utils.FbxConv;
import com.rivelbop.fbxconvgui.utils.FbxConvModel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

import static com.rivelbop.fbxconvgui.FbxConvGui.model;

/**
 * Creates a file explorer window using Java Swing.
 *
 * @author David Jerzak (RivelBop)
 */
public class FileExplorerWindow extends JFrame {
    public JFileChooser explorer;

    /**
     * Creates and configures a BorderLayout JFrame, adds a centered JFileChooser to it.
     */
    public FileExplorerWindow() {
        super("Select FBX File");
        createWindow();
    }

    /**
     * Creates and filters the JFileChooser.
     */
    private void createFileExplorer() {
        // Filter FBX files only
        explorer = new JFileChooser();
        explorer.setAcceptAllFileFilterUsed(false);
        explorer.setFileSelectionMode(JFileChooser.FILES_ONLY);
        explorer.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".fbx");
            }

            @Override
            public String getDescription() {
                return ".fbx";
            }
        });

        // If FBX file is selected, convert the model, and display it to the user
        explorer.addActionListener(e -> {
            if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                if (model == null) loadSelectedModelToScene();
                else FbxConvGui.combinationWindow.setVisible(true);
            } else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) setVisible(false);
        });
    }

    /**
     * Creates a JFrame that is hidden on close, 350-650 size, and always on top.
     */
    private void createWindow() {
        setLayout(new BorderLayout());
        createFileExplorer();
        add(explorer, BorderLayout.CENTER);

        setMinimumSize(new Dimension(350, 350));
        setPreferredSize(new Dimension(450, 450));
        setMaximumSize(new Dimension(650, 650));
        setSize(450, 450);
        pack();

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setVisible(false);
    }

    /**
     * Loads the selected model to the 3D world to be displayed.
     */
    public void loadSelectedModelToScene() {
        setVisible(false);

        FbxConvModel tempModel = model;
        model = new FbxConvModel(explorer.getSelectedFile());
        Gdx.app.postRunnable(() -> {
            if (tempModel != null) tempModel.dispose();
            model.reload();
            FbxConvGui.convUI.animationList.setItems(FbxConv.parseAnimations(model.G3DJ_HANDLE));
        });

        explorer.setSelectedFile(model.FBX);
    }
}