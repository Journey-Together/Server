package Journey.Together.global.util;

import Journey.Together.global.exception.ApplicationException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import Journey.Together.global.exception.ErrorCode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Client {

    private final AmazonS3 amazonS3Client;
    @Value("${aws-property.s3-bucket-name}")
    private String bucket;

    @Value("${aws-property.baseUrl}")
    private String baseUrl;

    public String createFolder(){
        String folderName = UUID.randomUUID().toString();
        amazonS3Client.putObject(bucket, folderName + "/", new ByteArrayInputStream(new byte[0]), new ObjectMetadata());

        return folderName;
    }

    public String upload(MultipartFile multipartFile, String folderName, String imageName) {
        // Validation
        if(multipartFile.isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_VALUE_EXCEPTION);
        }

        // Business Logic
        String url = folderName+"/"+imageName;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        // Check File upload
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, url, multipartFile.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Response
        return uuid;
    }

    public String update(String fileName, MultipartFile newFile) {
        // Validation
        if(newFile.isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_VALUE_EXCEPTION);
        }

        // Business Logic
        // 기존 파일이 존재하는지 확인
        if(!amazonS3Client.doesObjectExist(bucket, fileName)) {
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }

        // 새 파일 메타데이터 설정
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(newFile.getContentType());
        objectMetadata.setContentLength(newFile.getSize());

        // 새 파일 업로드
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, newFile.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.INTERNAL_SERVER_EXCEPTION);
        }

        // Response
        return fileName;
    }

    public String getUrl(){
        return baseUrl;
    }

    public void delete(String fileName) {
        // Validation
        if(!amazonS3Client.doesObjectExist(bucket, fileName)) {
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }

        // Business Logic
        try {
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.INTERNAL_SERVER_EXCEPTION);
        }
    }


}
