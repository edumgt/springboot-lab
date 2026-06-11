import { auth } from '../auth.js';
import { router } from '../router.js';
import { confirm, toast } from '../components.js';

const ADMIN_NAV = [
  { id: 'admin/dashboard',  label: '대시보드',   icon: dashIcon() },
  { id: 'admin/users',      label: '사용자 관리', icon: usersIcon() },
  { id: 'admin/subjects',   label: '과목 관리',   icon: subjectIcon() },
  { id: 'admin/questions',  label: '문항 관리',   icon: questionIcon() },
  { id: 'admin/exam-papers',label: '시험지 관리', icon: paperIcon() },
];
const STUDENT_NAV = [
  { id: 'student/dashboard', label: '대시보드',  icon: dashIcon() },
  { id: 'student/exams',     label: '시험 목록', icon: paperIcon() },
  { id: 'student/results',   label: '내 성적',   icon: resultIcon() },
];

export function renderLayout(activeId, pageHtml, pageTitle = '', pageSubtitle = '') {
  const user = auth.user();
  const isAdmin = auth.isAdmin();
  const nav = isAdmin ? ADMIN_NAV : STUDENT_NAV;
  const initials = (user?.username || 'U').slice(0, 2).toUpperCase();
  const roleLabel = isAdmin ? '관리자' : '학생';

  document.getElementById('app').innerHTML = `
    <div class="app-shell">
      <aside class="sidebar">
        <div class="sidebar-logo">
          <div class="sidebar-logo-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/>
              <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>
            </svg>
          </div>
          <span class="sidebar-logo-name">Exam Platform</span>
          <span class="sidebar-logo-badge">${roleLabel}</span>
        </div>
        <nav class="sidebar-nav">
          ${nav.map(item => `
            <div class="sidebar-nav-item ${activeId === item.id ? 'active' : ''}"
                 data-route="${item.id}">
              ${item.icon}
              <span>${item.label}</span>
            </div>`).join('')}
        </nav>
        <div class="sidebar-footer">
          <div class="sidebar-user" id="logout-btn">
            <div class="sidebar-avatar">${initials}</div>
            <div class="sidebar-user-info">
              <div class="sidebar-user-name">${user?.username || '사용자'}</div>
              <div class="sidebar-user-role">${roleLabel}</div>
            </div>
            <div class="sidebar-logout-btn" title="로그아웃">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                <polyline points="16 17 21 12 16 7"/>
                <line x1="21" y1="12" x2="9" y2="12"/>
              </svg>
            </div>
          </div>
        </div>
      </aside>
      <div class="main-content">
        <div class="page-header">
          <div class="page-header-top">
            <div>
              <h1 class="page-title">${pageTitle}</h1>
              ${pageSubtitle ? `<p class="page-subtitle">${pageSubtitle}</p>` : ''}
            </div>
          </div>
        </div>
        <div class="page-body" id="page-content">
          ${pageHtml}
        </div>
      </div>
    </div>`;

  document.querySelectorAll('[data-route]').forEach(el => {
    el.addEventListener('click', () => router.navigate(`/${el.dataset.route}`));
  });

  document.getElementById('logout-btn').addEventListener('click', async () => {
    const ok = await confirm({ title: '로그아웃', message: '정말 로그아웃하시겠습니까?', danger: false, confirmText: '로그아웃' });
    if (ok) { auth.clear(); router.navigate('/login'); toast.info('로그아웃됐습니다.'); }
  });
}

/* ── Icons ── */
function dashIcon() {
  return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>`;
}
function usersIcon() {
  return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>`;
}
function subjectIcon() {
  return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>`;
}
function questionIcon() {
  return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>`;
}
function paperIcon() {
  return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>`;
}
function resultIcon() {
  return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>`;
}
