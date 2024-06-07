package com.rivelbop.fbxconvgui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rivelbop.fbxconvgui.utils.Font;

import java.awt.*;

/**
 * Handles all in-app UI (part of the GLFW window).
 *
 * @author David/Philip Jerzak (RivelBop)
 */
public class FbxConvUI extends Stage {
    private final Skin SKIN;
    private final Font LABEL_FONT;
    public boolean isVisible;

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
        TextButton shortcutsWindowButton = new TextButton("Shortcuts", SKIN);
        shortcutsWindowButton.addListener(event -> {
            if (event.isHandled()) {
                FbxConvGui.shortCutsWindow.setVisible(true);
            }
            return false;
        });
        shortcutsWindowButton.setBounds(0, 690, 64f, 32f);
        addActor(shortcutsWindowButton);

        // Sets the perspective camera's FOV
        Slider sliderFov = new Slider(30f, 120f, 2f, false, SKIN);
        sliderFov.setBounds(220f, 15f, 128f, 48f);
        sliderFov.setValue(70f);
        FbxConvGui.camera.fieldOfView = sliderFov.getValue();
        sliderFov.addListener(e -> {
            FbxConvGui.camera.fieldOfView = sliderFov.getValue();
            return false;
        });
        addActor(sliderFov);

        // Alters the model's animation name
        TextField textBox = new TextField("", SKIN);
        textBox.setBounds(220f, 60f, 128f, 48f);
        textBox.setTextFieldListener((f, c) -> {
            if (c == '\n') {
                System.out.println("HEY");
                FbxConv.renameAnimation(Gdx.files.absolute(FbxConvGui.fileExplorer.explorer.getSelectedFile().getAbsolutePath().replace(".fbx", ".g3dj")), textBox.getText());
            }
        });
        addActor(textBox);

        // Loads the 'Label' font
        Font.FontBuilder fontBuilder = new Font.FontBuilder();
        LABEL_FONT = fontBuilder
                .setFont(Gdx.files.internal("Hack.ttf"))
                .setSize(20)
                .build();
        fontBuilder.dispose();

    }

    /**
     * Updates and renders the Stage.
     */
    public void render() {
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

    /**
     * Updates the viewport when the windows is resized.
     *
     * @param width  The new window width.
     * @param height The new window height.
     */
    public void updateViewport(int width, int height) {
        getViewport().update(width, height, true);
    }

    /**
     * Toggles the visibility of all UI components on the stage.
     */
    public void toggleVisibility() {
        if (FbxConvGui.fileExplorer != null && !FbxConvGui.fileExplorer.isVisible()) isVisible = !isVisible;
    }

    /**
     * Disposes of both the stage and skin applied to each element.
     */
    @Override
    public void dispose() {
        super.dispose();
        SKIN.dispose();
    }
}