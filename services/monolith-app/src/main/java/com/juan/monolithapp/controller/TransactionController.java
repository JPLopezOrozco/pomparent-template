package com.juan.monolithapp.controller;

import com.juan.monolithapp.dto.PageResponse;
import com.juan.monolithapp.dto.TransactionRequestDto;
import com.juan.monolithapp.dto.TransactionResponseDto;
import com.juan.monolithapp.model.Transaction;
import com.juan.monolithapp.service.ITransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping(value = "/transaction", produces = "application/json")
@RequiredArgsConstructor
public class TransactionController {

    private final ITransactionService transactionService;

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<TransactionResponseDto> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(TransactionResponseDto.from(transactionService.getById(id)));
    }

    @PostMapping(consumes="application/json", produces="application/json")
    public ResponseEntity<TransactionResponseDto> createTransaction(@RequestBody @Valid TransactionRequestDto transactionRequestDto) {
        Transaction newTransaction = transactionService.save(transactionRequestDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newTransaction.getId())
                .toUri();
        return ResponseEntity.created(location).body(TransactionResponseDto.from(newTransaction));
    }

    @GetMapping(value = "/account/{id}", produces = "application/json")
    public ResponseEntity<PageResponse<TransactionResponseDto>> getAllByAccount(@PathVariable("id") Long id,
                                                                                @ParameterObject
                                                                                @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                                                                                Pageable pageable) {
        var transactions = transactionService.getByAccountId(id, pageable)
                .map(TransactionResponseDto::from);
        return ResponseEntity.ok(PageResponse.from(transactions));
    }

}
