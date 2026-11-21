package com.banking.platform.account.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.platform.account.domain.AccountResponse;
import com.banking.platform.account.domain.AccountStatus;
import com.banking.platform.account.persistence.AccountRepository;
import com.banking.platform.account.web.CreateAccountRequest;
import com.banking.platform.account.web.StateChangeRequest;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTests {
    @Mock
    AccountRepository repository;

    @Test
    void rejectsInvalidAccountRequest() {
        AccountService service = new AccountService(repository);
        assertThatThrownBy(() -> service.create(new CreateAccountRequest(null, "bad"), "actor", "corr"))
                .isInstanceOf(AccountService.InvalidAccountException.class);
    }

    @Test
    void blocksAndRecordsStateTransition() {
        UUID accountId = UUID.randomUUID();
        UUID partyId = UUID.randomUUID();
        AccountResponse active = new AccountResponse(accountId, "123456", partyId,
                "PAYMENT", AccountStatus.ACTIVE, "VND", OffsetDateTime.now(), OffsetDateTime.now());
        AccountResponse blocked = new AccountResponse(accountId, "123456", partyId,
                "PAYMENT", AccountStatus.BLOCKED, "VND", OffsetDateTime.now(), OffsetDateTime.now());
        when(repository.findById(accountId)).thenReturn(Optional.of(active), Optional.of(blocked));
        when(repository.transition(any(), any(), any(), any(), any(), any())).thenReturn(true);

        AccountService service = new AccountService(repository);
        AccountResponse result = service.block(accountId,
                new StateChangeRequest("risk hold"), "actor", "corr");

        verify(repository).transition(accountId, AccountStatus.ACTIVE,
                AccountStatus.BLOCKED, "risk hold", "actor", "corr");
        org.assertj.core.api.Assertions.assertThat(result.status()).isEqualTo(AccountStatus.BLOCKED);
    }
}
