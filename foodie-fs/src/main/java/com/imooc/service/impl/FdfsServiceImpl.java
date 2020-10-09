package com.imooc.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.imooc.resource.FileResource;
import com.imooc.service.FdfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class FdfsServiceImpl implements FdfsService {

    @Autowired
    FastFileStorageClient fastFileStorageClient;

    @Autowired
    FileResource fileResource;

    @Override
    public String upload(MultipartFile file, String fileExtName) throws Exception {
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(),
                file.getSize(),
                fileExtName,
                null);
        String fullPath = storePath.getFullPath();
        return fullPath;
    }

    @Override
    public String uploadOSS(MultipartFile file, String userId, String fileExtName) throws Exception {
        OSS client = new OSSClientBuilder().build(
                fileResource.getEndpoint(),
                fileResource.getAccessKeyId(),
                fileResource.getAccessKeySecret());
        InputStream inputStream = file.getInputStream();
        String myObjectName = fileResource.getObjectName() + "/" + userId + "/" + userId + "." + fileExtName;
        client.putObject(
                fileResource.getBucketName(),
                myObjectName,
                inputStream);
        client.shutdown();
        return fileResource.getOssHost() + myObjectName;
    }
}
