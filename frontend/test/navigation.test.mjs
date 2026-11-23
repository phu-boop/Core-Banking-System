import assert from "node:assert/strict";
import test from "node:test";
import { navigation } from "../src/lib/navigation.mjs";

test("only implemented dashboard navigation has a route", () => {
  const routableItems = navigation.filter((item) => item.href);

  assert.deepEqual(
    routableItems.map(({ label, href }) => ({ label, href })),
    [{ label: "Tổng quan", href: "/" }],
  );
});

test("future banking sections are explicitly unavailable", () => {
  const futureItems = navigation.filter((item) => !item.href);

  assert.equal(futureItems.length, 5);
  assert.ok(futureItems.every((item) => item.available === false));
});
