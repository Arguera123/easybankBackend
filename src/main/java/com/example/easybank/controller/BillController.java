package com.example.easybank.controller;

import com.example.easybank.domain.dto.request.BillRequestDTO;
import com.example.easybank.domain.dto.response.BillResponseDTO;
import com.example.easybank.domain.dto.response.PageResponse;
import com.example.easybank.service.implementation.BillServiceImpl;
import com.example.easybank.util.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.easybank.util.Constant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + BILL)
public class BillController {
    private final BillServiceImpl billService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping(CREATE)
    public ResponseEntity<GenericResponse> createBill(@RequestBody @Valid BillRequestDTO bill) throws Exception {
        billService.save(bill);
        return GenericResponse
                .builder()
                .status(HttpStatus.CREATED)
                .message("Successfully created bill")
                .build()
                .buildResponse();
    }

    @GetMapping(FIND)
    public ResponseEntity<GenericResponse> findOwnBill(Pageable pageable) throws Exception {
        PageResponse<BillResponseDTO> response = billService.getMyBillsPaged(pageable);

        return GenericResponse
                .builder()
                .status(HttpStatus.OK)
                .message("Bills found")
                .data(response.getContent())
                .pageNumber(response.getPageNumber())
                .pageSize(response.getPageSize())
                .first(response.isFirst())
                .last(response.isLast())
                .totalElements(response.getTotalElements())
                .totalPages(response.getTotalPages())
                .build().buildResponse();
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping(PAY + "/{id}")
    public ResponseEntity<GenericResponse> payBill(@PathVariable("id") UUID id) throws Exception {
        billService.payBill(id);
        return GenericResponse.builder()
                .status(HttpStatus.OK)
                .message("Successfully payed bill")
                .build().buildResponse();
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping(EDIT)
    public ResponseEntity<GenericResponse> editBill(@RequestBody @Valid BillRequestDTO bill) throws Exception {
        billService.save(bill);
        return GenericResponse.builder()
                .status(HttpStatus.OK)
                .message("Successfully edited bill")
                .build().buildResponse();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping(DELETE + "/{id}")
    public ResponseEntity<GenericResponse> deleteBill(@PathVariable("id") UUID id) throws Exception {
        billService.delete(id);
        return GenericResponse.builder()
                .status(HttpStatus.ACCEPTED)
                .message("Successfully deleted bill")
                .build().buildResponse();
    }
}
