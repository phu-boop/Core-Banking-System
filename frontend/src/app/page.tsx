import Link from "next/link";
import { auth, signIn, signOut } from "@/auth";
import { Icon } from "@/components/icon";
import { loadCurrentPrincipal, type IdentityLookup } from "@/lib/identity-api";
import { navigation } from "@/lib/navigation.mjs";
import { getToken } from "next-auth/jwt";
import { headers } from "next/headers";
import { redirect } from "next/navigation";
import { connection } from "next/server";

export const instant = false;

const summaries = [
  { label: "Tài khoản", icon: "accounts" as const },
  { label: "Giao dịch hôm nay", icon: "transfers" as const },
  { label: "Chờ phê duyệt", icon: "approvals" as const },
];

const services = [
  { key: "identity", label: "Identity", detail: "Đăng nhập và phân quyền" },
  { key: "account", label: "Account & Ledger", detail: "Tài khoản và sổ cái" },
  { key: "transfer", label: "Transfer & Payment", detail: "Chuyển tiền và thanh toán" },
  { key: "corporate", label: "Corporate", detail: "Phê duyệt doanh nghiệp" },
];

function getIdentityPresentation(identity: IdentityLookup) {
  switch (identity.status) {
    case "authenticated":
      return {
        title: "Danh tính đã được xác thực",
        message:
          "Identity API đã xác nhận hồ sơ của bạn. Số liệu tài khoản và giao dịch chưa được tích hợp.",
        badge: "ĐÃ XÁC THỰC",
        serviceLabel: "Đã xác thực",
        className: "status-success",
      };
    case "forbidden":
      return {
        title: "Chưa được cấp hồ sơ Core Banking",
        message:
          "Keycloak đã xác thực người dùng, nhưng Identity Service không tìm thấy ánh xạ đang hoạt động. Vui lòng liên hệ quản trị viên.",
        badge: "CẦN CẤP QUYỀN",
        serviceLabel: "Chưa cấp hồ sơ",
        className: "status-warning",
      };
    case "unauthorized":
      return {
        title: "Cần xác thực lại",
        message: "Access token không hợp lệ hoặc đã hết hạn. Đăng nhập lại để tiếp tục.",
        badge: "CẦN ĐĂNG NHẬP",
        serviceLabel: "Cần đăng nhập",
        className: "status-warning",
      };
    case "configuration-missing":
      return {
        title: "Thiếu cấu hình Identity API",
        message: "Chưa cấu hình IDENTITY_API_BASE_URL cho frontend.",
        badge: "THIẾU CẤU HÌNH",
        serviceLabel: "Thiếu cấu hình",
        className: "status-error",
      };
    case "invalid-response":
      return {
        title: "Identity API trả dữ liệu không hợp lệ",
        message: "Không thể xác nhận hồ sơ từ phản hồi của Identity Service.",
        badge: "LỖI PHẢN HỒI",
        serviceLabel: "Lỗi phản hồi",
        className: "status-error",
      };
    case "unavailable":
      return {
        title: "Identity API chưa khả dụng",
        message: "Không thể kết nối Identity Service. Vui lòng kiểm tra trạng thái dịch vụ.",
        badge: "API CHƯA SẴN SÀNG",
        serviceLabel: "Chưa kết nối",
        className: "status-error",
      };
    default: {
      const exhaustiveCheck: never = identity;
      throw new Error(`Unsupported Identity API state: ${exhaustiveCheck}`);
    }
  }
}

async function signOutAction() {
  "use server";
  await signOut({ redirectTo: "/login" });
}

async function signInAgainAction() {
  "use server";
  await signIn("keycloak", { redirectTo: "/" });
}

export default async function Home() {
  await connection();
  const session = await auth();
  if (!session) {
    redirect("/login");
  }

  const secret = process.env.AUTH_SECRET;
  if (!secret) {
    throw new Error("AUTH_SECRET must be set in the frontend environment.");
  }
  const token = await getToken({
    req: { headers: await headers() },
    secret,
  });
  const identity: IdentityLookup = token?.accessToken
    ? await loadCurrentPrincipal(token.accessToken)
    : { status: "unauthorized" };
  const identityPresentation = getIdentityPresentation(identity);
  const displayName = session.user?.name ?? session.user?.email ?? "Người dùng";

  return (
    <div className="app-shell">
      <aside className="sidebar" aria-label="Thanh điều hướng">
        <Link className="brand" href="/" aria-label="Core Banking, trang tổng quan">
          <span className="brand-mark" aria-hidden="true">
            <span />
            <span />
            <span />
          </span>
          <span className="brand-copy">
            <strong>Core Banking</strong>
            <small>OPERATIONS WORKSPACE</small>
          </span>
        </Link>

        <div className="sidebar-context">
          <span className="context-indicator" aria-hidden="true" />
          <div>
            <span className="context-label">Không gian làm việc</span>
            <strong>Vận hành ngân hàng</strong>
          </div>
        </div>

        <nav className="primary-navigation" aria-label="Điều hướng chính">
          <p className="nav-section-label">NGHIỆP VỤ</p>
          <ul className="nav-list">
            {navigation.map((item) => (
              <li key={item.label}>
                {item.href ? (
                  <Link className="nav-item nav-item-active" href={item.href} aria-current="page">
                    <Icon name={item.icon} />
                    <span>{item.label}</span>
                  </Link>
                ) : (
                  <span className="nav-item nav-item-disabled" aria-disabled="true">
                    <Icon name={item.icon} />
                    <span>{item.label}</span>
                    <span className="nav-coming-soon">Sắp có</span>
                  </span>
                )}
              </li>
            ))}
          </ul>
        </nav>

        <div className="sidebar-footer">
          <span className="sidebar-footer-icon">
            <Icon name="shield" size={17} />
          </span>
          <p>
            <strong>An toàn là ưu tiên</strong>
            <span>Giao diện chưa kết nối dữ liệu</span>
          </p>
        </div>
      </aside>

      <div className="main-region">
        <header className="topbar">
          <div className="breadcrumbs">
            <span>Core Banking</span>
            <Icon name="chevron" size={14} />
            <strong>Tổng quan</strong>
          </div>
          <div className="topbar-status">
            <span className="preview-dot" aria-hidden="true" />
            Đã đăng nhập
          </div>
          <div className="topbar-account">
            <span className="topbar-user" title={displayName}>
              {displayName}
            </span>
            <form action={signOutAction}>
              <button className="logout-button" type="submit">
                Đăng xuất
              </button>
            </form>
          </div>
        </header>

        <main className="dashboard">
          <section className="page-heading" aria-labelledby="page-title">
            <div>
              <p className="eyebrow">KHÔNG GIAN VẬN HÀNH</p>
              <h1 id="page-title">Tổng quan</h1>
              <p className="page-description">
                Theo dõi các hoạt động ngân hàng trong một không gian tập trung.
              </p>
            </div>
            <div className="secure-label">
              <Icon name="shield" size={16} />
              <span>Thiết kế bảo mật</span>
            </div>
          </section>

          <div className={`preview-notice ${identityPresentation.className}`} role="status">
            <span className="notice-icon">
              <Icon name="info" size={18} />
            </span>
            <p>
              <strong>{identityPresentation.title}</strong>
              <span>{identityPresentation.message}</span>
            </p>
            {identity.status === "unauthorized" ? (
              <form action={signInAgainAction}>
                <button className="notice-action" type="submit">
                  Đăng nhập lại
                </button>
              </form>
            ) : (
              <span className="notice-badge">{identityPresentation.badge}</span>
            )}
          </div>

          <section className="summary-grid" aria-label="Tóm tắt vận hành">
            {summaries.map((summary) => (
              <article className="summary-card" key={summary.label}>
                <div className="summary-card-top">
                  <span className="summary-icon">
                    <Icon name={summary.icon} size={19} />
                  </span>
                  <span className="summary-placeholder-label">CHƯA KẾT NỐI</span>
                </div>
                <h2>{summary.label}</h2>
                <p className="summary-value" aria-label="Chưa có dữ liệu">
                  —
                </p>
                <p className="summary-caption">Sẽ hiển thị sau khi tích hợp dịch vụ</p>
              </article>
            ))}
          </section>

          <div className="content-grid">
            <section className="panel activity-panel" aria-labelledby="activity-title">
              <div className="panel-heading">
                <div>
                  <p className="eyebrow">THEO DÕI</p>
                  <h2 id="activity-title">Hoạt động gần đây</h2>
                </div>
                <button className="button button-secondary" type="button" disabled>
                  Xem tất cả
                </button>
              </div>
              <div className="table-scroll">
                <table className="activity-table">
                  <thead>
                    <tr>
                      <th scope="col">THỜI GIAN</th>
                      <th scope="col">HOẠT ĐỘNG</th>
                      <th scope="col">THAM CHIẾU</th>
                      <th scope="col">TRẠNG THÁI</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td colSpan={4}>
                        <div className="empty-state">
                          <span className="empty-state-icon">
                            <Icon name="activity" size={22} />
                          </span>
                          <strong>Chưa có hoạt động để hiển thị</strong>
                          <span>
                            Danh sách sẽ được cập nhật khi các dịch vụ giao dịch được kết nối.
                          </span>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </section>

            <aside className="panel integration-panel" aria-labelledby="integration-title">
              <div className="panel-heading panel-heading-compact">
                <div>
                  <p className="eyebrow">TÍCH HỢP</p>
                  <h2 id="integration-title">Dịch vụ ngân hàng</h2>
                </div>
                <span className="integration-count">04</span>
              </div>
              <p className="integration-description">
                Các nguồn dữ liệu dự kiến cho màn hình vận hành.
              </p>
              <ul className="service-list">
                {services.map((service) => (
                  <li className="service-row" key={service.label}>
                    <span className="service-indicator" aria-hidden="true" />
                    <span className="service-copy">
                      <strong>{service.label}</strong>
                      <small>{service.detail}</small>
                    </span>
                    <span
                      className={`service-status ${service.key === "identity" ? identityPresentation.className : ""}`}
                    >
                      {service.key === "identity" ? identityPresentation.serviceLabel : "Chưa kết nối"}
                    </span>
                  </li>
                ))}
              </ul>
              <div className="integration-footnote">
                <Icon name="info" size={15} />
                <span>Trạng thái dịch vụ chưa được kiểm tra.</span>
              </div>
            </aside>
          </div>

          <section className="quick-actions" aria-labelledby="quick-actions-title">
            <div>
              <p className="eyebrow">BƯỚC TIẾP THEO</p>
              <h2 id="quick-actions-title">Thao tác nhanh</h2>
              <p>Các thao tác tài chính sẽ khả dụng sau khi tích hợp API và xác thực người dùng.</p>
            </div>
            <div className="quick-action-buttons">
              <button className="button button-primary" type="button" disabled>
                <Icon name="transfers" size={17} />
                Tạo giao dịch
              </button>
              <button className="button button-secondary" type="button" disabled>
                <Icon name="approvals" size={17} />
                Mở danh sách phê duyệt
              </button>
            </div>
          </section>

          <footer className="page-footer">
            <span>Core Banking · Giao diện nền tảng</span>
            <span>Không hiển thị hoặc xử lý dữ liệu khách hàng</span>
          </footer>
        </main>
      </div>
    </div>
  );
}
