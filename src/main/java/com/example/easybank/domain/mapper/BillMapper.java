package com.example.easybank.domain.mapper;

import com.example.easybank.domain.dto.request.BillRequestDTO;
import com.example.easybank.domain.dto.response.BillResponseDTO;
import com.example.easybank.domain.entity.Bill;

public class BillMapper {
    public static Bill toEntity(BillRequestDTO billRequestDTO) {
        return Bill.builder()
                .id(billRequestDTO.getId())
                .name(billRequestDTO.getExpenseName())
                .category(billRequestDTO.getCategory())
                .amount(billRequestDTO.getAmount())
                .date(billRequestDTO.getDate())
                .build();
    }

    public static BillResponseDTO toDTO(Bill bill) {
        return BillResponseDTO.builder()
                .id(bill.getId())
                .expenseName(bill.getName())
                .category(bill.getCategory())
                .amount(bill.getAmount())
                .date(bill.getDate())
                .state(bill.getState())
                .build();
    }
}
