package com.watabou;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//a class so I can debug on desktop more efficiently that throwing exceptions everywhere (sometimes even randomly)
public class DebugOnDesktop {

    public static BufferedWriter out;
    private static File f;

    private static void init() {
        try {
            f = new File(System.getProperty("user.home"), "Debug-SandboxPD.txt");
            if (!f.exists()) f.createNewFile();
        } catch (Exception ex) {
        }
    }

    public static void prepare() {
        if (f == null) init();
        try {
            out = new BufferedWriter(new FileWriter(f, true));
        } catch (IOException e) {
        }
    }

    public static void stop() {
        if (out != null) {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void printException(Exception ex) {
        DebugOnDesktop.prepare();
        try {
            out.write("START: " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + "\n");
        } catch (IOException e) {
        }
        for (StackTraceElement st : ex.getStackTrace()) {
            try {
                DebugOnDesktop.out.write(st.getFileName() + " at " + st.getLineNumber() + "\n");
            } catch (IOException e) {
            }
        }
        DebugOnDesktop.stop();
    }

}