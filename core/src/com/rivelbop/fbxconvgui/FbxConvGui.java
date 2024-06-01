package com.rivelbop.fbxconvgui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import javax.swing.*;

/**
 * Initializes, handles, and displays all data.
 *
 * @author David/Philip Jerzak (RivelBop)
 */
public class FbxConvGui extends ApplicationAdapter {
    public final static int PREF_HEIGHT = 720, PREF_WIDTH = PREF_HEIGHT * 16 / 9;

    public static FileExplorerWindow fileExplorer;
    private InputMultiplexer inputMultiplexer;

    private boolean flyCam, oldFlyCam;
    private FirstPersonCameraController cameraController;
    public static PerspectiveCamera camera;
    public ExtendViewport viewport;

    private FbxConvUI convUI;

    private ModelBatch modelBatch;
    private Environment environment;

    private ModelInstance sky;
    public static Model model;
    public static ModelInstance modelInstance;
    public static AnimationController animationController;

    /**
     * Create the Java Swing File Explorer on separate thread to avoid interference with LWJGL thread.
     */
    public FbxConvGui() {
        SwingUtilities.invokeLater(() -> fileExplorer = new FileExplorerWindow());
    }

    @Override
    public void create() {
        // Create camera and viewport
        camera = new PerspectiveCamera();
        camera.far = 0f;
        cameraController = new FirstPersonCameraController(camera);
        cameraController.setVelocity(250f);
        cameraController.autoUpdate = false;
        viewport = new ExtendViewport(PREF_WIDTH, PREF_HEIGHT, camera);

        // Create in-app UI
        convUI = new FbxConvUI(new ScreenViewport(new OrthographicCamera()));

        // Create model rendering environment
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        sky = new ModelInstance(new G3dModelLoader(new UBJsonReader()).loadModel(Gdx.files.internal("sky.g3db")));

        // Handle input
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(convUI);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    /**
     * Updates and renders models and GUI.
     */
    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // If pressed ESC, unfocus all UI elements
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) convUI.unfocusAll();

        // Toggle UI visibility
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) convUI.toggleVisibility();
        if (Gdx.input.isKeyPressed(Input.Keys.GRAVE)) fileExplorer.setVisible(true);

        // Toggle Fly Camera
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) flyCam = !flyCam;

        // Set each libGDX Stage UI element's visibility according to what it should be
        if (fileExplorer != null && fileExplorer.isVisible()) {
            for (Actor a : convUI.getActors()) a.setVisible(false);
        } else {
            for (Actor a : convUI.getActors()) a.setVisible(convUI.isVisible);
        }

        if (animationController != null) animationController.update(Gdx.graphics.getDeltaTime());

        // TEMPORARY
        //if (modelInstance != null) modelInstance.transform.setToScaling(0.1f, 0.1f, 0.1f);
        // TEMPORARY

        // Toggles Fly Camera and Target Camera
        if (flyCam) {
            if (!oldFlyCam) {
                inputMultiplexer.addProcessor(cameraController);
                oldFlyCam = true;
            }
            cameraController.update();
        } else {
            if (oldFlyCam) {
                inputMultiplexer.removeProcessor(cameraController);
                oldFlyCam = false;
            }
            if (modelInstance != null) camera.lookAt(modelInstance.transform.getTranslation(new Vector3()));
        }

        camera.update(true);
        viewport.apply();
        modelBatch.begin(camera);
        modelBatch.render(sky);
        if (modelInstance != null) modelBatch.render(modelInstance, environment);
        modelBatch.end();

        convUI.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        convUI.updateViewport(width, height);
    }

    @Override
    public void dispose() {
        fileExplorer.dispose();
        convUI.dispose();
        modelBatch.dispose();
        sky.model.dispose();
        if (model != null) model.dispose();
        System.exit(0);
    }
}