package com.juan.transactionservice.controller;

import com.juan.transactionservice.dto.PageResponse;
import com.juan.transactionservice.dto.TransactionRequestDto;
import com.juan.transactionservice.dto.TransactionResponseDto;
import com.juan.transactionservice.model.Transaction;
import com.juan.transactionservice.service.ITransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/transactions", produces = "application/json")
@Validated
public class TransactionController {

    private final ITransactionService transactionService;


    @PostMapping(consumes = "application/json")
    public ResponseEntity<TransactionResponseDto> createTransaction(@Valid @RequestBody TransactionRequestDto transactionRequestDto) {
        Transaction transaction = transactionService.save(transactionRequestDto);
        var location = URI.create("/transactions/" + transaction.getId());
        return ResponseEntity.created(location).body(TransactionResponseDto.from(transaction));
    }

    @GetMapping("{id}")
    public ResponseEntity<TransactionResponseDto> getTransactionById(@PathVariable @Positive Long id) {
        Transaction transaction = transactionService.getById(id);
        return ResponseEntity.ok(TransactionResponseDto.from(transaction));
    }

    @GetMapping("/source/{id}")
    public ResponseEntity<PageResponse<TransactionResponseDto>> getTransactionsBySourceId(@PathVariable @Positive Long id,
                                                                      @PageableDefault(
                                                                         size = 20,
                                                                         sort = "createdAt",
                                                                         direction = Sort.Direction.DESC
                                                                 )Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(transactionService.getBySource(id, pageable).map(TransactionResponseDto::from)));
    }

    @GetMapping("/target/{id}")
    public ResponseEntity<PageResponse<TransactionResponseDto>> getTransactionsByTargetId(@PathVariable @Positive Long id,
                                                                              @PageableDefault(
                                                                                      size = 20,
                                                                                      sort = "createdAt",
                                                                                      direction = Sort.Direction.DESC
                                                                              ) Pageable pageable){
        return ResponseEntity.ok(PageResponse.from(transactionService.getByTarget(id, pageable).map(TransactionResponseDto::from)));
    }

    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponseDto>> getTransactionsBySourceIdAndTargetId(@RequestParam @Positive Long sourceId, @RequestParam @Positive Long targetId,
                                                                                         @PageableDefault(
                                                                                                 size = 20,
                                                                                                 sort = "createdAt",
                                                                                                 direction = Sort.Direction.DESC
                                                                                         )Pageable pageable) {

        return ResponseEntity.ok(PageResponse.from(transactionService.getBySourceAndTargetId(sourceId, targetId, pageable).map(TransactionResponseDto::from)));
    }

    @GetMapping("/dialog")
    public ResponseEntity<PageResponse<TransactionResponseDto>> getTransactionsByDialogId(@RequestParam @Positive Long a, @RequestParam @Positive Long b,
                                                                              @PageableDefault(
                                                                                      size = 20,
                                                                                      sort = "createdAt",
                                                                                      direction = Sort.Direction.DESC
                                                                              ) Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(transactionService.getDialog(a , b,pageable).map(TransactionResponseDto::from)));
    }





    @PatchMapping("/{id}/approve")
    public ResponseEntity<TransactionResponseDto> approveTransaction(@PathVariable @Positive Long id) {
        Transaction transaction = transactionService.approve(id);
        return ResponseEntity.ok(TransactionResponseDto.from(transaction));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<TransactionResponseDto> rejectTransaction(@PathVariable @Positive Long id) {
        Transaction transaction = transactionService.reject(id);
        return ResponseEntity.ok(TransactionResponseDto.from(transaction));
    }



}
