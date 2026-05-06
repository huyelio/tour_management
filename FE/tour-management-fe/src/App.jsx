import { BrowserRouter, Routes, Route, NavLink, useNavigate } from "react-router-dom";
import TourListPage from "./pages/TourListPage";
import TourDetailPage from "./pages/TourDetailPage";
import RevenueReportPage from "./pages/RevenueReportPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";

const navLinkStyle = ({ isActive }) => ({
  color: isActive ? "#fff" : "rgba(255,255,255,0.7)",
  textDecoration: "none",
  fontWeight: isActive ? 700 : 400,
  padding: "6px 14px",
  borderRadius: "6px",
  background: isActive ? "rgba(255,255,255,0.15)" : "transparent",
  fontSize: "14px",
  transition: "all 0.2s",
});

const HeaderNav = () => {
  const navigate = useNavigate();
  const currentUser = (() => {
    try { return JSON.parse(sessionStorage.getItem("currentUser")); } catch { return null; }
  })();

  const handleLogout = () => {
    sessionStorage.removeItem("currentUser");
    navigate("/login");
  };

  return (
    <header
      style={{
        background: "linear-gradient(135deg, #0f172a, #1e3a8a)",
        color: "#fff",
        padding: "0 24px",
        display: "flex",
        alignItems: "center",
        gap: "24px",
        height: "60px",
        boxShadow: "0 2px 8px rgba(0,0,0,0.2)",
        position: "sticky",
        top: 0,
        zIndex: 100,
      }}
    >
      <div
        style={{
          fontWeight: 800,
          fontSize: "18px",
          letterSpacing: "-0.5px",
          marginRight: "16px",
        }}
      >
        TourManager
      </div>

      <nav style={{ display: "flex", gap: "4px", flex: 1 }}>
        <NavLink to="/" end style={navLinkStyle}>
          Danh sách Tour
        </NavLink>
        <NavLink to="/reports/revenue" style={navLinkStyle}>
          Thống kê doanh thu
        </NavLink>
      </nav>

      <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
        {currentUser ? (
          <>
            <span style={{ fontSize: "13px", color: "rgba(255,255,255,0.8)" }}>
              Xin chào, <strong>{currentUser.username}</strong>
            </span>
            <button
              onClick={handleLogout}
              style={{
                padding: "5px 12px",
                background: "rgba(255,255,255,0.15)",
                color: "#fff",
                border: "1px solid rgba(255,255,255,0.3)",
                borderRadius: "6px",
                fontSize: "13px",
                cursor: "pointer",
              }}
            >
              Đăng xuất
            </button>
          </>
        ) : (
          <>
            <NavLink to="/login" style={navLinkStyle}>
              Đăng nhập
            </NavLink>
            <NavLink to="/register" style={navLinkStyle}>
              Đăng ký
            </NavLink>
          </>
        )}
      </div>
    </header>
  );
};

const App = () => (
  <BrowserRouter>
    <HeaderNav />

    {/* Main Content */}
    <main
      style={{
        minHeight: "calc(100vh - 60px)",
        background: "#f8fafc",
        padding: "28px 24px",
      }}
    >
      <div style={{ maxWidth: "1200px", margin: "0 auto" }}>
        <Routes>
          <Route path="/" element={<TourListPage />} />
          <Route path="/tours/:id" element={<TourDetailPage />} />
          <Route path="/reports/revenue" element={<RevenueReportPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Routes>
      </div>
    </main>
  </BrowserRouter>
);

export default App;
