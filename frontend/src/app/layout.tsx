import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Core Banking | Tổng quan",
  description:
    "Giao diện nền tảng Core Banking. Chưa kết nối dữ liệu khách hàng hoặc giao dịch.",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html lang="vi">
      <body>{children}</body>
    </html>
  );
}
