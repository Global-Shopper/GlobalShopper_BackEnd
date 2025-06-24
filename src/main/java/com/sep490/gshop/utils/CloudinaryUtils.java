package com.sep490.gshop.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sep490.gshop.config.handler.AppException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Service
@Log4j2
public class CloudinaryUtils {
    private final Cloudinary cloudinary;

    public CloudinaryUtils(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }


    public String uploadFile(MultipartFile file, Map<String, Object> options) {
        try {
            log.debug("uploadFile() CloudinaryUtils start | file: {}", file.getOriginalFilename());
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            log.debug("uploadFile() CloudinaryUtils end | {}", uploadResult);
            return (String) uploadResult.get("url");
        } catch (Exception e) {
            log.error("uploadFile() CloudinaryUtils error | {}", e.getMessage());
            throw new AppException(400, "Lỗi khi upload file");
        }
    }
}
