/**
 * 加密工具模块
 *
 * 提供 RSA-OAEP 加密能力，用于登录密码的前端加密传输。
 * 流程：从后端获取 RSA 公钥 -> 使用 Web Crypto API 加密密码 -> 发送密文到登录接口。
 */
import { Encrypt } from "../types";

// RSA 加密算法配置
const RSA_ALGORITHM = "RSA-OAEP";
const HASH_ALGORITHM = "SHA-256";

/**
 * 将 Base64 编码字符串转换为 ArrayBuffer
 * @param base64 - Base64 编码的公钥字符串
 */
function base64ToArrayBuffer(base64: string): ArrayBuffer {
  const binary = atob(base64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes.buffer;
}

/**
 * 导入 RSA 公钥为 Web Crypto API 可用的 CryptoKey 对象
 * @param base64Key - Base64 编码的 SPKI 格式公钥
 */
async function importPublicKey(base64Key: string): Promise<CryptoKey> {
  const keyBuffer = base64ToArrayBuffer(base64Key);
  return crypto.subtle.importKey(
    "spki",
    keyBuffer,
    { name: RSA_ALGORITHM, hash: HASH_ALGORITHM },
    false,
    ["encrypt"]
  );
}

/**
 * 使用 RSA-OAEP 算法对明文进行加密
 * @param plainText - 待加密的明文（用户密码）
 * @param publicKeyBase64 - Base64 编码的 RSA 公钥
 * @returns Base64 编码的密文字符串
 */
export async function rsaEncrypt(plainText: string, publicKeyBase64: string): Promise<string> {
  const publicKey = await importPublicKey(publicKeyBase64);
  const encoded = new TextEncoder().encode(plainText);
  const encrypted = await crypto.subtle.encrypt(
    { name: RSA_ALGORITHM },
    publicKey,
    encoded
  );
  return btoa(String.fromCharCode(...new Uint8Array(encrypted)));
}

/**
 * 从后端获取 RSA 公钥
 * @returns 包含公钥字符串和算法名称的对象
 */
export async function fetchPublicKey(): Promise<Encrypt.PublicKey> {
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8081/api/v1";
  const response = await fetch(`${API_BASE_URL}/auth/public-key`, {
    headers: { "X-Request-Id": crypto.randomUUID() },
  });
  if (!response.ok) {
    throw new Error(`Failed to fetch public key: HTTP ${response.status}`);
  }
  const payload = await response.json();
  if (payload.code !== "SUCCESS") {
    throw new Error(payload.message || "Failed to fetch public key");
  }
  return {
    key: payload.data.key,
    algorithm: payload.data.algorithm,
  };
}
