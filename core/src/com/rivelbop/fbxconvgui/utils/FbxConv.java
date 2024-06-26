package com.rivelbop.fbxconvgui.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Handles the command usage for fbx-conv.
 *
 * @author David Jerzak (RivelBop)
 */
public final class FbxConv {
    /**
     * Used to convert G3DJ to G3DB.
     */
    public static final G3DBConverter G3DB_CONVERTER = new G3DBConverter();

    /**
     * Stores the supported operating system types.
     */
    private enum OS_TYPE {
        NULL, // UNKNOWN
        WIN,  // Windows
        MAC,  // MacOS
        NUX   // Linux
    }

    /**
     * The current OS.
     */
    private static final OS_TYPE OS;

    static {
        // Determine OS
        String OS_NAME = System.getProperty("os.name").toLowerCase();
        if (OS_NAME.contains("win")) OS = OS_TYPE.WIN;
        else if (OS_NAME.contains("mac")) OS = OS_TYPE.MAC;
        else if (OS_NAME.contains("linux")) {
            OS = OS_TYPE.NUX;

            // Create a script for the user to run to install the necessary library file
            try {
                FileWriter fileWriter = new FileWriter("install_library_path_script.sh");
                fileWriter.write(
                        "sudo cp " + new File("binaries/linux/libfbxsdk.so").getAbsolutePath() + " /usr/lib\n" +
                                "LD_LIBRARY_PATH=/usr/lib\n" +
                                "export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:" + new File("binaries/linux/libfbxsdk.so").getAbsolutePath() + "\n" +
                                "echo $LD_LIBRARY_PATH\n"
                );
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Linux library installation script has been created!");
        } else OS = OS_TYPE.NULL;
        System.out.println("Detected OS: " + OS);
    }

    private FbxConv() {
    }

    /**
     * Converts the FBX model at the provided path into both a G3DJ and G3DB model.
     *
     * @param fbxFile The FBX file to convert.
     * @return The FBX file used (location may be altered).
     */
    public static File convertModel(File fbxFile) {
        // Generate G3DJ from FBX
        String fileName = fbxFile.getAbsolutePath();
        try {
            switch (OS) {
                case NULL:
                    System.err.println("Operating System is Unknown!");
                    return fbxFile;
                case WIN:
                    Runtime.getRuntime().exec("binaries/win/fbx-conv.exe -f -o G3DJ " + fileName).waitFor();
                    break;
                case MAC:
                    Runtime.getRuntime().exec("binaries/mac/fbx-conv -f -o G3DJ " + fileName).waitFor();
                    break;
                case NUX:
                    Runtime.getRuntime().exec("binaries/linux/fbx-conv -f -o G3DJ " + fileName).waitFor();
                    break;
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        // Get available files to alter
        File directory = new File(fileName.replace(".fbx", ".fbm"));
        File g3djFile = new File(fileName.replace(".fbx", ".g3dj"));
        String directoryPath = directory.getAbsolutePath() + "/";

        // If no asset folder was detected, convert at the FBX file location
        if (!directory.isDirectory()) {
            try {
                G3DB_CONVERTER.convert(Gdx.files.absolute(g3djFile.getAbsolutePath()), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Converted FBX Model: " + fileName);
            return fbxFile;
        }

        // Move the G3DJ file into the new folder
        File newG3djFile = new File(directoryPath + g3djFile.getName());
        if (g3djFile.renameTo(newG3djFile))
            System.out.println(newG3djFile.getName() + " -> " + directoryPath);

        // Move the FBX file into the new folder
        File newFbxFile = new File(directoryPath + fbxFile.getName());
        if (fbxFile.renameTo(newFbxFile))
            System.out.println(newFbxFile.getName() + " -> " + directoryPath);
        fileName = newFbxFile.getAbsolutePath();

        // Generate G3DB file
        try {
            G3DB_CONVERTER.convert(Gdx.files.absolute(newG3djFile.getAbsolutePath()), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Converted FBX Model: " + fileName);
        return newFbxFile;
    }

    /**
     * Combines 2 G3DJ files into one with the specified path name.
     *
     * @param g3dj_1 G3DJ file to combine into.
     * @param g3dj_2 G3DJ file to combine from.
     * @return The newly combined G3DJ file.
     */
    public static File combineG3DJ(FileHandle g3dj_1, FileHandle g3dj_2) {
        // Parse the g3dj data
        JsonReader reader = new JsonReader();
        JsonValue v1 = reader.parse(g3dj_1);
        JsonValue v2 = reader.parse(g3dj_2);

        // Put the animation from g3dj_2 into g3dj_1
        v1.get("animations").addChild(v2.get("animations").child);

        // Write the new data for model1 back into its G3DJ file
        File combinedG3djFile = new File(g3dj_1.pathWithoutExtension() + "_" + g3dj_2.nameWithoutExtension() + ".g3dj");
        try {
            FileWriter writer = new FileWriter(combinedG3djFile);
            writer.write(v1.prettyPrint(new JsonValue.PrettyPrintSettings() {{
                outputType = JsonWriter.OutputType.json;
                wrapNumericArrays = true;
            }}));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(g3dj_1.name() + " added to " + g3dj_2.name());
        return combinedG3djFile;
    }

    /**
     * Renames the animation of a g3dj file.
     *
     * @param g3dj          The file from which the animation should be renamed.
     * @param animationName The animation to rename.
     * @param newName       The new name of the animation.
     */
    public static void renameAnimation(FileHandle g3dj, String animationName, String newName) {
        JsonValue g3djValue = new JsonReader().parse(g3dj);
        JsonValue animationValue = g3djValue.get("animations");

        for (JsonValue jsonValue : animationValue) {
            JsonValue value = jsonValue.get("id");
            if (value.asString().equals(animationName)) {
                value.set(newName);

                try {
                    FileWriter writer = new FileWriter(g3dj.path());
                    writer.write(g3djValue.prettyPrint(new JsonValue.PrettyPrintSettings() {{
                        outputType = JsonWriter.OutputType.json;
                        wrapNumericArrays = true;
                    }}));
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Successfully renamed G3DJ animation to: " + newName);
                return;
            }
        }
    }

    /**
     * Parses the provided g3dj file and returns a list of all the animation names.
     *
     * @param g3dj The g3dj file to parse/read from.
     * @return List of animation names.
     */
    public static Array<String> parseAnimations(FileHandle g3dj) {
        JsonValue g3djValue = new JsonReader().parse(g3dj).get("animations");
        Array<String> animationNames = new Array<>(g3djValue.size);

        for (JsonValue animation : g3djValue)
            animationNames.add(animation.get("id").asString());

        return animationNames;
    }
}