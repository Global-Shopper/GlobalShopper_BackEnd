package com.sep490.gshop.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        return list == null || list.isEmpty() ? "" : String.join(SPLIT_CHAR, list);
    }

    @Override
    public List<String> convertToEntityAttribute(String joined) {
        return (joined == null || joined.isBlank())
                ? List.of()
                : Arrays.stream(joined.split(SPLIT_CHAR)).collect(Collectors.toList());
    }
}
