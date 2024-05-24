package com.rivelbop.fbxconvgui;

import com.badlogic.gdx.Gdx;

import java.io.IOException;

public final class FbxConv {
    private static final G3DBConverter G3DB_CONVERTER = new G3DBConverter();

    private enum OS_TYPE {
        NULL,
        WIN, // Windows
        MAC, // MacOS
        NUX  // Linux
    }

    private static final OS_TYPE OS;

    static {
        String OS_NAME = System.getProperty("os.name").toLowerCase();
        if (OS_NAME.contains("win"))
            OS = OS_TYPE.WIN;
        else if (OS_NAME.contains("mac"))
            OS = OS_TYPE.MAC;
        else if (OS_NAME.contains("linux"))
            OS = OS_TYPE.NUX;
        else
            OS = OS_TYPE.NULL;
    }

    private FbxConv() {
    }

    public static void convertModel(String fileName) throws IOException, InterruptedException {
        switch (OS) {
            case NULL:
                System.err.println("Operating System Is Unknown!");
                return;
            case WIN:
                Runtime.getRuntime().exec("binaries/win/fbx-conv.exe -f -o G3DJ " + fileName).waitFor();
            case MAC:
                Runtime.getRuntime().exec("binaries/mac/fbx-conv -f -o G3DJ " + fileName).waitFor();
            case NUX:
                Runtime.getRuntime().exec("binaries/linux/fbx-conv -f -o G3DJ " + fileName).waitFor();
        }
        G3DB_CONVERTER.convert(Gdx.files.absolute(fileName.replace(".fbx", ".g3dj")), true);
    }
}