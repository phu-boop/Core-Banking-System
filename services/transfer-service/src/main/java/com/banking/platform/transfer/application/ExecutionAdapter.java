package com.banking.platform.transfer.application;

import com.banking.platform.transfer.domain.TransferRecord;
import java.util.UUID;

public interface ExecutionAdapter {
    void requestExecution(TransferRecord transfer);

    @org.springframework.stereotype.Component
    class DeferredExecutionAdapter implements ExecutionAdapter {
        @Override
        public void requestExecution(TransferRecord transfer) {
            throw new UnsupportedOperationException(
                    "No Ledger/Payment execution adapter is configured for transfer " + transfer.transferId());
        }
    }
}
