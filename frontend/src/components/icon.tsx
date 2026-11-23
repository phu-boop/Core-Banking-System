import type { ReactNode } from "react";

export type IconName =
  | "overview"
  | "customers"
  | "accounts"
  | "transfers"
  | "approvals"
  | "reports"
  | "chevron"
  | "shield"
  | "info"
  | "activity";

const icons: Record<IconName, ReactNode> = {
  overview: (
    <>
      <rect x="3.5" y="3.5" width="7" height="7" rx="1.5" />
      <rect x="13.5" y="3.5" width="7" height="7" rx="1.5" />
      <rect x="3.5" y="13.5" width="7" height="7" rx="1.5" />
      <rect x="13.5" y="13.5" width="7" height="7" rx="1.5" />
    </>
  ),
  customers: (
    <>
      <circle cx="9" cy="8" r="3.25" />
      <path d="M3.5 20v-1.6A4.4 4.4 0 0 1 7.9 14h2.2a4.4 4.4 0 0 1 4.4 4.4V20" />
      <path d="M16 4.9a3.3 3.3 0 0 1 0 6.2M17 14.2a4.2 4.2 0 0 1 3.5 4.2V20" />
    </>
  ),
  accounts: (
    <>
      <rect x="3" y="5" width="18" height="14" rx="2.5" />
      <path d="M3 9h18M7 15h4" />
      <circle cx="17" cy="14.5" r="1" />
    </>
  ),
  transfers: (
    <>
      <path d="M7 7h12l-3-3M17 17H5l3 3" />
      <path d="M19 7v4M5 17v-4" />
    </>
  ),
  approvals: (
    <>
      <path d="m5 12 4.2 4.2L19.5 6" />
      <circle cx="12" cy="12" r="9" />
    </>
  ),
  reports: (
    <>
      <path d="M4 19.5h16M6.5 16V10m5 6V5m5 11v-4" />
      <path d="M5 4.5h4" />
    </>
  ),
  chevron: <path d="m9 18 6-6-6-6" />,
  shield: (
    <>
      <path d="M12 3 19 6v5.2c0 4.4-2.9 7.7-7 9.8-4.1-2.1-7-5.4-7-9.8V6l7-3Z" />
      <path d="m9 12 2 2 4-4" />
    </>
  ),
  info: (
    <>
      <circle cx="12" cy="12" r="9" />
      <path d="M12 11v5m0-8h.01" />
    </>
  ),
  activity: (
    <>
      <path d="M3 12h4l2.2-6 4.1 12 2.2-6H21" />
    </>
  ),
};

interface IconProps {
  name: IconName;
  size?: number;
}

export function Icon({ name, size = 18 }: IconProps) {
  return (
    <svg
      aria-hidden="true"
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.7"
      strokeLinecap="round"
      strokeLinejoin="round"
      focusable="false"
    >
      {icons[name]}
    </svg>
  );
}
