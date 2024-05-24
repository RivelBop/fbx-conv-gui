package com.rivelbop.fbxconvgui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.UBJsonReader;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.rivelbop.fbxconvgui.FbxConvGui.*;

public class FileExplorerWindow extends JFrame {
    public JFileChooser explorer;

    public FileExplorerWindow() {
        super("Select FBX File");

        JPanel panel = new JPanel(new BorderLayout());
        createFileExplorer();
        panel.add(explorer, BorderLayout.CENTER);
        add(panel);

        createWindow();
    }

    private void createFileExplorer() {
        explorer = new JFileChooser();
        explorer.setFileSelectionMode(JFileChooser.FILES_ONLY);
        explorer.setAcceptAllFileFilterUsed(false);
        explorer.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".fbx");
            }

            @Override
            public String getDescription() {
                return ".fbx (3D Model Format)";
            }
        });
        explorer.addActionListener(e -> {
            if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                File fbxFile = explorer.getSelectedFile();
                try {
                    FbxConv.convertModel(fbxFile.getAbsolutePath());
                } catch (IOException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                setVisible(false);

                Gdx.app.postRunnable(() -> {
                    model = new G3dModelLoader(new UBJsonReader())
                            .loadModel(
                                    Gdx.files.absolute(fbxFile.getAbsolutePath().replace(".fbx", ".g3db"))
                            );
                    modelInstance = new ModelInstance(model);
                    animationController = new AnimationController(modelInstance);
                    animationController.setAnimation(
                            new JsonReader()
                                    .parse(Gdx.files.absolute(fbxFile.getAbsolutePath().replace(".fbx", ".g3dj")))
                                    .get("animations").child.get("id").asString()
                    );
                });
            } else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
                setVisible(false);
            }
        });
    }

    private void createWindow() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        setMinimumSize(new Dimension(250, 250));
        setMaximumSize(new Dimension(650, 650));
        setPreferredSize(new Dimension(450, 450));
        setSize(450, 450);

        setLocationRelativeTo(null);

        pack();
        setVisible(true);
    }
}