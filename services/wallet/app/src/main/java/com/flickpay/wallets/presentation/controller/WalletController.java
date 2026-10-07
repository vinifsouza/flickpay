package com.flickpay.wallets.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flickpay.shared.presentation.dto.ApiResponse;
import com.flickpay.wallets.application.dto.AddBalanceCommand;
import com.flickpay.wallets.application.dto.CreateWalletCommand;
import com.flickpay.wallets.application.dto.UpdateCurrencyCommand;
import com.flickpay.wallets.application.dto.UpdateWalletStatusCommand;
import com.flickpay.wallets.application.service.CreateWalletService;
import com.flickpay.wallets.application.service.GetWalletService;
import com.flickpay.wallets.application.service.UpdateWalletService;
import com.flickpay.wallets.domain.entity.Wallet;
import com.flickpay.wallets.domain.enums.WalletStatus;
import com.flickpay.wallets.presentation.dto.AddBalanceRequest;
import com.flickpay.wallets.presentation.dto.CreateWalletRequest;
import com.flickpay.wallets.presentation.dto.UpdateCurrencyRequest;
import com.flickpay.wallets.presentation.dto.UpdateWalletStatusRequest;
import com.flickpay.wallets.presentation.dto.WalletResponse;
import com.flickpay.wallets.presentation.mapper.WalletResponseMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final CreateWalletService createWalletService;
    private final GetWalletService getWalletService;
    private final UpdateWalletService updateWalletService;

    public WalletController(CreateWalletService createWalletService, GetWalletService getWalletService, UpdateWalletService updateWalletService) {
        this.createWalletService = createWalletService;
        this.getWalletService = getWalletService;
        this.updateWalletService = updateWalletService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WalletResponse>> createWallet(
            @Valid @RequestBody CreateWalletRequest request
    ) {
        var command = new CreateWalletCommand(
                request.userId(),
                request.currency()
        );

        Wallet wallet = createWalletService.create(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(WalletResponseMapper.toResponse(wallet)));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<ApiResponse<WalletResponse>> getById(
            @Valid @PathVariable UUID walletId
    ) {
        Wallet wallet = getWalletService.getById(walletId);

        return ResponseEntity
                .ok(new ApiResponse<>(WalletResponseMapper.toResponse(wallet)));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<WalletResponse>>> search(
            @Valid @RequestParam(required = false) UUID userId,
            @Valid @RequestParam(required = false) WalletStatus status
    ) {
        List<Wallet> wallets = getWalletService.findByUserId(userId);

        if (userId != null && status != null) {
            wallets = getWalletService.findAll();
        } else {
            if (userId != null) {
                wallets = getWalletService.findByUserId(userId);
            }

            if (status != null) {
                wallets = getWalletService.findByStatus(status);
            }
        }

        return ResponseEntity
                .ok(new ApiResponse<>(WalletResponseMapper.toResponseList(wallets)));
    }

    @PutMapping("/{walletId}/add-balance")
    public ResponseEntity<ApiResponse<WalletResponse>> addBalance(
            @Valid @PathVariable UUID walletId,
            @Valid @RequestBody AddBalanceRequest request
    ) {
        var command = new AddBalanceCommand(
                walletId,
                request.amount()
        );

        Wallet wallet = updateWalletService.addBalance(command);

        return ResponseEntity
                .ok(new ApiResponse<>(WalletResponseMapper.toResponse(wallet)));
    }

    @PutMapping("/{walletId}/currency")
    public ResponseEntity<ApiResponse<WalletResponse>> updateCurrency(
            @Valid @PathVariable UUID walletId,
            @Valid @RequestBody UpdateCurrencyRequest request
    ) {
        var command = new UpdateCurrencyCommand(
                walletId,
                request.currency()
        );

        Wallet wallet = updateWalletService.updateCurrency(command);

        return ResponseEntity
                .ok(new ApiResponse<>(WalletResponseMapper.toResponse(wallet)));
    }

    @PutMapping("/{walletID}/status")
    public ResponseEntity<ApiResponse<WalletResponse>> updateWalletStatus(
            @Valid @PathVariable UUID walletID,
            @Valid @RequestBody UpdateWalletStatusRequest request
    ) {
        var command = new UpdateWalletStatusCommand(walletID, request.status());

        Wallet wallet = updateWalletService.updateStatus(command);

        return ResponseEntity
                .ok(new ApiResponse<>(WalletResponseMapper.toResponse(wallet)));
    }

    @DeleteMapping("/{walletId}")
    public ResponseEntity<ApiResponse<Void>> deleteWallet(
            @Valid @PathVariable UUID walletId
    ) {
        updateWalletService.deleteWallet(walletId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
