package com.macbeth.service.impl;

import com.google.common.collect.Lists;
import com.macbeth.service.FileService;
import com.macbeth.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("fileService")
public class FileServiceImpl implements FileService {
    private static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    @Override
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String fileNewName = UUID.randomUUID().toString() + extension;
        File dest = new File(path);
        if (! dest.exists()) {
            dest.setWritable(true);
            dest.mkdirs();
        }
        File targetFile = new File(path,fileNewName);
        try {
            file.transferTo(targetFile);
            FileUtils.uploadFile(Lists.newArrayList(targetFile));
            targetFile.delete();
        } catch (IOException e) {
            logger.error("文件上传失败",e);
            return null;
        }
        return fileNewName;
    }
}
