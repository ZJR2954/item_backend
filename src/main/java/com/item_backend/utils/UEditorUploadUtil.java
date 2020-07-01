package com.item_backend.utils;

import com.item_backend.model.dto.UEditorFile;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @Author xiao
 * @Time 2020/5/12
 * @Describe UEditor上传文件工具类
 **/

@Component
public class UEditorUploadUtil {
    private String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();

    @Value("${qiniu.accessKey}")
    private String accessKey ;
    @Value("${qiniu.secretKey}")
    private String secretKey ;
    @Value("${qiniu.bucketName}")
    private String bucketName ;
    @Value("${qiniu.fileDomain}")
    private String fileDomain;

    private UploadManager uploadManager;
    private BucketManager bucketManager;
    private Configuration c;
    private Client client;
    // 密钥配置
    private Auth auth;

    public UEditorFile uploadImage(MultipartFile file) throws IOException {
        //获取文件名
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        //获取系统时间组成文件保存路径
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String time = "" + dateFormat.format(new Date());
        //UEditor的config.json规定的返回路径格式
//        String returnPath = "/image/ueditor/" + time + "/" + fileName;
        //保存路径
//        String saveDir = path + "static/image/ueditor/" + time + "/";
//        File saveDirFile = new File(saveDir);
        //保存路径+文件名
//        File saveFile = new File(saveDir + fileName);
        //没有保存路径时创建该文件夹
//        if (!saveDirFile.exists()) {
//            saveDirFile.mkdirs();
//        }
        //将文件写入
//        if (file.getSize() > 0) {
//            InputStream ins = file.getInputStream();
//            try {
//                OutputStream os = new FileOutputStream(saveFile);
//                int bytesRead;
//                byte[] buffer = new byte[8912];
//                while ((bytesRead = ins.read(buffer, 0, 8912)) != -1) {
//                    os.write(buffer, 0, bytesRead);
//                }
//                os.close();
//                ins.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        //七牛云上传
        String returnPath = upload(file, fileName);

        UEditorFile uEditorFile = new UEditorFile();
        uEditorFile.setState("SUCCESS");
        //用于被前台访问的URL
        uEditorFile.setUrl(returnPath);
        uEditorFile.setTitle(fileName);
        uEditorFile.setOriginal(fileName);
        return uEditorFile;
    }

    public String upload(MultipartFile file, String fileKey) throws IOException {
        Response res;
        try {
            res = getUploadManager().put(file.getInputStream(), fileKey, getUpToken(fileKey), null, null);
            return fileDomain + "/" + fileKey;
        } catch (QiniuException e) {
            res = e.response;
            e.printStackTrace();
            return "上传失败";
        }
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getFileDomain() {
        return fileDomain;
    }

    public Client getClient(){
        if (client==null) {
            client=new Client(getConfiguration());
        }
        return client;
    }

    public BucketManager getBucketManager() {
        if (bucketManager == null) {
            bucketManager = new BucketManager(getAuth(), getConfiguration());
        }
        return bucketManager;
    }

    public UploadManager getUploadManager() {
        if (uploadManager == null) {
            uploadManager = new UploadManager(getConfiguration());
        }
        return uploadManager;
    }

    public Configuration getConfiguration() {
        if (c == null) {
            Zone z = Zone.autoZone();
            c = new Configuration(z);
        }
        return c;
    }

    public Auth getAuth() {
        if (auth == null) {
            auth = Auth.create(getAccessKey(), getSecretKey());
        }
        return auth;
    }
    //简单上传模式的凭证
    public String getUpToken() {
        return getAuth().uploadToken(getBucketName());
    }
    //覆盖上传模式的凭证
    public String getUpToken(String fileKey) {
        return getAuth().uploadToken(getBucketName(), fileKey);
    }
}
