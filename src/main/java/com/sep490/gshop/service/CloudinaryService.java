package com.sep490.gshop.service;

import com.sep490.gshop.payload.response.CloudinaryResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    CloudinaryResponse uploadImage(MultipartFile file, String fileName);
}
