package com.rivelbop.fbxconvgui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rivelbop.fbxconvgui.FbxConvGui;
import com.rivelbop.fbxconvgui.utils.FbxConv;
import com.rivelbop.fbxconvgui.utils.FbxConvModel;
import com.rivelbop.fbxconvgui.utils.Font;

import java.io.IOException;

/**
 * Handles all in-app UI (part of the GLFW window).
 *
 * @author David/Philip Jerzak (RivelBop)
 */
public class FbxConvUI extends Stage {
    private final Skin SKIN;
    private final Font LABEL_FONT;
    public boolean isVisible;

    // GUI Components
    private TextButton shortcutsWindowButton;
    private ScrollPane scrollPane;
    public List<String> animationList;

    /**
     * Creates a libGDX UI Stage, using the provided viewport. Contains all the in-app interface.
     *
     * @param viewport The viewport of applied to the Stage.
     */
    public FbxConvUI(ScreenViewport viewport) {
        super(viewport);
        this.SKIN = new Skin(Gdx.files.internal("skin/metal-ui.json"));
        this.isVisible = true;

        // Opens the file explorer
        TextButton modelButton = new TextButton("Open Model", SKIN);
        modelButton.addListener(e -> {
            if (e.isHandled()) FbxConvGui.fileExplorer.setVisible(true);
            return false;
        });
        modelButton.setBounds(15f, 15f, 125f, 46f);
        addActor(modelButton);

        // Opens the shortcut window
        shortcutsWindowButton = new TextButton("Shortcuts", SKIN);
        shortcutsWindowButton.addListener(event -> {
            if (event.isHandled()) FbxConvGui.shortCutsWindow.setVisible(true);
            return false;
        });
        addActor(shortcutsWindowButton);

        // Sets the camera's FOV
        Slider fovSlider = new Slider(30f, 120f, 2f, false, SKIN);
        fovSlider.setBounds(220f, 15f, 128f, 48f);
        fovSlider.setValue(70f);
        FbxConvGui.camera.fieldOfView = fovSlider.getValue();
        fovSlider.addListener(e -> {
            FbxConvGui.camera.fieldOfView = fovSlider.getValue();
            return false;
        });
        addActor(fovSlider);

        // Alters the model animation name
        TextField animationField = new TextField("", SKIN);
        animationField.setBounds(220f, 60f, 128f, 48f);
        animationField.setTextFieldListener((f, c) -> {
            if (c == '\n') {
                FbxConvModel fbxConvModel = FbxConvGui.model;
                if (fbxConvModel != null) {
                    FbxConv.renameAnimation(fbxConvModel.G3DJ_HANDLE, animationField.getText());
                    try {
                        FbxConv.G3DB_CONVERTER.convert(FbxConvGui.model.G3DJ_HANDLE, true);
                        FbxConvGui.model.reload();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                super.unfocus(animationField);
            }
        });
        addActor(animationField);

        // Lists all the animations of the model
        animationList = new List<>(SKIN);
        scrollPane = new ScrollPane(animationList);
        addActor(scrollPane);

        // Loads the 'Label' font
        Font.FontBuilder fontBuilder = new Font.FontBuilder();
        this.LABEL_FONT = fontBuilder
                .setFont(Gdx.files.internal("Hack.ttf"))
                .setSize(20)
                .build();
        fontBuilder.dispose();
    }

    /**
     * Updates and renders the Stage.
     */
    public void render() {
        if (isVisible && FbxConvGui.fileExplorer != null && !FbxConvGui.fileExplorer.isVisible()) {
            // Render Stage UI
            getViewport().apply(true);
            act();
            draw();

            // Render Font UI
            SpriteBatch batch = (SpriteBatch) getBatch();
            batch.begin();
            LABEL_FONT.draw(batch, "Animation name: ", 20, 90);
            LABEL_FONT.draw(batch, "Fov: ", 170, 48);
            batch.end();
        }
    }

    /**
     * Updates the viewport when the windows is resized.
     *
     * @param width  The new window width.
     * @param height The new window height.
     */
    public void updateViewport(int width, int height) {
        getViewport().update(width, height, true);
        shortcutsWindowButton.setBounds(0, height - 32f, 64f, 32f);
        scrollPane.setBounds(width - 144f, height - 160f, 144f, 160f);
    }

    /**
     * Toggles the visibility of all UI components on the stage.
     */
    public void toggleVisibility() {
        if (FbxConvGui.fileExplorer != null && !FbxConvGui.fileExplorer.isVisible()) isVisible = !isVisible;
    }

    public boolean isFocused() {
        return getKeyboardFocus() != null;
    }

    /**
     * Disposes of the stage, skin, and font.
     */
    @Override
    public void dispose() {
        super.dispose();
        SKIN.dispose();
        LABEL_FONT.dispose();
    }
}