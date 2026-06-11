import { getAdminDashboard } from '../../api.js';
import { renderLayout } from '../layout.js';
import { toast } from '../../components.js';

export async function renderAdminDashboard() {
  renderLayout('admin/dashboard', `<div id="dash-content" class="loading-spinner"><div class="spinner"></div>불러오는 중...</div>`,
    '대시보드', '시스템 현황을 한눈에 확인하세요');

  try {
    const d = await getAdminDashboard();
    const months = ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'];
    const max = (arr) => Math.max(...arr, 1);

    document.getElementById('dash-content').innerHTML = `
      <div class="stats-grid">
        ${stat('총 사용자', d.userCount ?? 0, 'purple', userIcon())}
        ${stat('총 문항', d.questionCount ?? 0, 'blue', qIcon())}
        ${stat('총 시험지', d.examPaperCount ?? 0, 'green', paperIcon())}
        ${stat('총 응시', d.examAnswerCount ?? 0, 'amber', answerIcon())}
      </div>
      <div class="charts-grid">
        ${chart('월별 신규 문항', d.questionMonthly ?? [], months, 'purple')}
        ${chart('월별 신규 시험지', d.examPaperMonthly ?? [], months, 'blue')}
        ${chart('월별 응시 수', d.answerMonthly ?? [], months, 'green')}
      </div>`;
  } catch (e) {
    toast.error('대시보드 데이터를 불러오지 못했습니다: ' + e.message);
    document.getElementById('dash-content').innerHTML = `<p class="text-muted">데이터 로드 실패</p>`;
  }
}

function stat(label, value, color, icon) {
  return `
    <div class="stat-card">
      <div class="stat-icon ${color}">${icon}</div>
      <div class="stat-info">
        <div class="stat-value">${value.toLocaleString()}</div>
        <div class="stat-label">${label}</div>
      </div>
    </div>`;
}

function chart(title, arr, labels, _color) {
  const data = arr.length === 12 ? arr : Array(12).fill(0);
  const m = Math.max(...data, 1);
  const recent = data.slice(-6);
  const recentLabels = labels.slice(-6);
  return `
    <div class="chart-card">
      <div class="chart-title">${title}</div>
      <div class="bar-chart">
        ${recent.map((v, i) => `
          <div class="bar-item">
            <div class="bar-fill" style="height:${Math.round((v/m)*72)+4}px"></div>
            <span class="bar-label">${recentLabels[i]}</span>
          </div>`).join('')}
      </div>
    </div>`;
}

const userIcon  = () => `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>`;
const qIcon     = () => `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>`;
const paperIcon = () => `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>`;
const answerIcon= () => `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 11 12 14 22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>`;
