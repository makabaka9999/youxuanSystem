const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8081/api/v1";

export type BackendHealth = {
  connected: boolean;
  status?: string;
  service?: string;
  requestId?: string;
};

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
