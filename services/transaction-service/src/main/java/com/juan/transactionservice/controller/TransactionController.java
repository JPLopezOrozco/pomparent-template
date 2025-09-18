package com.juan.transactionservice.controller;

import com.juan.transactionservice.dto.PageResponse;
import com.juan.transactionservice.dto.TransactionRequestDto;
import com.juan.transactionservice.dto.TransactionResponseDto;
import com.juan.transactionservice.model.Transaction;
import com.juan.transactionservice.service.ITransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/transactions", produces = "application/json")
public class TransactionController {

    private final ITransactionService transactionService;


    @PostMapping
    public ResponseEntity<TransactionResponseDto> create(@Valid @RequestBody TransactionRequestDto transactionRequestDto) {
        Transaction transaction = transactionService.save(transactionRequestDto);
        var location = URI.create("/transactions/" + transaction.getId());
        return ResponseEntity.created(location).body(TransactionResponseDto.from(transaction));
    }

    @GetMapping("{id}")
    public ResponseEntity<TransactionResponseDto> getById(@PathVariable Long id) {
        Transaction transaction = transactionService.getById(id);
        return ResponseEntity.ok(TransactionResponseDto.from(transaction));
    }

    @GetMapping("/source/{id}")
    public ResponseEntity<PageResponse<TransactionResponseDto>> getBySourceId(@PathVariable Long id,
                                                                      @PageableDefault(
                                                                         size = 20,
                                                                         sort = "createdAt",
                                                                         direction = Sort.Direction.DESC
                                                                 )Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(transactionService.getBySource(id, pageable).map(TransactionResponseDto::from)));
    }

    @GetMapping("/target/{id}")
    public ResponseEntity<PageResponse<TransactionResponseDto>> getByTargetId(@PathVariable Long id,
                                                                              @PageableDefault(
                                                                                      size = 20,
                                                                                      sort = "createdAt",
                                                                                      direction = Sort.Direction.DESC
                                                                              ) Pageable pageable){
        return ResponseEntity.ok(PageResponse.from(transactionService.getByTarget(id, pageable).map(TransactionResponseDto::from)));
    }

    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponseDto>> getBySourceIdAndTargetId(@RequestParam Long sourceId, @RequestParam Long targetId,
                                                                                         @PageableDefault(
                                                                                                 size = 20,
                                                                                                 sort = "createdAt",
                                                                                                 direction = Sort.Direction.DESC
                                                                                         )Pageable pageable) {

        return ResponseEntity.ok(PageResponse.from(transactionService.getBySourceAndTargetId(sourceId, targetId, pageable).map(TransactionResponseDto::from)));
    }

    @GetMapping("/dialog")
    public ResponseEntity<PageResponse<TransactionResponseDto>> getByDialogId(@RequestParam Long a, @RequestParam Long b,
                                                                              @PageableDefault(
                                                                                      size = 20,
                                                                                      sort = "createdAt",
                                                                                      direction = Sort.Direction.DESC
                                                                              ) Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(transactionService.getDialog(a , b,pageable).map(TransactionResponseDto::from)));
    }





    @PatchMapping("/{id}/approve")
    public ResponseEntity<TransactionResponseDto> approve(@PathVariable Long id) {
        Transaction transaction = transactionService.approve(id);
        return ResponseEntity.ok(TransactionResponseDto.from(transaction));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<TransactionResponseDto> reject(@PathVariable Long id) {
        Transaction transaction = transactionService.reject(id);
        return ResponseEntity.ok(TransactionResponseDto.from(transaction));
    }



}
