package com.sep490.gshop.service.implement;

import com.cloudinary.Cloudinary;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.payload.response.CloudinaryResponse;
import com.sep490.gshop.service.CloudinaryService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@Log4j2
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    @Transactional
    public CloudinaryResponse uploadImage(MultipartFile file, String fileName) {
        log.debug("uploadImage() start | fileName: {}, originalFileName: {}", fileName, file.getOriginalFilename());
        try {
            if (file == null || file.isEmpty()) {
                throw AppException.builder().message("Vui lòng chọn file để upload").code(400).build();
            }
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), Map.of("public_id", fileName));

            String imageUrl = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            if (imageUrl == null || publicId == null) {
                throw AppException.builder().message("Upload thất bại: không nhận được URL hoặc publicId từ Cloudinary").code(409).build();

            }

            log.debug("uploadImage() success | publicId: {}, imageUrl: {}", publicId, imageUrl);

            return CloudinaryResponse.builder()
                    .publicId(publicId)
                    .responseURL(imageUrl)
                    .build();

        }catch (Exception e) {
            log.error("uploadImage() upload error | message: {}", e.getMessage());
            throw new RuntimeException("Lỗi khi upload ảnh lên Cloudinary: " + e.getMessage());
        }
    }




}
