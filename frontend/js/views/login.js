import { login } from '../api.js';
import { auth } from '../auth.js';
import { toast } from '../components.js';
import { router } from '../router.js';

export function renderLogin() {
  const app = document.getElementById('app');
  app.innerHTML = `
    <div class="login-root">
      <div class="login-card">
        <div class="login-logo">
          <div class="login-logo-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/>
              <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>
            </svg>
          </div>
          <span class="login-logo-text">Exam Platform</span>
        </div>
        <h1 class="login-title">로그인</h1>
        <p class="login-sub">계정에 로그인하여 시작하세요.</p>
        <form class="login-form" id="login-form">
          <div class="form-group">
            <label class="form-label" for="username">아이디</label>
            <input class="form-input" id="username" type="text" placeholder="아이디를 입력하세요" autocomplete="username" required />
          </div>
          <div class="form-group">
            <label class="form-label" for="password">비밀번호</label>
            <input class="form-input" id="password" type="password" placeholder="비밀번호를 입력하세요" autocomplete="current-password" required />
          </div>
          <div class="login-actions">
            <button class="btn btn-primary btn-full btn-lg" type="submit" id="login-btn">
              로그인
            </button>
          </div>
        </form>
        <p class="text-sm text-muted mt-3" style="text-align:center">기본 계정: admin / admin, student1 / 1234</p>
      </div>
    </div>`;

  document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = document.getElementById('login-btn');
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    if (!username || !password) return;

    btn.disabled = true;
    btn.innerHTML = '<div class="spinner" style="width:16px;height:16px;border-width:2px;margin:0 auto"></div>';
    try {
      const res = await login(username, password);
      auth.save(res.token, { username: res.username, role: res.role, userId: res.userId });
      toast.success(`환영합니다, ${res.username}님!`);
      if (res.role === 'ROLE_ADMIN') router.navigate('/admin/dashboard');
      else router.navigate('/student/dashboard');
    } catch (err) {
      toast.error(err.message || '로그인에 실패했습니다');
      btn.disabled = false;
      btn.textContent = '로그인';
    }
  });
}
