/** 优选电商平台前端入口文件 */
import React from "react";
import ReactDOM from "react-dom/client";
import { App } from "./App";
import "./styles.css";

/** 应用启动入口：挂载根组件到 #root DOM 节点 */
ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
