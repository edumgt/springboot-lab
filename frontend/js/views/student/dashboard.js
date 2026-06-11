import { getStudentDashboard } from '../../api.js';
import { renderLayout } from '../layout.js';
import { toast, spinner } from '../../components.js';
import { router } from '../../router.js';

export async function renderStudentDashboard() {
  renderLayout('student/dashboard',
    `<div id="dash-content">${spinner('불러오는 중...')}</div>`,
    '내 대시보드', '학습 현황을 확인하세요');

  try {
    const d = await getStudentDashboard();
    document.getElementById('dash-content').innerHTML = `
      <div class="stats-grid stats-3">
        ${stat('총 응시 횟수', d.answerCount ?? 0, 'purple', checkIcon())}
        ${stat('완료된 시험', d.completeCount ?? 0, 'green', doneIcon())}
        ${stat('평균 점수', (d.averageScore ?? 0).toFixed(1), 'amber', starIcon())}
      </div>
      <div class="cards-row mt-4">
        <div class="card cta-card">
          <div class="cta-icon">${examIcon()}</div>
          <div class="cta-body">
            <div class="cta-title">시험 보기</div>
            <div class="cta-desc">개설된 시험 목록을 확인하고 응시하세요.</div>
          </div>
          <button class="btn btn-primary" id="go-exams">시험 목록 →</button>
        </div>
        <div class="card cta-card">
          <div class="cta-icon">${resultIcon()}</div>
          <div class="cta-body">
            <div class="cta-title">내 성적 확인</div>
            <div class="cta-desc">응시한 시험의 결과와 점수를 확인하세요.</div>
          </div>
          <button class="btn btn-secondary" id="go-results">성적 보기 →</button>
        </div>
      </div>`;

    document.getElementById('go-exams').addEventListener('click', () => router.navigate('/student/exams'));
    document.getElementById('go-results').addEventListener('click', () => router.navigate('/student/results'));
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
        <div class="stat-value">${typeof value === 'number' ? value.toLocaleString() : value}</div>
        <div class="stat-label">${label}</div>
      </div>
    </div>`;
}

const checkIcon  = () => `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 11 12 14 22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>`;
const doneIcon   = () => `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>`;
const starIcon   = () => `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>`;
const examIcon   = () => `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>`;
const resultIcon = () => `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>`;
