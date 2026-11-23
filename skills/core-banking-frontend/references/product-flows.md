# Core Banking product flows

These are UX patterns for future screens. They are not new API contracts or permission rules; wire each step only to behavior that its service exposes.

## 1. Customer transfer

1. Select the active source account and confirm its masked identifier/currency.
2. Select or enter the destination using supported beneficiary flows.
3. Enter amount and reference; validate locally for format only.
4. Request a server-side review/quote if the API provides one.
5. Present a final review of source, destination, amount, currency, and returned fees.
6. Submit once and prevent duplicate submission while awaiting response.
7. Display the authoritative result: accepted/processing is not the same as completed.
8. Show the service reference and receipt details only when returned by the service.

Timeouts or network loss must not be presented as failure or success unless the backend confirms the outcome. Provide a safe status refresh/reconciliation path only where the service supports it.

## 2. Corporate maker-checker

1. A maker prepares a transfer in the active organization context.
2. The system shows the policy/approval state returned by Corporate/Transfer services.
3. The maker submits for approval; show a pending status, not an executed transfer.
4. An eligible checker reviews the amount, beneficiary, initiator, and policy requirements.
5. Approve/reject is an explicit action with confirmation and a recorded reason where required.
6. Show immutable decision history and resulting transfer status from the API.

The backend owns separation-of-duties checks, eligibility, and execution gates.

## 3. Teller operation

1. Confirm staff identity and active branch context.
2. Find the customer/account using authorized search.
3. Confirm account identity, currency, and permitted operation.
4. Review the financial details and submit once.
5. Display the server result and reference; show processing/unknown states honestly.

Do not design unsupported controls for cash drawer, reversal, or limit override until those contracts exist.

## 4. Account and activity review

- Show only balances and account states provided by Account/Ledger.
- Distinguish current and available balance if the API defines both.
- Provide date, amount, type, reference, and status filters for transaction history.
- Explain empty, stale/unavailable, and unauthorized results without presenting stale data as current.

## 5. Audit and reports

- Make scope and date range explicit before queries or exports.
- Clearly mark export generation as pending until a completed response is received.
- Use least-privilege fields and masked identifiers.
- Never present a reporting projection as the financial system of record.

## Status vocabulary

Map service states to user-readable labels without changing their meaning: pending, processing/unresolved, awaiting approval, completed, rejected, failed, and cancelled only where supported. Do not invent progress percentages or retries.
