package com.rivelbop.fbxconvgui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.UBJsonReader;
import com.rivelbop.fbxconvgui.utils.FbxConv;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

import static com.rivelbop.fbxconvgui.FbxConvGui.*;

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
                return ".fbx (3D Model Format)";
            }
        });

        // If FBX file is selected, convert the model, and display it to the user
        explorer.addActionListener(e -> {
            if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                // Converts the selected FBX file to both G3DB and G3DJ
                File fbxFile = FbxConv.convertModel(explorer.getSelectedFile());
                setVisible(false);

                // Sets the in-app model to the generated G3DB model
                String fbxFilePath = fbxFile.getAbsolutePath();
                Gdx.app.postRunnable(() -> {
                    model = new G3dModelLoader(new UBJsonReader())
                            .loadModel(
                                    Gdx.files.absolute(fbxFilePath.replace(".fbx", ".g3db"))
                            );
                    modelInstance = new ModelInstance(model);
                    animationController = new AnimationController(modelInstance);

                    JsonValue animation = new JsonReader()
                            .parse(Gdx.files.absolute(fbxFilePath.replace(".fbx", ".g3dj")))
                            .get("animations");
                    if (animation.child != null)
                        animationController.setAnimation(animation.child.get("id").asString(), -1);
                    else animationController = null;
                });

                // Move FBX file back out of the new model directory
                if (fbxFile.getParent().endsWith(".fbm")) {
                    File tempFbxFile = new File(fbxFile.getParentFile().getParent() + "/" + fbxFile.getName());
                    if (fbxFile.renameTo(tempFbxFile))
                        System.out.println(tempFbxFile.getName() + " -> " + tempFbxFile.getParent() + "/");
                    explorer.setSelectedFile(tempFbxFile);
                }
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
}