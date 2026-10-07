package com.flickpay.wallets.domain.exception;

import com.flickpay.wallets.domain.enums.WalletStatus;

public class IllegalWalletStatusChangeException extends RuntimeException {
    public IllegalWalletStatusChangeException(WalletStatus newStatus) {
        super("Cannot change wallet status to " + newStatus + " while balance is not zero");
    }
}
