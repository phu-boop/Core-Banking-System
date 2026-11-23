import type { IconName } from "../components/icon";

interface NavigationItem {
  label: string;
  icon: IconName;
  href?: string;
  available?: boolean;
}

export const navigation: readonly NavigationItem[];
