/**
 * 登录页面
 *
 * 支持三端登录（用户端 / 商家端 / 平台后台），
 * 采用 RSA-OAEP 加密传输密码至后端，
 * 登录成功后保存认证信息到 localStorage 并跳转至对应门户。
 * 也支持手机号注册用户。
 */
import { useCallback, useState } from "react";
import {
  Eye,
  EyeOff,
  Loader2,
  LogIn,
  ShieldCheck,
  Store,
  UserPlus,
  UserRound,
} from "lucide-react";
import { login as loginApi, register as registerApi } from "../api/authApi";
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

/** 页面模式 */
type PageMode = "login" | "register";

/** 登录页面组件 */
export function LoginPage({ onLoginSuccess }: LoginPageProps) {
  // ── 模式切换 ──
  const [mode, setMode] = useState<PageMode>("login");

  // ── 登录表单状态 ──
  const [portalType, setPortalType] = useState<PortalType>("USER");
  const [account, setAccount] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  // ── 注册表单状态 ──
  const [regMobile, setRegMobile] = useState("");
  const [regNickname, setRegNickname] = useState("");
  const [regPassword, setRegPassword] = useState("");
  const [regConfirmPassword, setRegConfirmPassword] = useState("");
  const [showRegPassword, setShowRegPassword] = useState(false);
  const [regSuccess, setRegSuccess] = useState(false);

  // ── 通用状态 ──
  const [submitting, setSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  /** 登录表单提交处理：RSA 加密密码后调用登录 API */
  const handleLogin = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      if (!account.trim() || !password.trim()) {
        setErrorMsg("请输入账号和密码");
        return;
      }

      setSubmitting(true);
      setErrorMsg("");

      try {
        const publicKey = await fetchPublicKey();
        const encryptedPassword = await rsaEncrypt(password, publicKey.key);

        const result: LoginResponse = await loginApi({
          account: account.trim(),
          password: encryptedPassword,
          principalType: portalType,
        });

        const authState: AuthState = {
          authenticated: true,
          accessToken: result.accessToken,
          tokenType: result.tokenType,
          currentPrincipal: result.currentPrincipal,
        };

        localStorage.setItem("youxuan_auth", JSON.stringify(authState));
        setTimeout(() => onLoginSuccess(authState), 300);
      } catch (err) {
        setErrorMsg(err instanceof Error ? err.message : "登录失败，请重试");
      } finally {
        setSubmitting(false);
      }
    },
    [account, password, portalType, onLoginSuccess]
  );

  /** 注册表单提交处理 */
  const handleRegister = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      if (!regMobile.trim() || !regPassword.trim()) {
        setErrorMsg("请输入手机号和密码");
        return;
      }
      if (regPassword !== regConfirmPassword) {
        setErrorMsg("两次密码输入不一致");
        return;
      }
      if (regPassword.length < 6) {
        setErrorMsg("密码长度至少 6 位");
        return;
      }

      setSubmitting(true);
      setErrorMsg("");

      try {
        const publicKey = await fetchPublicKey();
        const encryptedPassword = await rsaEncrypt(regPassword, publicKey.key);

        await registerApi({
          mobile: regMobile.trim(),
          password: encryptedPassword,
          nickname: regNickname.trim() || undefined,
        });

        setRegSuccess(true);
        // 注册成功后自动切换到登录模式并填充手机号
        setTimeout(() => {
          setMode("login");
          setAccount(regMobile.trim());
          setRegMobile("");
          setRegNickname("");
          setRegPassword("");
          setRegConfirmPassword("");
          setRegSuccess(false);
          setPortalType("USER");
        }, 1500);
      } catch (err) {
        setErrorMsg(err instanceof Error ? err.message : "注册失败，请重试");
      } finally {
        setSubmitting(false);
      }
    },
    [regMobile, regNickname, regPassword, regConfirmPassword]
  );

  /** 切换到注册模式 */
  const switchToRegister = useCallback(() => {
    setMode("register");
    setErrorMsg("");
    setRegSuccess(false);
  }, []);

  /** 切换到登录模式 */
  const switchToLogin = useCallback(() => {
    setMode("login");
    setErrorMsg("");
  }, []);

  const isSubmitting = submitting;

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

        {/* 登录/注册卡片 */}
        <div className="login-card">
          {mode === "login" ? (
            <>
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
                      if (errorMsg) setErrorMsg("");
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
                        if (errorMsg) setErrorMsg("");
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

                {/* 错误提示信息 */}
                {errorMsg && (
                  <div className="login-error" role="alert">
                    {errorMsg}
                  </div>
                )}

                {/* 登录提交按钮 */}
                <button type="submit" className="login-submit" disabled={isSubmitting}>
                  {isSubmitting ? (
                    <><Loader2 size={18} className="spin" />验证中...</>
                  ) : (
                    <><LogIn size={18} />登录</>
                  )}
                </button>
              </form>

              {/* 注册入口 */}
              <div className="login-footer">
                <button type="button" className="link-button" onClick={switchToRegister}>
                  <UserPlus size={14} />
                  没有账号？立即注册
                </button>
              </div>
            </>
          ) : (
            <>
              <div className="login-card-header">
                <h1>注册</h1>
                <p>使用手机号注册普通用户账号</p>
              </div>

              {/* 注册表单 */}
              <form className="login-form" onSubmit={handleRegister} noValidate>
                <div className="login-field">
                  <label htmlFor="reg-mobile">手机号</label>
                  <input
                    id="reg-mobile"
                    type="text"
                    placeholder="请输入手机号"
                    value={regMobile}
                    onChange={(e) => { setRegMobile(e.target.value); if (errorMsg) setErrorMsg(""); }}
                    disabled={isSubmitting || regSuccess}
                    autoComplete="tel"
                    autoFocus
                  />
                </div>

                <div className="login-field">
                  <label htmlFor="reg-nickname">昵称（选填）</label>
                  <input
                    id="reg-nickname"
                    type="text"
                    placeholder="给自己起个名字"
                    value={regNickname}
                    onChange={(e) => setRegNickname(e.target.value)}
                    disabled={isSubmitting || regSuccess}
                    autoComplete="name"
                  />
                </div>

                <div className="login-field">
                  <label htmlFor="reg-password">设置密码</label>
                  <div className="password-input-wrap">
                    <input
                      id="reg-password"
                      type={showRegPassword ? "text" : "password"}
                      placeholder="至少 6 位密码"
                      value={regPassword}
                      onChange={(e) => { setRegPassword(e.target.value); if (errorMsg) setErrorMsg(""); }}
                      disabled={isSubmitting || regSuccess}
                      autoComplete="new-password"
                    />
                    <button
                      type="button"
                      className="password-toggle"
                      aria-label={showRegPassword ? "隐藏密码" : "显示密码"}
                      onClick={() => setShowRegPassword(!showRegPassword)}
                      tabIndex={-1}
                    >
                      {showRegPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                    </button>
                  </div>
                </div>

                <div className="login-field">
                  <label htmlFor="reg-confirm-password">确认密码</label>
                  <input
                    id="reg-confirm-password"
                    type="password"
                    placeholder="再次输入密码"
                    value={regConfirmPassword}
                    onChange={(e) => { setRegConfirmPassword(e.target.value); if (errorMsg) setErrorMsg(""); }}
                    disabled={isSubmitting || regSuccess}
                    autoComplete="new-password"
                  />
                </div>

                {/* 注册成功提示 */}
                {regSuccess && (
                  <div className="login-success" role="alert">
                    <ShieldCheck size={16} />
                    注册成功！即将跳转登录...
                  </div>
                )}

                {/* 错误提示信息 */}
                {errorMsg && !regSuccess && (
                  <div className="login-error" role="alert">
                    {errorMsg}
                  </div>
                )}

                {/* 注册提交按钮 */}
                <button type="submit" className="login-submit" disabled={isSubmitting || regSuccess}>
                  {isSubmitting ? (
                    <><Loader2 size={18} className="spin" />注册中...</>
                  ) : (
                    <><UserPlus size={18} />注册</>
                  )}
                </button>
              </form>

              {/* 返回登录 */}
              <div className="login-footer">
                <button type="button" className="link-button" onClick={switchToLogin}>
                  <LogIn size={14} />
                  已有账号？立即登录
                </button>
              </div>
            </>
          )}

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
