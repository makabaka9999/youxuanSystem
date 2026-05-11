import { Encrypt } from "../types";

const RSA_ALGORITHM = "RSA-OAEP";
const HASH_ALGORITHM = "SHA-256";

function base64ToArrayBuffer(base64: string): ArrayBuffer {
  const binary = atob(base64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes.buffer;
}

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
