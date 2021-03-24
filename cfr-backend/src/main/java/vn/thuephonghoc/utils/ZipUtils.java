package vn.thuephonghoc.utils;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


public class ZipUtils {
	/**
     * unzip files
     *
     * @param zipFilePath Unzip file path
     * @param outputFolder output decompressed file path
     */
    public static void unZipIt(String zipFilePath, String outputFolder) {
        byte[] buffer = new byte[1024];

        File folder = new File(outputFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }
        try {
            //get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);
                System.out.println("file unzip : " + newFile.getAbsoluteFile());
             // Most of the source code on the Internet, there is no judgment subdirectory here
                if (ze.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unzip(File source, String out) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source))) {

            ZipEntry entry = zis.getNextEntry();

            while (entry != null) {

                File file = new File(out, entry.getName());

                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();

                    if (!parent.exists()) {
                        parent.mkdirs();
                    }

                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {

                        byte[] buffer = new byte[Math.toIntExact(entry.getSize())];

                        int location;

                        while ((location = zis.read(buffer)) != -1) {
                            bos.write(buffer, 0, location);
                        }
                    }
                }
                entry = zis.getNextEntry();
            }
        }
    }

    /**
     * Extract all files directly to the specified directory (ignoring subfolders)
     *
     * @param zipFile
     * @param folderPath
     * @throws ZipException
     * @throws IOException
     */
    public static void upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            InputStream in = zf.getInputStream(entry);
            String str = folderPath;
            File desFile = new File(str, java.net.URLEncoder.encode(entry.getName(), "UTF-8"));

            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
            }

            OutputStream out = new FileOutputStream(desFile);
            byte[] buffer = new byte[1024 * 1024];
            int realLength = in.read(buffer);
            while (realLength != -1) {
                out.write(buffer, 0, realLength);
                realLength = in.read(buffer);
            }

            out.close();
            in.close();

        }
    }
}
