package com.rivelbop.fbxconvgui;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import static com.rivelbop.fbxconvgui.FbxConvGui.PREF_HEIGHT;
import static com.rivelbop.fbxconvgui.FbxConvGui.PREF_WIDTH;

// macOS: -XstartOnFirstThread
public class DesktopLauncher {
	public final static int MAX_HEIGHT = 1080, MAX_WIDTH = MAX_HEIGHT * 16 / 9;
	public final static int MIN_HEIGHT = 480, MIN_WIDTH = MIN_HEIGHT * 16 / 9;

	public static void main (String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(true);
		config.setResizable(true);
		config.setWindowedMode(PREF_WIDTH, PREF_HEIGHT);
		config.setWindowSizeLimits(MIN_WIDTH, MIN_HEIGHT, MAX_WIDTH, MAX_HEIGHT);
		config.setTitle("fbx-conv-gui");
		new Lwjgl3Application(new FbxConvGui(), config);
	}
}