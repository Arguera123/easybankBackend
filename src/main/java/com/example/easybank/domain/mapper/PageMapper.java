package com.example.easybank.domain.mapper;

import com.example.easybank.domain.dto.response.PageResponse;
import org.springframework.data.domain.Page;

public class PageMapper {
    public static <T> PageResponse<T> map(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
}

