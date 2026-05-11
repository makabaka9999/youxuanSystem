import { useCallback, useState } from "react";
import {
  Eye,
  EyeOff,
  Loader2,
  LogIn,
  ShieldCheck,
  Store,
  UserRound,
} from "lucide-react";
import { login as loginApi } from "../api/authApi";
import type { AuthState, LoginResponse } from "../types";
import { fetchPublicKey, rsaEncrypt } from "../utils/crypto";

type LoginPageProps = {
  onLoginSuccess: (authState: AuthState) => void;
};

type PortalType = "USER" | "MERCHANT_OWNER" | "PLATFORM_ADMIN";

const portalOptions: { type: PortalType; label: string; Icon: typeof UserRound }[] = [
  { type: "USER", label: "用户端", Icon: UserRound },
  { type: "MERCHANT_OWNER", label: "商家端", Icon: Store },
  { type: "PLATFORM_ADMIN", label: "平台后台", Icon: ShieldCheck },
];

type LoginStatus = "idle" | "loading" | "error" | "success";

export function LoginPage({ onLoginSuccess }: LoginPageProps) {
  const [portalType, setPortalType] = useState<PortalType>("USER");
  const [account, setAccount] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [status, setStatus] = useState<LoginStatus>("idle");
  const [errorMsg, setErrorMsg] = useState("");

  const handleLogin = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      if (!account.trim() || !password.trim()) {
        setStatus("error");
        setErrorMsg("请输入账号和密码");
        return;
      }

      setStatus("loading");
      setErrorMsg("");

      try {
        // Step 1: fetch RSA public key
        const publicKey = await fetchPublicKey();

        // Step 2: encrypt the password
        const encryptedPassword = await rsaEncrypt(password, publicKey.key);

        // Step 3: send login request with encrypted password
        const result: LoginResponse = await loginApi({
          account: account.trim(),
          password: encryptedPassword,
          principalType: portalType,
        });

        setStatus("success");

        const authState: AuthState = {
          authenticated: true,
          accessToken: result.accessToken,
          tokenType: result.tokenType,
          currentPrincipal: result.currentPrincipal,
        };

        // Store auth in localStorage (token persists across refreshes)
        localStorage.setItem("youxuan_auth", JSON.stringify(authState));

        setTimeout(() => onLoginSuccess(authState), 300);
      } catch (err) {
        setStatus("error");
        setErrorMsg(err instanceof Error ? err.message : "登录失败，请重试");
      }
    },
    [account, password, portalType, onLoginSuccess]
  );

  const isSubmitting = status === "loading";

  return (
    <div className="login-page">
      {/* Background decoration */}
      <div className="login-bg" aria-hidden="true">
        <div className="login-bg-circle login-bg-circle-1" />
        <div className="login-bg-circle login-bg-circle-2" />
        <div className="login-bg-circle login-bg-circle-3" />
      </div>

      <div className="login-container">
        {/* Brand */}
        <div className="login-brand">
          <div className="login-brand-mark">优</div>
          <div className="login-brand-text">
            <strong>优选</strong>
            <span>多商户综合电商平台</span>
          </div>
        </div>

        {/* Login Card */}
        <div className="login-card">
          <div className="login-card-header">
            <h1>登录</h1>
            <p>选择登录端并使用账号密码登录</p>
          </div>

          {/* Portal Type Selector */}
          <div className="login-portal-selector" role="radiogroup" aria-label="选择登录端">
            {portalOptions.map(({ type, label, Icon }) => (
              <button
                key={type}
                type="button"
                role="radio"
                aria-checked={portalType === type}
                className={`login-portal-option ${portalType === type ? "active" : ""}`}
                onClick={() => setPortalType(type)}
              >
                <Icon size={20} />
                <span>{label}</span>
              </button>
            ))}
          </div>

          {/* Login Form */}
          <form className="login-form" onSubmit={handleLogin} noValidate>
            <div className="login-field">
              <label htmlFor="account">账号</label>
              <input
                id="account"
                type="text"
                placeholder="请输入账号"
                value={account}
                onChange={(e) => {
                  setAccount(e.target.value);
                  if (status === "error") setStatus("idle");
                }}
                disabled={isSubmitting}
                autoComplete="username"
                autoFocus
              />
            </div>

            <div className="login-field">
              <label htmlFor="password">密码</label>
              <div className="password-input-wrap">
                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  placeholder="请输入密码"
                  value={password}
                  onChange={(e) => {
                    setPassword(e.target.value);
                    if (status === "error") setStatus("idle");
                  }}
                  disabled={isSubmitting}
                  autoComplete="current-password"
                />
                <button
                  type="button"
                  className="password-toggle"
                  aria-label={showPassword ? "隐藏密码" : "显示密码"}
                  onClick={() => setShowPassword(!showPassword)}
                  tabIndex={-1}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            {status === "error" && (
              <div className="login-error" role="alert">
                {errorMsg}
              </div>
            )}

            <button
              type="submit"
              className="login-submit"
              disabled={isSubmitting}
            >
              {isSubmitting ? (
                <>
                  <Loader2 size={18} className="spin" />
                  验证中...
                </>
              ) : (
                <>
                  <LogIn size={18} />
                  登录
                </>
              )}
            </button>
          </form>

          {/* Footer */}
          <div className="login-footer">
            <span className="login-footer-encryption">
              <ShieldCheck size={14} />
              RSA-2048 加密传输
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
