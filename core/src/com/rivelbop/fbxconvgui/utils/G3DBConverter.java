package com.rivelbop.fbxconvgui.utils;

/*
 * Copyright 2014 Questing Software
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.ValueType;
import com.badlogic.gdx.utils.UBJsonWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Batch converter to load G3DJ files and save them in G3DB (binary) format.
 *
 * @author Danilo Costa Viana
 */
public class G3DBConverter {
    /**
     * <p>
     * Convert all G3DJ files inside a folder to G3DB format (binary JSON).
     * </p>
     *
     * @param g3djFolder Folder containing files with the {@code g3dj} extension. Ths method will not recursively navigate folders.
     * @param overwrite  If {@code true} and there's already a file with the same name and the {@code g3db} extension, the file will
     *                   be overwritten. Otherwise a counter will be appended at the end of the file.
     * @throws IOException If there's an exception while reading the input file or writing the output file.
     */
    public void convertFolder(FileHandle g3djFolder, boolean overwrite) throws IOException {
        ArrayList<FileHandle> g3djFiles = new ArrayList<>();

        if (g3djFolder != null) {
            if (g3djFolder.isDirectory()) {
                for (FileHandle handle : g3djFolder.list()) {
                    if (handle.name().toLowerCase().endsWith(".g3dj")) {
                        g3djFiles.add(handle);
                    }
                }
            } else if (g3djFolder.name().toLowerCase().endsWith(".g3dj")) {
                FileHandle g3djFile = g3djFolder;
                g3djFolder = g3djFolder.parent();
                g3djFiles.add(g3djFile);
            }
        }

        for (FileHandle handle : g3djFiles) {
            convert(handle, overwrite);
        }
    }

    /**
     * <p>
     * Convert a text JSON file into binary JSON. The new file will be saved in the same folder as the original one with the
     * {@code gd3b} extension.
     * </p>
     *
     * @param g3djFile  Handle to the original G3DJ file.
     * @param overwrite If {@code true} the new file will overwrite any previous file with the same name. Otherwise append a
     *                  counter at the end of the file name to make it unique.
     * @throws IOException If there's an exception while reading the input file or writing the output file.
     */
    public void convert(FileHandle g3djFile, boolean overwrite) throws IOException {
        FileHandle newFile = new FileHandle(g3djFile.pathWithoutExtension() + ".g3db");
        int noOverwriteCounter = 0;
        while (!overwrite && newFile.exists()) {
            newFile = new FileHandle(g3djFile.pathWithoutExtension() + "(" + (++noOverwriteCounter) + ").g3db");
        }

        OutputStream fileOutputStream = newFile.write(false);

        try (UBJsonWriter writer = new UBJsonWriter(fileOutputStream)) {
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(g3djFile);
            writeObject(root, writer);

        }
    }

    /**
     * Writes an array or object into binary JSON. Can be recursively called for inner objects/arrays.
     */
    private void writeObject(JsonValue root, UBJsonWriter writer) throws IOException {
        if (root.type() == ValueType.array) {
            if (root.name() != null) {
                writer.array(root.name());
            } else {
                writer.array();
            }
        } else {
            if (root.name() != null) {
                writer.object(root.name());
            } else {
                writer.object();
            }
        }

        JsonValue child = root.child();
        while (child != null) {
            switch (child.type()) {
                case booleanValue:
                    if (child.name() != null) {
                        writer.set(child.name(), child.asBoolean());
                    } else {
                        writer.value(child.asBoolean());
                    }
                    break;

                case doubleValue:
                    if (child.name() != null) {
                        writer.set(child.name(), child.asDouble());
                    } else {
                        writer.value(child.asDouble());
                    }
                    break;

                case longValue:
                    if (child.name() != null) {
                        writer.set(child.name(), child.asLong());
                    } else {
                        writer.value(child.asLong());
                    }
                    break;

                case stringValue:
                    if (child.name() != null) {
                        writer.set(child.name(), child.asString());
                    } else {
                        writer.value(child.asString());
                    }
                    break;

                case nullValue:
                    if (child.name() != null) {
                        writer.set(child.name());
                    }
                    break;

                case array:
                case object:
                    writeObject(child, writer);
                    break;
            }

            child = child.next();
        }

        writer.pop();
    }
}