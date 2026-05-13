/**
 * 认证相关 API
 *
 * 提供登录认证和注册的远程调用接口，
 * 支持密码登录、用户注册等功能。
 */
import type { LoginRequest, LoginResponse } from "../types";

// 后端 API 基础路径，优先使用环境变量配置
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api/v1";

/** 注册请求参数 */
export type RegisterRequest = {
  /** 手机号 */
  mobile: string;
  /** RSA 加密后的密码 */
  password: string;
  /** 昵称（可选） */
  nickname?: string;
};

/** 密码登录接口：提交账号（RSA 加密后的密码）和登录端类型，返回 JWT 令牌和主体信息 */
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

/** 用户注册接口：使用手机号注册新用户 */
export async function register(request: RegisterRequest): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/auth/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-Request-Id": crypto.randomUUID(),
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorBody = await response.json().catch(() => null);
    throw new Error(errorBody?.message || `注册失败: HTTP ${response.status}`);
  }

  const payload = await response.json();
  if (payload.code !== "SUCCESS") {
    throw new Error(payload.message || "注册失败");
  }
}
