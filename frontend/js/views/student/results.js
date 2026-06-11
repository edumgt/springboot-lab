import { getMyAnswers } from '../../api.js';
import { renderLayout } from '../layout.js';
import { toast, renderPagination, spinner } from '../../components.js';

const STATUS_LABEL = { 1: '채점 중', 2: '완료' };
let page = 1;

export async function renderResults() {
  renderLayout('student/results',
    `<div class="card">
       <div class="table-toolbar">
         <span class="card-title">내 응시 내역</span>
       </div>
       <div id="table-wrap">${spinner()}</div>
       <div id="pagination"></div>
     </div>`,
    '내 성적', '응시한 시험 결과를 확인합니다');

  await load();
}

async function load() {
  const wrap = document.getElementById('table-wrap');
  wrap.innerHTML = spinner();
  try {
    const res = await getMyAnswers({ pageIndex: page, pageSize: 10 });
    const list = res.list ?? res ?? [];
    const total = res.total ?? list.length;

    wrap.innerHTML = list.length === 0
      ? emptyState()
      : `<div class="table-wrap">
           <table>
             <thead><tr>
               <th>시험지명</th><th>과목</th><th>응시 시간</th><th>소요 시간</th><th>점수</th><th>상태</th>
             </tr></thead>
             <tbody>
               ${list.map(a => `
                 <tr>
                   <td><strong>${a.examPaperName ?? a.paperName ?? '-'}</strong></td>
                   <td>${a.subjectName ?? '-'}</td>
                   <td>${a.createTime ? formatDate(a.createTime) : '-'}</td>
                   <td>${a.doTime ? formatDuration(a.doTime) : '-'}</td>
                   <td>
                     ${a.status === 2
                       ? `<strong class="${scoreColor(a.userScore, a.systemScore)}">${a.userScore ?? 0} / ${a.systemScore ?? '-'}</strong>`
                       : '-'}
                   </td>
                   <td><span class="badge ${a.status === 2 ? 'badge-green' : 'badge-amber'}">${STATUS_LABEL[a.status] ?? a.status}</span></td>
                 </tr>`).join('')}
             </tbody>
           </table>
         </div>`;

    renderPagination(document.getElementById('pagination'), { page, total, size: 10, onChange: (p) => { page = p; load(); } });
  } catch (e) {
    wrap.innerHTML = `<p class="text-muted" style="padding:20px">${e.message}</p>`;
    toast.error('성적을 불러오지 못했습니다');
  }
}

function formatDate(ts) {
  const d = new Date(ts);
  return `${d.getFullYear()}.${String(d.getMonth()+1).padStart(2,'0')}.${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`;
}

function formatDuration(seconds) {
  const m = Math.floor(seconds / 60);
  const s = seconds % 60;
  return m > 0 ? `${m}분 ${s}초` : `${s}초`;
}

function scoreColor(userScore, systemScore) {
  if (systemScore == null || systemScore === 0) return '';
  const ratio = (userScore ?? 0) / systemScore;
  if (ratio >= 0.8) return 'text-green';
  if (ratio >= 0.5) return 'text-amber';
  return 'text-red';
}

function emptyState() {
  return `<div class="empty-state">
    <div class="empty-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg></div>
    <div class="empty-title">응시한 시험이 없습니다</div>
    <div class="empty-desc">시험 목록에서 시험을 응시하면 결과가 여기에 표시됩니다.</div>
  </div>`;
}
