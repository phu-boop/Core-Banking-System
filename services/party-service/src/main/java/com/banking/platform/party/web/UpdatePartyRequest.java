package com.banking.platform.party.web;

import java.util.UUID;

public record UpdatePartyRequest(String displayName, UUID managingBranchId, long version) {}
