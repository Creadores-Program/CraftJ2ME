package com.holdfast.mbide.j2mecompiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author HoldFast
 */
public class Compiler {

    long time1, time2;

    void Compiler() {

    }

    void compile(String apipath, String srcpath, String outpath) throws Exception {
        File file = new File(outpath);
        file.mkdir();
        File[] listOfFiles = file.listFiles();
        for (File fil : listOfFiles) {
            if (fil.isFile()) {
                fil.delete();
            }
        }
        ProcessBuilder pb = new ProcessBuilder("sh", "-c",
            "find " + srcpath + " -name '*.java' | xargs javac -cp " + apipath +
            " -target 1.1 -source 1.3 -nowarn -encoding UTF-8 -d " + outpath);
        Process p = pb.start();

        time1 = System.currentTimeMillis();

        StringBuilder console = new StringBuilder();
        InputStream stream = p.getErrorStream();
        try {
            int read;
            byte[] buf = new byte[1024 * 99];
            while ((read = stream.read(buf)) > 0) {
                console.append(new String(buf, 0, read));
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        p.waitFor();

        final String result = console.toString().trim();
        System.out.println("javac:");
        System.out.println(result);

        p.waitFor();

        File folder = new File(outpath);
        listOfFiles = folder.listFiles();
        if (listOfFiles.length == 0) {
            throw new Exception("Compilation error");
        }

        File f = new File("prebuild" + File.separator + "temp.jar");
        f.mkdirs();
        f.delete();
        copyResources(new File(srcpath), new File(outpath));

        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(f));

        zip.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
        zip.write(("Manifest-Version: 1.0\n"
                + "Ant-Version: Apache Ant 1.9.4\n"
                + "Created-By: "+System.getProperty("java.version")+" ("+System.getProperty("java.vendor")+")\n"
                + "Built-By: "+ System.getProperty("user.name")+"\n"
                + "Build-Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n"
                + "MIDlet-1: CraftJ2ME, /icon.png, org.CreadoresProgram.CraftJ2ME.Main\n"
                + "MIDlet-Vendor: Creadores Program\n"
                + "MIDlet-Version: 1.0.0-beta\n"
                + "MIDlet-Name: CraftJ2ME\n"
                + "MicroEdition-Configuration: CLDC-1.1\n"
                + "MicroEdition-Profile: MIDP-2.0").getBytes("UTF-8"));
        zip.closeEntry();

        addFilesToZip(new File(outpath), zip, "");
        zip.close();
        previrify("prebuild" + File.separator + "temp.jar", "CraftJ2ME.jar");

    }

    void previrify(String injar, String outjar) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "proguard" + File.separator + "proguard.jar",
                "-injars", injar, "-outjars", outjar,
                "-libraryjars", "proguard" + File.separator + "cldcapi11.jar", "-libraryjars", "proguard" + File.separator + "midpapi20.jar", "-microedition",
                "-keep", "public class * extends javax.microedition.midlet.MIDlet", "-dontoptimize");
        System.out.println("\nproguard:");
        Process p = pb.start();
        StringBuilder console = new StringBuilder();
        InputStream stream = p.getInputStream();
        try {
            int read;
            byte[] buf = new byte[1024 * 99];
            while ((read = stream.read(buf)) > 0) {
                console.append(new String(buf, 0, read));
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        StringBuilder errorConsole = new StringBuilder();
        InputStream errorStream = p.getErrorStream();
        try {
            int read;
            byte[] buf = new byte[1024 * 99];
            while ((read = errorStream.read(buf)) > 0) {
                errorConsole.append(new String(buf, 0, read));
            }
        } finally {
            if (errorStream != null) {
                errorStream.close();
            }
        }

        p.waitFor();
        time2 = System.currentTimeMillis();
        long difference = time2 - time1;
        java.util.Date differneceDate = new Date(difference);

        final String result = console.toString().trim();
        final String errorResult = errorConsole.toString().trim();
        System.out.println(result);
        if (!errorResult.isEmpty()) {
            System.out.println("Errors:");
            System.out.println(errorResult);
        }
        System.out.println("Assembly completed in " + getTime(differneceDate));
    }

    private String getTime(Date date) {
        return (new SimpleDateFormat("mm:ss")).format(date);
    }
    private void addFilesToZip(File folder, ZipOutputStream zip, String basePath) throws Exception {
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                addFilesToZip(file, zip, basePath + file.getName() + "/");
            } else if (file.isFile()) {
                System.out.println(basePath + file.getName());
                zip.putNextEntry(new ZipEntry(basePath + file.getName()));
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[4092];
                int byteCount;
                while ((byteCount = fis.read(buffer)) != -1) {
                    zip.write(buffer, 0, byteCount);
                    System.out.print('.');
                    System.out.flush();
                }
                fis.close();
                zip.closeEntry();
            }
        }
    }
    private void copyResources(File src, File dest) throws Exception {
        File[] files = src.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                File newDest = new File(dest, file.getName());
                newDest.mkdir();
                copyResources(file, newDest);
            } else if (!file.getName().endsWith(".java")) {
                FileInputStream in = new FileInputStream(file);
                FileOutputStream out = new FileOutputStream(new File(dest, file.getName()));
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }
        }
    }
}
