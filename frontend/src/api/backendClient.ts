/**
 * 后端服务客户端
 *
 * 提供与后端网关/认证服务的直接通信能力，
 * 包含健康检查等基础接口的调用。
 */

// 后端 API 基础路径，优先使用环境变量配置
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8081/api/v1";

/** 后端健康检查响应类型 */
export type BackendHealth = {
  /** 后端是否已连接 */
  connected: boolean;
  /** 服务状态描述 */
  status?: string;
  /** 服务名称 */
  service?: string;
  /** 请求追踪 ID */
  requestId?: string;
};

/** 检测后端服务健康状态 */
export async function fetchBackendHealth(): Promise<BackendHealth> {
  try {
    const response = await fetch(`${API_BASE_URL}/health`, {
      headers: {
        "X-Request-Id": crypto.randomUUID()
      }
    });
    if (!response.ok) {
      return { connected: false, status: `HTTP_${response.status}` };
    }
    const payload = await response.json();
    return {
      connected: payload.code === "SUCCESS",
      status: payload.data?.status,
      service: payload.data?.serviceName || payload.data?.service,
      requestId: payload.requestId
    };
  } catch {
    return { connected: false, status: "OFFLINE" };
  }
}
