import type { LoginRequest, LoginResponse } from "../types";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1";

export async function login(request: LoginRequest): Promise<LoginResponse> {
  const response = await fetch(`${API_BASE_URL}/auth/password-login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-Request-Id": crypto.randomUUID(),
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorBody = await response.json().catch(() => null);
    throw new Error(errorBody?.message || `登录失败: HTTP ${response.status}`);
  }

  const payload = await response.json();
  if (payload.code !== "SUCCESS") {
    throw new Error(payload.message || "登录失败");
  }

  return payload.data;
}
