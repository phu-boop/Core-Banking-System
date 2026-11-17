# Banking Clone — Architecture Document

This root-level note is a pointer only. The canonical, detailed architecture is maintained in [docs/architecture/BANKING_ARCHITECTURE.md](docs/architecture/BANKING_ARCHITECTURE.md).

Do not use previous decision summaries in this file as current requirements. The accepted decisions and remaining open items are recorded in the canonical architecture document and its linked [ADRs](docs/adr/README.md) and [Open Decisions](docs/architecture/OPEN-DECISIONS.md).
Mình sẽ chỉ khảo sát repository và lập kế hoạch theo yêu cầu; chưa ghi hoặc sửa bất kỳ file nào. Sau khi trình bày phát hiện và danh sách tài liệu dự kiến, mình sẽ chờ bạn duyệt.

Find files matching `**/*.md`

Ran terminal command: git status --short; printf '\nTop-level entries:\n'; find . -maxdepth 2 -type f -not -path './.git/*' -print | sort | sed -n '1,160p'

Search for `ADR-|Microservices|Banking|schema-per-service|Transfer|Ledger`

Read 
Completed with input: {
  "path": "/home/phudev/Developer/projects/Core-Banking-System/docs/Phase.md"
}

Read 
Completed with input: {
  "path": "/home/phudev/Developer/projects/Core-Banking-System/BANKING_ARCHITECTURE.md"
}

Read 
Completed with input: {
  "path": "/home/phudev/Developer/projects/Core-Banking-System/.gitignore"
}

Ran terminal command: printf 'Tracked files:\n'; git ls-files; printf '\nDirectories (depth <= 3):\n'; find . -maxdepth 3 -type d -not -path './.git*' -print | sort; printf '\nMarkdown inventory:\n'; find . -type f -name '*.md' -not -path './.git/*' -print | sort

Ask User
Completed with input: {
  "message": "**Discovery hoàn tất — chưa tạo/sửa file nào.**\n\n**Hiện trạng repository:** `git ls-files` chỉ có `.gitignore`; không có README, source code, ADR hay tài liệu kiến trúc đã được lưu trong `docs/`. Các file hiện có là `docs/Phase.md`, `BANKING_ARCHITECTURE.md` ở repository root, cùng thư mục `skills/` không liên quan đến Banking Clone. Các tài liệu hiện có đều đang untracked.\n\n**Điểm cần lưu ý:** tài liệu root `BANKING_ARCHITECTURE.md` đã cũ so với yêu cầu mới: ADR-004 chưa ghi schema-per-service; VND, interbank, approval cấu hình được, loan ngoài MVP và multi-branch còn được liệt kê là câu hỏi mở. Nội dung `docs/Phase.md` là lộ trình công nghệ ở mức khái quát, không xác nhận các công nghệ đó đã được chọn hoặc triển khai. Không có source để xác minh implementation. Chưa thấy mâu thuẫn nội bộ trong các quyết định mới bạn vừa cung cấp.\n\n**Kế hoạch sau khi được duyệt — chỉ ghi Markdown trong `docs/`:**\n1. Tạo `docs/architecture/BANKING_ARCHITECTURE.md` và `docs/architecture/EVENT-CONTRACTS.md`.\n2. Tạo 9 ADR tại `docs/adr/ADR-001-...md` đến `ADR-009-...md`; trạng thái ACCEPTED theo quyết định bạn vừa xác nhận, không tự đặt ngày phê duyệt cũ.\n3. Tạo `docs/domain/BOUNDED_CONTEXTS.md`, `docs/domain/BUSINESS-RULES.md`.\n4. Tạo `docs/database/ERD.md`, `docs/api/API-CONTRACTS.md`, `docs/architecture/OPEN-DECISIONS.md`.\n5. Ghi rõ ranh giới ownership và nguyên tắc tài chính/bảo mật; đánh dấu các chi tiết kỹ thuật chưa được duyệt là PROPOSED hoặc OPEN; không tạo SQL hay triển khai.\n6. Sau đó kiểm tra Mermaid, liên kết nội bộ và tính nhất quán giữa service/schema/API/event/ADR; không chạy migration hay thay đổi source/configuration.\n\nMình đề xuất giữ nguyên tài liệu ở repository root vì phạm vi cho phép hiện tại chỉ bao gồm ghi tài liệu trong `docs/`, đồng thời ghi nhận trong báo cáo rằng tài liệu root đã cũ. Hãy chọn phê duyệt hoặc yêu cầu chỉnh kế hoạch; cũng cho biết cách xử lý tài liệu root.",
  "requestedSchema": {
    "properties": {
      "plan_approval": {
        "type": "string",
        "title": "Kế hoạch",
        "description": "Chỉ sau khi bạn duyệt mình mới bắt đầu tạo các tài liệu.",
        "oneOf": [
          {
            "const": "approve",
            "title": "Duyệt kế hoạch như đã nêu"
          },
          {
            "const": "revise",
            "title": "Yêu cầu chỉnh sửa kế hoạch trước"
          }
        ]
      },
      "root_document": {
        "type": "string",
        "title": "Tài liệu root cũ",
        "description": "Mặc định không cập nhật file ngoài thư mục docs/ theo giới hạn hiện tại.",
        "oneOf": [
          {
            "const": "leave_unchanged",
            "title": "Giữ nguyên và ghi nhận là tài liệu cũ"
          },
          {
            "const": "authorize_update",
            "title": "Cho phép ngoại lệ: cập nhật tài liệu root để tránh thông tin lỗi thời"
          },
          {
            "const": "other",
            "title": "Cách xử lý khác (ghi rõ)"
          }
        ]
      }
    }
  }
}

Kế hoạch đã được duyệt, bao gồm ngoại lệ cập nhật tài liệu cũ ở repository root. Mình bắt đầu tạo bộ tài liệu Markdown theo đúng ranh giới đã nêu; không đụng tới source, database hay cấu hình.

Edit , , 

Edit , 

Edit 

Edit 

Edit , , , , , , , , , 

Edit 

Edit 

Các tài liệu đã được tạo theo kế hoạch. Mình đang kiểm tra liên kết tương đối, cấu trúc Markdown/Mermaid và tính nhất quán các tên schema, service, trạng thái quyết định; nếu phát hiện điểm lệch mình sẽ chỉnh tài liệu trước khi báo cáo.

Ran terminal command: python - <<'PY'
from pathlib import Path
import re
root = Path('.')
files = sorted(p for p in root.rglob('*.md') if '.git' not in p.parts)
errors = []
mermaid_count = 0
for path in files:
    text = path.read_text(encoding='utf-8')
    if re.search(r'[^\S\n]+$', text, re.M):
        errors.append(f'{path}: trailing whitespace')
    fences = re.findall(r'^```([^\n]*)$', text, re.M)
    if len(fences) % 2:
        errors.append(f'{path}: unmatched code fence')
    mermaid_count += sum(label.strip() == 'mermaid' for label in fences)
    for target in re.findall(r'(?<!!)\[[^\]]*\]\(([^)]+)\)', text):
        if target.startswith(('http://', 'https://', 'mailto:', '#')):
            continue
        resolved = (path.parent / target.split('#', 1)[0]).resolve()
        if not resolved.exists():
            errors.append(f'{path}: broken link -> {target}')
print(f'Markdown files checked: {len(files)}')
print(f'Mermaid blocks found: {mermaid_count}')
print('Validation errors:')
print('\n'.join(errors) if errors else 'none')
PY
printf '\nMermaid CLI availability: '; command -v mmdc || true

Search for `schema-per-service|identity_schema|party_schema|corporate_schema|branch_schem...`

Ran terminal command: git status --short; printf '\nDocumentation files:\n'; find docs -type f -name '*.md' -print | sort; printf '\nSource/config file changes:\n'; git status --short -- '*.java' '*.kt' '*.sql' '*.yml' '*.yaml' '*.json' 'Dockerfile*'

Ran terminal command: python3 - <<'PY'
from pathlib import Path
import re
root = Path('.')
files = sorted(p for p in root.rglob('*.md') if '.git' not in p.parts)
errors = []
mermaid_count = 0
for path in files:
    text = path.read_text(encoding='utf-8')
    if re.search(r'[^\S\n]+$', text, re.M):
        errors.append(f'{path}: trailing whitespace')
    fences = re.findall(r'^```([^\n]*)$', text, re.M)
    if len(fences) % 2:
        errors.append(f'{path}: unmatched code fence')
    mermaid_count += sum(label.strip() == 'mermaid' for label in fences)
    for target in re.findall(r'(?<!!)\[[^\]]*\]\(([^)]+)\)', text):
        if target.startswith(('http://', 'https://', 'mailto:', '#')):
            continue
        resolved = (path.parent / target.split('#', 1)[0]).resolve()
        if not resolved.exists():
            errors.append(f'{path}: broken link -> {target}')
print(f'Markdown files checked: {len(files)}')
print(f'Mermaid blocks found: {mermaid_count}')
print('Validation errors:')
print('\n'.join(errors) if errors else 'none')
PY

Kiểm tra ban đầu cho thấy liên kết hỏng chỉ nằm trong `skills/` có sẵn, ngoài phạm vi Banking Clone; tài liệu mới chưa báo lỗi link nội bộ. Mình đang tinh chỉnh vài chỗ để không gợi ý rằng Account sở hữu số dư hoặc Identity phát event thuộc Audit.

"Apply Patch" failed
Completed with input: {"path":"/home/phudev/Developer/projects/Core-Banking-System/docs/architecture/EVENT-CONTRACTS.md"}
Tool execution failed: Error: Failed to find expected lines in /home/phudev/Developer/projects/Core-Banking-System/docs/domain/BOUNDED_CONTEXTS.md:
- **Consume:** `PartyCreated` nếu user-party linking được duyệt; branch/org/account events chỉ nếu policy cần local projection.

Search for `Publish:|Consume:|GET /v1/accounts|GET /internal/ledger|approval-decisions|Jo...`

Read , lines 75 to 125
Completed with input: {
  "path": "/home/phudev/Developer/projects/Core-Banking-System/docs/architecture/BANKING_ARCHITECTURE.md",
  "view_range": [
    75,
    125
  ]
}

Edit , 

Ran terminal command: python3 - <<'PY'
from pathlib import Path
import re
files = sorted(Path('docs').rglob('*.md')) + [Path('BANKING_ARCHITECTURE.md')]
errors = []
mermaid = []
for path in files:
    text = path.read_text(encoding='utf-8')
    if re.search(r'[^\S\n]+$', text, re.M):
        errors.append(f'{path}: trailing whitespace')
    fences = re.findall(r'^```([^\n]*)$', text, re.M)
    if len(fences) % 2:
        errors.append(f'{path}: unmatched code fence')
    for target in re.findall(r'(?<!!)\[[^\]]*\]\(([^)]+)\)', text):
        if target.startswith(('http://', 'https://', 'mailto:', '#')):
            continue
        resolved = (path.parent / target.split('#', 1)[0]).resolve()
        if not resolved.exists():
            errors.append(f'{path}: broken link -> {target}')
    blocks = re.findall(r'^```mermaid\s*\n(.*?)^```\s*$', text, re.M | re.S)
    for block in blocks:
        lines = [line.strip() for line in block.splitlines() if line.strip()]
        if not lines or not re.match(r'^(flowchart|graph|sequenceDiagram|classDiagram|stateDiagram|erDiagram)\b', lines[0]):
            errors.append(f'{path}: Mermaid block has invalid/missing diagram declaration')
        if lines and lines[0].startswith(('flowchart', 'graph')):
            depth = sum(line.startswith('subgraph ') for line in lines) - sum(line == 'end' for line in lines)
            if depth != 0:
                errors.append(f'{path}: Mermaid subgraph/end mismatch ({depth})')
        mermaid.append(path)
print(f'Files checked: {len(files)}')
print(f'Mermaid blocks checked structurally: {len(mermaid)}')
print('Errors:')
print('\n'.join(errors) if errors else 'none')
PY

Ran terminal command: python3 - <<'PY'
from pathlib import Path
required_schemas = {'identity_schema','party_schema','corporate_schema','branch_schema','account_schema','transaction_schema','transfer_schema','ledger_schema','payment_schema','notification_schema','audit_schema','reporting_schema'}
events = {'PartyCreated','AccountOpened','AccountBlocked','TransferRequested','TransferApproved','TransferCompleted','TransferFailed','JournalPosted','JournalReversed','PaymentSubmitted','PaymentSettled','NotificationRequested','AuditEventRecorded'}
all_text = '\n'.join(p.read_text(encoding='utf-8') for p in Path('docs').rglob('*.md'))
missing_schemas = sorted(x for x in required_schemas if x not in all_text)
event_doc = Path('docs/architecture/EVENT-CONTRACTS.md').read_text(encoding='utf-8')
missing_events = sorted(x for x in events if f'`{x}`' not in event_doc)
adr_files = sorted(Path('docs/adr').glob('ADR-*.md'))
missing_adrs = [f'ADR-{i:03d}' for i in range(1,10) if not any(f.name.startswith(f'ADR-{i:03d}-') for f in adr_files)]
print(f'ADR records: {len(adr_files)}')
print(f'Schema names: {len(required_schemas) - len(missing_schemas)}/{len(required_schemas)} present')
print(f'Event names: {len(events) - len(missing_events)}/{len(events)} in event contracts')
print(f'Missing ADRs: {missing_adrs or "none"}')
print(f'Missing schemas: {missing_schemas or "none"}')
print(f'Missing events: {missing_events or "none"}')
PY

Ran terminal command: git status --short; printf '\nNon-document tracked changes:\n'; git status --short -- '*.java' '*.kt' '*.sql' '*.yml' '*.yaml' '*.json' 'Dockerfile*'

Search for ````^```mermaid|^flowchart|^erDiagram|^```$|^  subgraph|^  end|^  [A-Z_]+ [|o].*--````

Read , lines 40 to 140
Completed with input: {
  "path": "/home/phudev/Developer/projects/Core-Banking-System/docs/architecture/BANKING_ARCHITECTURE.md",
  "view_range": [
    40,
    140
  ]
}

Read , lines 28 to 53
Completed with input: {
  "path": "/home/phudev/Developer/projects/Core-Banking-System/docs/database/ERD.md",
  "view_range": [
    28,
    53
  ]
}

## Hoàn tất tài liệu thiết kế

Đã tạo 9 ADR cùng các tài liệu kiến trúc, domain, database, API và event trong `docs/`. Các điểm chính:

- `Kiến trúc hệ thống`
- `Danh mục ADR-001–009`
- `Bounded Contexts` · `Business Rules`
- `Logical ERD` · `API Contracts`
- `Event Contracts` · `Open Decisions`

**Đã chốt:** Microservices; Retail + Corporate; Internet Banking, Admin, Teller; PostgreSQL dùng chung với schema-per-service; VND-only; internal + interbank transfer; configurable corporate approval; Loan ngoài MVP, để giai đoạn sau; multi-branch.

**Còn OPEN:** provider/protocol interbank; managing branch của customer; approval policy snapshot; API Gateway; Identity Provider; Kafka hay RabbitMQ; REST hay gRPC; Docker Compose hay Kubernetes; transaction limits/business-day; interbank settlement, timeout, reversal và reconciliation.

## Khảo sát và kiểm tra

Repository không có README, ADR hay source code để xác minh implementation. Tài liệu root cũ `BANKING_ARCHITECTURE.md` đã được thay bằng liên kết tới tài liệu chuẩn trong `docs/`; `Phase.md` không thay đổi.

Kiểm tra cho thấy liên kết nội bộ trong bộ tài liệu mới hợp lệ; đủ 9 ADR, 12 schema và 13 event. Cả 4 Mermaid diagram đã được kiểm tra cấu trúc và đọc soát; không có Mermaid CLI để chạy kiểm tra bằng renderer. Có một số liên kết hỏng trong tài liệu `skills/` có sẵn, ngoài phạm vi nên không sửa.

Chỉ thay đổi Markdown; **không sửa source code, database hay configuration**, không chạy migration.