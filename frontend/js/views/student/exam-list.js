import { getExamPaperPage, getSubjectList } from '../../api.js';
import { renderLayout } from '../layout.js';
import { toast, renderPagination, spinner } from '../../components.js';
import { router } from '../../router.js';

let page = 1;
let subjects = [];
let filterSubject = '';

export async function renderExamList() {
  renderLayout('student/exams',
    `<div class="toolbar-row">
       <div style="display:flex;gap:8px;align-items:center">
         <select class="form-input" id="subj-filter" style="width:160px">
           <option value="">전체 과목</option>
         </select>
       </div>
     </div>
     <div id="exam-grid">${spinner()}</div>
     <div id="pagination"></div>`,
    '시험 목록', '응시 가능한 시험 목록입니다');

  subjects = await getSubjectList().catch(() => []);
  const sel = document.getElementById('subj-filter');
  subjects.forEach(s => {
    const opt = document.createElement('option');
    opt.value = s.id; opt.textContent = s.name;
    sel.appendChild(opt);
  });
  sel.value = filterSubject;
  sel.addEventListener('change', () => { page = 1; filterSubject = sel.value; load(); });

  await load();
}

async function load() {
  const grid = document.getElementById('exam-grid');
  grid.innerHTML = spinner();
  try {
    const params = { pageIndex: page, pageSize: 12, status: 1 };
    if (filterSubject) params.subjectId = filterSubject;
    const res = await getExamPaperPage(params);
    const list = res.list ?? res ?? [];
    const total = res.total ?? list.length;

    if (list.length === 0) {
      grid.innerHTML = `<div class="empty-state">
        <div class="empty-icon">${paperIcon()}</div>
        <div class="empty-title">응시 가능한 시험이 없습니다</div>
        <div class="empty-desc">과목 필터를 변경하거나 나중에 다시 확인하세요.</div>
      </div>`;
      document.getElementById('pagination').innerHTML = '';
      return;
    }

    const subjMap = Object.fromEntries(subjects.map(s => [String(s.id), s.name]));
    grid.innerHTML = `<div class="exam-grid">
      ${list.map(p => `
        <div class="exam-card" data-id="${p.id}">
          <div class="exam-card-header">
            <span class="exam-badge">${subjMap[String(p.subjectId)] ?? '공통'}</span>
            <span class="badge ${p.status === 1 ? 'badge-green' : 'badge-gray'}">${p.status === 1 ? '응시 가능' : '마감'}</span>
          </div>
          <div class="exam-card-title">${p.name}</div>
          <div class="exam-card-meta">
            <span>${p.limitedTime ? `⏱ ${p.limitedTime}분` : ''}</span>
            <span>${p.questionCount ? `📝 ${p.questionCount}문항` : ''}</span>
            <span>${p.totalScore ? `💯 ${p.totalScore}점 만점` : ''}</span>
          </div>
          ${p.description ? `<div class="exam-card-desc">${p.description}</div>` : ''}
          <div class="exam-card-footer">
            <button class="btn btn-primary btn-sm take-btn" data-id="${p.id}" ${p.status !== 1 ? 'disabled' : ''}>시험 응시 →</button>
          </div>
        </div>`).join('')}
    </div>`;

    renderPagination(document.getElementById('pagination'), { page, total, size: 12, onChange: (p2) => { page = p2; load(); } });

    grid.querySelectorAll('.take-btn').forEach(btn => {
      btn.addEventListener('click', () => router.navigate(`/student/take-exam/${btn.dataset.id}`));
    });
  } catch (e) {
    grid.innerHTML = `<p class="text-muted" style="padding:20px">${e.message}</p>`;
    toast.error('시험 목록을 불러오지 못했습니다');
  }
}

const paperIcon = () => `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>`;
