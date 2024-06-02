package Journey.Together.global.util;

import Journey.Together.global.exception.ApplicationException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import Journey.Together.global.exception.ErrorCode;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Client {

    private final AmazonS3 amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.baseUrl}")
    private String baseUrl;

    public String upload(MultipartFile multipartFile) {
        // Validation
        if(multipartFile.isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_VALUE_EXCEPTION);
        }

        // Business Logic
        String uuid = UUID.randomUUID().toString();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        // Check File upload
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, uuid, multipartFile.getInputStream(), objectMetadata)
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

    public S3ObjectInputStream get(String fileName){
        // Validation
        if(!amazonS3Client.doesObjectExist(bucket,fileName)) {
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }

        // Business Logic
        S3Object s3Object = amazonS3Client.getObject(bucket,fileName);
        if(s3Object.getObjectContent() !=null){
            System.out.println(s3Object.getObjectContent());
        }
        // Response
        return s3Object.getObjectContent();
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
