package com.sep490.gshop.entity.converter;

import com.sep490.gshop.entity.subclass.Fee;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Converter
public class FeeListConverter implements AttributeConverter<List<Fee>, String> {
    private static final String ITEM_SPLIT = ";";
    private static final String FIELD_SPLIT = "\\|";
    @Override
    public String convertToDatabaseColumn(List<Fee> feeList) {
        if (feeList == null || feeList.isEmpty()) return "";
        // Try-catch toàn bộ tránh lỗi
        try {
            return feeList.stream()
                    .map(f -> (f.getFeeName() == null ? "" : f.getFeeName()) + "|" +
                            (f.getAmount() == null ? "" : f.getAmount()) + "|" +
                            (f.getCurrency() == null ? "" : f.getCurrency()))
                    .collect(Collectors.joining(ITEM_SPLIT));
        } catch (Exception e) {
            return "";
        }
    }
    @Override
    public List<Fee> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return List.of();
        try {
            return Arrays.stream(dbData.split(ITEM_SPLIT))
                    .map(s -> {
                        String[] parts = s.split("\\|");
                        if (parts.length != 3) return null;
                        Fee fee = new Fee();
                        fee.setFeeName(parts[0]);
                        fee.setAmount(parts[1].isBlank() ? null : Double.parseDouble(parts[1]));
                        fee.setCurrency(parts[2]);
                        return fee;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
}

