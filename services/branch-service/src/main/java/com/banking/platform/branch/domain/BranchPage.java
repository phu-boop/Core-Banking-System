package com.banking.platform.branch.domain;

import java.util.List;

public record BranchPage(List<BranchResponse> items, int page, int size, long totalElements) {
    public BranchPage {
        items = List.copyOf(items);
    }

    public int totalPages() {
        return (int) Math.ceil((double) totalElements / size);
    }
}
