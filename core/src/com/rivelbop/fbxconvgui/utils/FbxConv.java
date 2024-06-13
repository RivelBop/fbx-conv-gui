package com.rivelbop.fbxconvgui.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
                System.out.println("Linux library installation script has been created!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else OS = OS_TYPE.NULL;
        System.out.println("Detected OS: " + OS);
    }

    private FbxConv() {
    }

    /**
     * Converts the FBX model at the provided path into both a G3DJ and G3DB model.
     *
     * @param fbxFile The FBX file to convert.
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
                System.out.println("Converted FBX Model: " + fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
            System.out.println("Converted FBX Model: " + fileName);
            return newFbxFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Combines 2 G3DJ files into one with the specified path name.
     *
     * @param v1   G3DJ file to combine into.
     * @param v2   G3DJ file to combine from.
     * @param name G3DJ output file path.
     */
    public static void combineG3DJ(JsonValue v1, JsonValue v2, FileHandle name) {
        // Put the animation from model2 into model1
        v1.get("animations").addChild(v2.get("animations").child);

        // Write the new data for model1 back into its G3DJ file
        try {
            FileWriter writer = new FileWriter(name.path());
            writer.write(v1.prettyPrint(new JsonValue.PrettyPrintSettings() {{
                outputType = JsonWriter.OutputType.json;
                wrapNumericArrays = true;
            }}));
            writer.close();
            System.out.println(v1.name() + " added to " + v2.name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Renames the animation of the provided G3DJ file path.
     *
     * @param name  The file path to the G3DJ file.
     * @param value The name to set the animation to.
     */
    public static void renameAnimation(FileHandle name, String value) {
        JsonValue g3dj = new JsonReader().parse(name);
        JsonValue animation = g3dj.get("animations").child;

        if (animation != null) {
            animation.get("id").set(value);
            try {
                FileWriter writer = new FileWriter(name.path());
                writer.write(g3dj.prettyPrint(new JsonValue.PrettyPrintSettings() {{
                    outputType = JsonWriter.OutputType.json;
                    wrapNumericArrays = true;
                }}));
                writer.close();
                System.out.println("Successfully renamed G3DJ animation to: " + value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}