import type { Metadata } from "next";
import { auth, signIn } from "@/auth";
import { redirect } from "next/navigation";
import { connection } from "next/server";

export const instant = false;
export const metadata: Metadata = {
  title: "Core Banking | Đăng nhập",
};

async function signInWithKeycloak() {
  "use server";
  await signIn("keycloak", { redirectTo: "/" });
}

export default async function LoginPage({
  searchParams,
}: {
  searchParams: Promise<{ error?: string }>;
}) {
  await connection();
  const session = await auth();
  if (session) {
    redirect("/");
  }

  const { error } = await searchParams;

  return (
    <main className="login-page">
      <section className="login-card" aria-labelledby="login-title">
        <div className="login-brand">
          <span className="brand-mark" aria-hidden="true">
            <span />
            <span />
            <span />
          </span>
          <span className="brand-copy">
            <strong>Core Banking</strong>
            <small>OPERATIONS WORKSPACE</small>
          </span>
        </div>
        <p className="eyebrow">TRUY CẬP AN TOÀN</p>
        <h1 id="login-title">Đăng nhập hệ thống</h1>
        <p className="login-description">
          Xác thực tập trung qua Keycloak. Mật khẩu của bạn không được gửi hoặc lưu trong ứng dụng.
        </p>
        {error ? (
          <p className="login-error" role="alert">
            Không thể hoàn tất đăng nhập. Vui lòng thử lại hoặc kiểm tra cấu hình Keycloak.
          </p>
        ) : null}
        <form action={signInWithKeycloak}>
          <button className="button button-primary login-button" type="submit">
            Tiếp tục với Keycloak
          </button>
        </form>
        <p className="login-footnote">
          Phiên đăng nhập được bảo vệ bằng cookie HTTP-only. Chỉ tài khoản đã được cấp hồ sơ Core
          Banking mới truy cập được dữ liệu nghiệp vụ.
        </p>
      </section>
    </main>
  );
}
