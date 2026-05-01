// /apps routes are now defined in remaining.ts using AppLayout (no sidebar, public access).
// This file is intentionally empty — keeping it avoids import errors if referenced elsewhere.
export default {
  path: "/__app_placeholder__",
  meta: { title: "", showLink: false },
  children: []
} satisfies RouteConfigsTable;
