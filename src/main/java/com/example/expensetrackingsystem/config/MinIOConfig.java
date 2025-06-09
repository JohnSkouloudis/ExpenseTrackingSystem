package com.example.expensetrackingsystem.config;

import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIOConfig {

    Logger logger = LoggerFactory.getLogger(MinIOConfig.class);

    @Value("${MINIO_URL}")
    private String URL;

    @Value("${MINIO_USERNAME}")
    private String username;

    @Value("${MINIO_PASSWORD}")
    private String password;

    @Bean
    public MinioClient minioClient()  {
        return MinioClient.builder()
                .endpoint(URL)
                .credentials(username, password)
                .build();
    }

}
