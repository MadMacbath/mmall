package com.macbeth.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FileUtils {
    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private FTPClient ftpClient;
    private String ip = PropertiesUtils.getProperty("ftp.server.ip");
    private String user = PropertiesUtils.getProperty("ftp.user");
    private String pass = PropertiesUtils.getProperty("ftp.pass");

    public FileUtils(){
        ftpClient = new FTPClient();
    }

    public static boolean uploadFile(List<File> list){
        FileUtils fileUtils = new FileUtils();
        logger.info("开始连接到ftp服务器");
        boolean result = fileUtils.uploadFile("img",list);
        String resultStr = result ? "成功" : "失败";
        logger.info("连接ftp服务器" + resultStr + "，结束上传，上传" + resultStr);
        return result;
    }
    private boolean uploadFile(String remoteLocation, List<File> list){
        boolean result = true;
        if (connect()){
            try {
                ftpClient.changeWorkingDirectory(remoteLocation);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("utf-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File file : list){
                    try (FileInputStream fis = new FileInputStream(file)){
                        ftpClient.storeFile(file.getName(),fis);
                    }
                }
            } catch (IOException e) {
                result = false;
                logger.error("上传文件异常",e);
            } finally {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    logger.error("关闭连接失败",e);
                }
            }
        }
        return result;
    }

    public boolean connect(){
        boolean isSuccess = true;
        try {
            ftpClient.connect(ip);
//            isSuccess = ftpClient.login(user,pass);
        } catch (IOException e) {
            isSuccess = false;
            logger.error("连接到：{}，用户名：{}，密码：{}，失败",ip,user,pass);
        }
        return isSuccess;
    }

    public static void main(String[] args) throws IOException {
        FTPClient client = new FTPClient();
        client.connect("192.168.23.3",21);
//        client.changeWorkingDirectory("img");
        client.setControlEncoding("utf-8");
        client.setFileType(FTPClient.BINARY_FILE_TYPE);
//        client.enterLocalPassiveMode();
        client.setBufferSize(1024);
        client.storeFile("/img",new FileInputStream("C:/macbeth/work_subject/mmall/m-mall/src/main/webapp/upload/3a6adb9e-cb6e-4972-a263-a21d870036bf.JPG"));
        client.disconnect();
    }
}
