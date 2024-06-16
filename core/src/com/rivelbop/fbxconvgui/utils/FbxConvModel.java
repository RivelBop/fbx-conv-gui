package com.rivelbop.fbxconvgui.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.UBJsonReader;

import java.io.File;

/**
 * Handles converting and storing a converted model.
 *
 * @author David Jerzak (RivelBop)
 */
public class FbxConvModel implements Disposable {
    // IO
    public File fbx, g3dj, g3db, dir;
    public FileHandle fileHandle, g3djHandle, g3dbHandle;

    // Model properties
    public Model model;
    public ModelInstance instance;
    public AnimationController animationController;

    /**
     * Converts and stores all necessary fbx file data.
     *
     * @param selectedFbxFile The fbx file to read.
     */
    public FbxConvModel(File selectedFbxFile) {
        // Convert the FBX file
        File tempFBX = FbxConv.convertModel(selectedFbxFile);
        dir = tempFBX.getParentFile();

        // Get the converted files
        String fbxFilePath = tempFBX.getAbsolutePath();
        g3dj = new File(fbxFilePath.replace(".fbx", ".g3dj"));
        g3djHandle = new FileHandle(g3dj);
        g3db = new File(fbxFilePath.replace(".fbx", ".g3db"));
        g3dbHandle = new FileHandle(g3db);

        // Move FBX back (if necessary)
        if (dir.getName().endsWith(".fbm")) {
            fbx = new File(dir.getParent() + "/" + tempFBX.getName());
            if (tempFBX.renameTo(fbx))
                System.out.println(fbx.getName() + " -> " + fbx.getParent() + "/");
        } else fbx = tempFBX;
        fileHandle = new FileHandle(fbx);
    }

    /**
     * Updates the animation controller.
     */
    public void update() {
        if (animationController != null)
            animationController.update(Gdx.graphics.getDeltaTime());
    }

    /**
     * Renders the model instance to the provided batch and environment.
     *
     * @param batch       The ModelBatch to render to.
     * @param environment The environment to apply.
     */
    public void render(ModelBatch batch, Environment environment) {
        if (instance != null)
            batch.render(instance, environment);
    }

    /**
     * Loads the model, model instance, and animation controller using the stored G3DJ/G3DB file handle.
     */
    public void reload() {
        // Create the model
        model = new G3dModelLoader(new UBJsonReader())
                .loadModel(g3dbHandle);
        instance = new ModelInstance(model);

        // Create animation controller
        animationController = new AnimationController(instance);
        JsonValue animation = new JsonReader()
                .parse(g3djHandle)
                .get("animations");
        if (animation.child != null)
            animationController.setAnimation(animation.child.get("id").asString(), -1);
    }

    /**
     * Disposes of the model.
     */
    @Override
    public void dispose() {
        model.dispose();
    }
}