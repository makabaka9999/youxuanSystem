/**
 * 登录页面
 *
 * 支持三端登录（用户端 / 商家端 / 平台后台），
 * 采用 RSA-OAEP 加密传输密码至后端，
 * 登录成功后保存认证信息到 localStorage 并跳转至对应门户。
 */
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

/** 登录页面组件属性 */
type LoginPageProps = {
  /** 登录成功回调：传递认证状态给父组件 */
  onLoginSuccess: (authState: AuthState) => void;
};

/** 可选登录门户类型 */
type PortalType = "USER" | "MERCHANT_STAFF" | "PLATFORM_ADMIN";

/** 门户选择器配置：类型、显示文字、对应图标 */
const portalOptions: { type: PortalType; label: string; Icon: typeof UserRound }[] = [
  { type: "USER", label: "用户端", Icon: UserRound },
  { type: "MERCHANT_STAFF", label: "商家端", Icon: Store },
  { type: "PLATFORM_ADMIN", label: "平台后台", Icon: ShieldCheck },
];

/** 登录流程状态：空闲 / 加载中 / 出错 / 成功 */
type LoginStatus = "idle" | "loading" | "error" | "success";

/** 登录页面组件 */
export function LoginPage({ onLoginSuccess }: LoginPageProps) {
  // 选中的登录门户类型
  const [portalType, setPortalType] = useState<PortalType>("USER");
  // 账号输入
  const [account, setAccount] = useState("");
  // 密码输入
  const [password, setPassword] = useState("");
  // 是否显示密码明文
  const [showPassword, setShowPassword] = useState(false);
  // 登录流程状态
  const [status, setStatus] = useState<LoginStatus>("idle");
  // 错误提示信息
  const [errorMsg, setErrorMsg] = useState("");

  /** 登录表单提交处理：RSA 加密密码后调用登录 API */
  const handleLogin = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      // 表单校验：账号和密码不能为空
      if (!account.trim() || !password.trim()) {
        setStatus("error");
        setErrorMsg("请输入账号和密码");
        return;
      }

      setStatus("loading");
      setErrorMsg("");

      try {
        // 第 1 步：从后端获取 RSA 公钥
        const publicKey = await fetchPublicKey();

        // 第 2 步：使用 RSA-OAEP 对明文密码加密
        const encryptedPassword = await rsaEncrypt(password, publicKey.key);

        // 第 3 步：发送加密后的密码到登录接口
        const result: LoginResponse = await loginApi({
          account: account.trim(),
          password: encryptedPassword,
          principalType: portalType,
        });

        setStatus("success");

        // 构建认证状态对象
        const authState: AuthState = {
          authenticated: true,
          accessToken: result.accessToken,
          tokenType: result.tokenType,
          currentPrincipal: result.currentPrincipal,
        };

        // 将认证信息持久化到 localStorage（刷新页面后保持登录态）
        localStorage.setItem("youxuan_auth", JSON.stringify(authState));

        // 延迟 300ms 跳转，让用户看到"登录成功"的反馈
        setTimeout(() => onLoginSuccess(authState), 300);
      } catch (err) {
        setStatus("error");
        setErrorMsg(err instanceof Error ? err.message : "登录失败，请重试");
      }
    },
    [account, password, portalType, onLoginSuccess]
  );

  // 是否正在提交中
  const isSubmitting = status === "loading";

  return (
    <div className="login-page">
      {/* 背景装饰圆 */}
      <div className="login-bg" aria-hidden="true">
        <div className="login-bg-circle login-bg-circle-1" />
        <div className="login-bg-circle login-bg-circle-2" />
        <div className="login-bg-circle login-bg-circle-3" />
      </div>

      <div className="login-container">
        {/* 品牌标识 */}
        <div className="login-brand">
          <div className="login-brand-mark">优</div>
          <div className="login-brand-text">
            <strong>优选</strong>
            <span>多商户综合电商平台</span>
          </div>
        </div>

        {/* 登录卡片 */}
        <div className="login-card">
          <div className="login-card-header">
            <h1>登录</h1>
            <p>选择登录端并使用账号密码登录</p>
          </div>

          {/* 门户类型选择器（用户端 / 商家端 / 平台后台） */}
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

          {/* 登录表单 */}
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
                  // 输错后重新输入时清除错误状态
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
                {/* 密码显示/隐藏切换按钮 */}
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

            {/* 错误提示信息 */}
            {status === "error" && (
              <div className="login-error" role="alert">
                {errorMsg}
              </div>
            )}

            {/* 登录提交按钮 */}
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

          {/* 底部：加密传输标识 */}
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
