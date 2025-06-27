package com.sep490.gshop.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sep490.gshop.config.handler.AppException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Service
@Log4j2
@Configuration
public class CloudinaryConfig {
    @Value("${cloudinary.cloud-name}")
    private String cloudName;
    @Value("${cloudinary.api-key}")
    private String apiKey;
    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }


    public String uploadFile(MultipartFile file, Map<String, Object> options) {
        try {
            log.debug("uploadFile() CloudinaryUtils start | file: {}", file.getOriginalFilename());
            Map uploadResult = cloudinary().uploader().upload(file.getBytes(), options);
            log.debug("uploadFile() CloudinaryUtils end | {}", uploadResult);
            return (String) uploadResult.get("url");
        } catch (Exception e) {
            log.error("uploadFile() CloudinaryUtils error | {}", e.getMessage());
            throw new AppException(400, "Lỗi khi upload file");
        }
    }
}
