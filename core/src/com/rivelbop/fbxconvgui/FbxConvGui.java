package com.rivelbop.fbxconvgui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rivelbop.fbxconvgui.ui.CombinationWindow;
import com.rivelbop.fbxconvgui.ui.FbxConvUI;
import com.rivelbop.fbxconvgui.ui.FileExplorerWindow;
import com.rivelbop.fbxconvgui.ui.ShortCutsWindow;
import com.rivelbop.fbxconvgui.utils.FbxConvModel;

import javax.swing.*;

/**
 * Initializes, handles, and displays all data.
 *
 * @author David/Philip Jerzak (RivelBop)
 */
public class FbxConvGui extends ApplicationAdapter {
    // View Dimensions
    public final static int PREF_HEIGHT = 720, PREF_WIDTH = PREF_HEIGHT * 16 / 9;

    // UI
    public static FileExplorerWindow fileExplorer;
    public static ShortCutsWindow shortCutsWindow;
    public static CombinationWindow combinationWindow;
    public static FbxConvUI convUI;

    // Input Processes
    private InputMultiplexer inputMultiplexer;
    private FirstPersonCameraController cameraController;

    // Camera and Viewport
    private boolean flyCam, oldFlyCam;
    public static PerspectiveCamera camera;
    public ExtendViewport viewport;

    // 3D Model Rendering Environment
    private ModelBatch modelBatch;
    private Environment environment;
    private ModelInstance sky;

    // Model
    public static FbxConvModel model;

    /**
     * Create the Java Swing File Explorer on separate thread to avoid interference with LWJGL thread.
     */
    public FbxConvGui() {
        SwingUtilities.invokeLater(() -> {
            fileExplorer = new FileExplorerWindow();
            shortCutsWindow = new ShortCutsWindow();
            combinationWindow = new CombinationWindow();
        });
    }

    @Override
    public void create() {
        // Create camera and viewport
        camera = new PerspectiveCamera();
        camera.far = 10000f;
        cameraController = new FirstPersonCameraController(camera);
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

        // If pressed ESC, all UI elements are unfocused
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            convUI.unfocusAll();
            fileExplorer.setVisible(false);
            shortCutsWindow.setVisible(false);
        }

        // Toggle UI visibility
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            convUI.toggleVisibility();
            convUI.unfocusAll();
        }
        if (fileExplorer != null && Gdx.input.isKeyPressed(Input.Keys.GRAVE)) fileExplorer.setVisible(true);

        // Toggle Fly Camera
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) flyCam = !flyCam;

        // Camera sprint
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
            cameraController.setVelocity(1000f);
        else
            cameraController.setVelocity(500f);

        // Update model animations, if applicable
        if (model != null) model.update();

        // Toggles Fly Camera and Target Camera
        if (flyCam) {
            if (!oldFlyCam && !convUI.isFocused()) {
                inputMultiplexer.addProcessor(cameraController);
                oldFlyCam = true;
            }
            cameraController.update();
        } else {
            if (oldFlyCam) {
                inputMultiplexer.removeProcessor(cameraController);
                oldFlyCam = false;
            }
            if (model != null && model.instance != null)
                camera.lookAt(model.instance.transform.getTranslation(new Vector3()));
        }

        // If the UI is in focus, don't allow the user to control the camera
        if (convUI.isFocused()) {
            inputMultiplexer.removeProcessor(cameraController);
            oldFlyCam = false;
        }

        viewport.apply();
        camera.update(true);

        modelBatch.begin(camera);
        modelBatch.render(sky);
        if (model != null) model.render(modelBatch, environment);
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
        // UI
        fileExplorer.dispose();
        convUI.dispose();

        // 3D Model View
        modelBatch.dispose();
        if (model != null) model.dispose();
        sky.model.dispose();

        // Java
        System.exit(0);
    }
}