package com.sep490.gshop.utils;

import com.sep490.gshop.common.constants.ErrorCode;
import com.sep490.gshop.config.handler.ErrorException;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class FileUploadUtil {

    public static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";

    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    public static final String FILE_NAME_FORMAT = "%s_%s";

    public static boolean isAllowedExtension(final String fileName, final String pattern) {
        final Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(fileName);
        return matcher.matches();
    }
    public static void AssertAllowedExtension(MultipartFile file, String pattern) {
        final String fileName = file.getOriginalFilename();
        if (fileName == null || !isAllowedExtension(fileName, pattern)) {
            throw new ErrorException("Chỉ chấp nhận file đuôi jpg, png, gif, bmp", ErrorCode.EXCEED_FILE_SIZE, 400);
        }
    }

    public static String formatFileName(final String fileName) {
        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        final String date = dateFormat.format(new Date());

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            return String.format(FILE_NAME_FORMAT, date, fileName);
        }

        String namePart = fileName.substring(0, dotIndex);
        System.out.println(namePart);
        return String.format("%s_%s", date, namePart);
    }

}
