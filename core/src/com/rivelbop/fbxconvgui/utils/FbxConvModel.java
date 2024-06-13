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
    public final File FBX, G3DJ, G3DB, DIR;
    public final FileHandle FBX_HANDLE, G3DJ_HANDLE, G3DB_HANDLE;

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
        DIR = tempFBX.getParentFile();

        // Get the converted files
        String fbxFilePath = tempFBX.getAbsolutePath();
        G3DJ = new File(fbxFilePath.replace(".fbx", ".g3dj"));
        G3DJ_HANDLE = new FileHandle(G3DJ);
        G3DB = new File(fbxFilePath.replace(".fbx", ".g3db"));
        G3DB_HANDLE = new FileHandle(G3DB);

        // Move FBX back (if necessary)
        if (DIR.getName().endsWith(".fbm")) {
            FBX = new File(DIR.getParent() + "/" + tempFBX.getName());
            if (tempFBX.renameTo(FBX))
                System.out.println(FBX.getName() + " -> " + FBX.getParent() + "/");
        } else FBX = tempFBX;
        FBX_HANDLE = new FileHandle(FBX);
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
                .loadModel(G3DB_HANDLE);
        instance = new ModelInstance(model);

        // Create animation controller
        animationController = new AnimationController(instance);
        JsonValue animation = new JsonReader()
                .parse(G3DJ_HANDLE)
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