package com.rivelbop.fbxconvgui.utils;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;

import java.io.File;

public class FbxConvModel {
    public final File FBX, G3DJ, G3DB, DIR;
    public Model model;
    public ModelInstance instance;
    public AnimationController animation;

    public FbxConvModel(File selectedFbxFile) {
        // Convert the FBX file
        FBX = FbxConv.convertModel(selectedFbxFile);
        DIR = FBX.getParentFile();

        // Get the converted files
        String fbxFilePath = FBX.getAbsolutePath();
        G3DJ = new File(fbxFilePath.replace(".fbx", ".g3dj"));
        G3DB = new File(fbxFilePath.replace(".fbx", ".g3db"));

        // Create the model

    }
}