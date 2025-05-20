package com.example.expensetrackingsystem.services;


import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {


    private final MinioClient minioClient;

    @Autowired
    public ImageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createBucket(String bucketName) {
        try {

            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!exists) {

                minioClient.makeBucket( MakeBucketArgs.builder().bucket(bucketName).build() );
            }else {
                System.out.println("Bucket "+ bucketName + " already exists.");
            }


        } catch (Exception e) {
            throw new RuntimeException("Error creating bucket: " + e.getMessage(), e);
        }
    }

    public void uploadImage(String bucketName, MultipartFile file)  {

        try {

            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if(exists){
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(file.getOriginalFilename())
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
                System.out.println("Image uploaded successfully.");

            }else {
                System.out.println("Bucket "+ bucketName + " doesnt exist.");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

}
