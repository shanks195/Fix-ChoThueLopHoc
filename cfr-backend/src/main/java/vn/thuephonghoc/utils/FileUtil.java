package vn.thuephonghoc.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import vn.thuephonghoc.exception.BadRequestException;

import org.apache.poi.util.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * File tool class, extend the hutool toolkit
 */
public class FileUtil extends cn.hutool.core.io.FileUtil {
    /**
     * Define the calculation constant for GB
     */
    private static final int GB = 1024 * 1024 * 1024;
    /**
     * Define the calculation constant of MB
     */
    private static final int MB = 1024 * 1024;
    /**
     * Define the calculation constant of KB
     */
    private static final int KB = 1024;

    /**
     * Format decimal
     */
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    /**
     * MultipartFile to File
     */
    public static File toFile(MultipartFile multipartFile){
        // Get the file name
        String fileName = multipartFile.getOriginalFilename();
        // Get the file suffix
        String prefix="."+getExtensionName(fileName);
        File file = null;
        try {
            // Use uuid as the file name to prevent duplication of generated temporary files
            file = File.createTempFile(IdUtil.simpleUUID(), prefix);
            // MultipartFile to File
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Get the file extension without.
     * @param filename
     * @return
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length()> 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot <(filename.length()-1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * Java file operation Get the file name without extension
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length()> 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot <(filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * File size conversion
     */
    public static String getSize(long size){
        String resultSize;
        if (size / GB >= 1) {
            //If the current Byte value is greater than or equal to 1GB
            resultSize = DF.format(size / (float) GB) + "GB";
        } else if (size / MB >= 1) {
            //If the current Byte value is greater than or equal to 1MB
            resultSize = DF.format(size / (float) MB) + "MB";
        } else if (size / KB >= 1) {
            //If the current Byte value is greater than or equal to 1KB
            resultSize = DF.format(size / (float) KB) + "KB";
        } else {
            resultSize = size + "B";
        }
        return resultSize;
    }

    /**
     * inputStream to File
     */
    static File inputStreamToFile(InputStream ins, String name) throws Exception{
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + name);
        if (file.exists()) {
            return file;
        }
        OutputStream os = new FileOutputStream(file);
        int bytesRead;
        int len = 8192;
        byte[] buffer = new byte[len];
        while ((bytesRead = ins.read(buffer, 0, len)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        return file;
    }
    
    /**
     * Parse the file name into the upload path of the file
     */
    public static File upload(MultipartFile file, String filePath) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmssS");
        String name = getFileNameNoEx(file.getOriginalFilename());
        String suffix = getExtensionName(file.getOriginalFilename());
        String nowStr = "-" + format.format(date);
        try {
            String fileName = name + nowStr + "." + suffix;
            String path = filePath + fileName;
            // getCanonicalFile can parse correct various paths
            File dest = new File(path).getCanonicalFile();
            // Check if the directory exists
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            // File writing
            file.transferTo(dest);
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String fileToBase64(File file) throws Exception {
        FileInputStream inputFile = new FileInputStream(file);
        String base64;
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        base64=Base64.encode(buffer);
        return base64.replaceAll("[\\s*\t\n\r]", "");
    }

    /**
     * excel
     */
    public static void downloadExcel(List<Map<String, Object>> list, HttpServletResponse response) throws IOException {
        String tempPath =System.getProperty("java.io.tmpdir") + IdUtil.fastSimpleUUID() + ".xlsx";
        File file = new File(tempPath);
        BigExcelWriter writer= ExcelUtil.getBigWriter(file);
     // Write out the content at once, use the default style, and force the title to be output
        writer.write(list, true);
        //response is the HttpServletResponse object
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        //test.xls is the file name of the pop-up download dialog box. It cannot be in Chinese. Please encode the Chinese by yourself
        response.setHeader("Content-Disposition","attachment;filename=file.xlsx");
        ServletOutputStream out=response.getOutputStream();
        // delete temporary files after termination
        file.deleteOnExit();
        writer.flush(out, true);
        //Remember to close the output Servlet stream here
        IoUtil.close(out);
    }

    public static String getFileType(String type) {
        String documents = "txt doc pdf ppt pps xlsx xls docx";
        String music = "mp3 wav wma mpa ram ra aac aif m4a";
        String video = "avi mpg mpe mpeg asf wmv mov qt rm mp4 flv m4v webm ogv ogg";
        String image = "bmp dib pcp dif wmf gif jpg tif eps psd cdr iff tga pcd mpt png jpeg";
        if(image.contains(type)){
        	return "Picture";
        } else if(documents.contains(type)){
            return "Document";
        } else if(music.contains(type)){
            return "Music";
        } else if(video.contains(type)){
            return "video";
        } else {
            return "Other";
        }
    }

    public static String getFileTypeByMimeType(String type) {
        String mimeType = new MimetypesFileTypeMap().getContentType("." + type);
        return mimeType.split("/")[0];
    }
    public static void checkSize(long maxSize, long size) {
        // 1M
        int len = 1024 * 1024;
        if(size > (maxSize * len)){
            throw new BadRequestException("File exceeds the specified size");
        }
    }

    /**
     * two files are the same
     */
    public static boolean check(File file1, File file2) {
        String img1Md5 = getMd5(file1);
        String img2Md5 = getMd5(file2);
        return img1Md5.equals(img2Md5);
    }

    /**
     * two files are the same
     */
    public static boolean check(String file1Md5, String file2Md5) {
        return file1Md5.equals(file2Md5);
    }

    private static byte[] getByte(File file) {
    	
        byte[] b = new byte[(int) file.length()];
        try {
            InputStream in = new FileInputStream(file);
            try {
                in.read(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return b;
    }

    /**
     * @param request /
     * @param response /
     * @param file /
     */
    public static void downloadFile(HttpServletRequest request, HttpServletResponse response, File file, boolean deleteOnExit){
        response.setCharacterEncoding(request.getCharacterEncoding());
        response.setContentType("application/octet-stream");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            response.setHeader("Content-Disposition", "attachment; filename="+file.getName());
            IOUtils.copy(fis,response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    if(deleteOnExit){
                        file.deleteOnExit();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static String getMd5(byte[] bytes) {
        // Hexadecimal characters
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(bytes);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            // Shift output string
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMd5(File file) {
        return getMd5(getByte(file));
    }
}
