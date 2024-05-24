package com.rivelbop.fbxconvgui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import javax.swing.*;

public class FbxConvGui extends ApplicationAdapter {
    public final static int PREF_HEIGHT = 720, PREF_WIDTH = PREF_HEIGHT * 16 / 9;

    private FileExplorerWindow fileExplorer;
    private InputMultiplexer inputMultiplexer;

    private PerspectiveCamera camera;
    private FirstPersonCameraController cameraController;
    private ExtendViewport viewport;

    private ModelBatch modelBatch;
    private Environment environment;

    public static Model model;
    public static ModelInstance modelInstance;
    public static AnimationController animationController;

    public FbxConvGui() {
        SwingUtilities.invokeLater(() -> fileExplorer = new FileExplorerWindow());
    }

    @Override
    public void create() {
        camera = new PerspectiveCamera();
        cameraController = new FirstPersonCameraController(camera);
        cameraController.autoUpdate = true;
        viewport = new ExtendViewport(PREF_WIDTH, PREF_HEIGHT, camera);

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(cameraController);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (animationController != null) animationController.update(Gdx.graphics.getDeltaTime());

        viewport.apply();
        cameraController.update();

        modelBatch.begin(camera);
        if (modelInstance != null) modelBatch.render(modelInstance, environment);
        modelBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        fileExplorer.dispose();
        modelBatch.dispose();
        if (model != null) model.dispose();
        System.exit(0);
    }
}