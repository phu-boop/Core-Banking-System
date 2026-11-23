package com.banking.platform.account.web;

import java.util.UUID;

public record CreateAccountRequest(UUID partyId, String accountNumber) {
}
