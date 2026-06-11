import { getExamPaperPage, createExamPaper, updateExamPaper, getSubjectList } from '../../api.js';
import { renderLayout } from '../layout.js';
import { toast, openModal, confirm, renderPagination, icons, spinner } from '../../components.js';

const STATUS = { 0: '비활성', 1: '활성' };
let page = 1;
let subjects = [];

export async function renderExamPapers() {
  renderLayout('admin/exam-papers',
    `<div class="card">
       <div class="table-toolbar">
         <span class="card-title">시험지 목록</span>
         <button class="btn btn-primary" id="add-btn">${icons.plus} 시험지 추가</button>
       </div>
       <div id="table-wrap">${spinner()}</div>
       <div id="pagination"></div>
     </div>`,
    '시험지 관리', '시험지를 등록하고 관리합니다');

  subjects = await getSubjectList().catch(() => []);
  document.getElementById('add-btn').addEventListener('click', () => openExamPaperForm());
  await load();
}

async function load() {
  const wrap = document.getElementById('table-wrap');
  wrap.innerHTML = spinner();
  try {
    const res = await getExamPaperPage({ pageIndex: page, pageSize: 10 });
    const list = res.list ?? res ?? [];
    const total = res.total ?? list.length;

    wrap.innerHTML = list.length === 0
      ? emptyState()
      : `<div class="table-wrap">
           <table>
             <thead><tr>
               <th>ID</th><th style="min-width:180px">시험지명</th><th>과목</th><th>제한시간(분)</th><th>문항수</th><th>총점</th><th>상태</th><th>작업</th>
             </tr></thead>
             <tbody>
               ${list.map(p => `
                 <tr>
                   <td>${p.id}</td>
                   <td><strong>${p.name}</strong></td>
                   <td>${p.subjectName ?? p.subjectId ?? '-'}</td>
                   <td>${p.limitedTime ?? '-'}</td>
                   <td>${p.questionCount ?? (p.questionIds?.length ?? '-')}</td>
                   <td>${p.totalScore ?? '-'}</td>
                   <td><span class="badge ${p.status === 1 ? 'badge-green' : 'badge-gray'}">${STATUS[p.status] ?? p.status}</span></td>
                   <td><div class="td-actions">
                     <button class="btn btn-sm btn-secondary edit-btn" data-id="${p.id}">${icons.edit} 수정</button>
                   </div></td>
                 </tr>`).join('')}
             </tbody>
           </table>
         </div>`;

    renderPagination(document.getElementById('pagination'), { page, total, size: 10, onChange: (p2) => { page = p2; load(); } });

    const pMap = Object.fromEntries(list.map(p => [String(p.id), p]));
    wrap.querySelectorAll('.edit-btn').forEach(btn => {
      btn.addEventListener('click', () => openExamPaperForm(pMap[btn.dataset.id]));
    });
  } catch (e) { wrap.innerHTML = `<p class="text-muted" style="padding:20px">${e.message}</p>`; }
}

function openExamPaperForm(data) {
  const isEdit = !!data?.id;
  const subjectOptions = subjects.map(s =>
    `<option value="${s.id}" ${data?.subjectId === s.id ? 'selected' : ''}>${s.name}</option>`
  ).join('');

  const modal = openModal({
    title: isEdit ? '시험지 수정' : '시험지 추가',
    size: 'large',
    body: `
      <div class="form-group">
        <label class="form-label">시험지명 <span>*</span></label>
        <input class="form-input" id="f-name" placeholder="시험지 이름을 입력하세요" value="${data?.name ?? ''}" />
      </div>
      <div class="form-row cols-2 mt-3">
        <div class="form-group">
          <label class="form-label">과목</label>
          <select class="form-input" id="f-subject"><option value="">선택 안함</option>${subjectOptions}</select>
        </div>
        <div class="form-group">
          <label class="form-label">제한시간 (분)</label>
          <input class="form-input" id="f-time" type="number" min="1" placeholder="60" value="${data?.limitedTime ?? ''}" />
        </div>
      </div>
      <div class="form-row cols-2 mt-3">
        <div class="form-group">
          <label class="form-label">총점</label>
          <input class="form-input" id="f-totalscore" type="number" min="0" placeholder="100" value="${data?.totalScore ?? ''}" />
        </div>
        <div class="form-group">
          <label class="form-label">상태</label>
          <select class="form-input" id="f-status">
            <option value="1" ${(data?.status ?? 1) === 1 ? 'selected' : ''}>활성</option>
            <option value="0" ${data?.status === 0 ? 'selected' : ''}>비활성</option>
          </select>
        </div>
      </div>
      <div class="form-group mt-3">
        <label class="form-label">설명</label>
        <textarea class="form-input" id="f-desc" rows="2" placeholder="시험 안내 (선택)">${data?.description ?? ''}</textarea>
      </div>`,
    footer: `<button class="btn btn-secondary" id="cancel-btn">취소</button>
             <button class="btn btn-primary" id="save-btn">저장</button>`,
  });

  modal.el.querySelector('#cancel-btn').addEventListener('click', modal.close);
  modal.el.querySelector('#save-btn').addEventListener('click', async () => {
    const name = document.getElementById('f-name').value.trim();
    if (!name) { toast.warning('시험지명을 입력하세요'); return; }
    const saveBtn = modal.el.querySelector('#save-btn');
    saveBtn.disabled = true;
    try {
      const payload = {
        name,
        subjectId: +document.getElementById('f-subject').value || undefined,
        limitedTime: +document.getElementById('f-time').value || undefined,
        totalScore: +document.getElementById('f-totalscore').value || undefined,
        status: +document.getElementById('f-status').value,
        description: document.getElementById('f-desc').value.trim() || undefined,
      };
      if (isEdit) await updateExamPaper(data.id, { ...payload, id: data.id });
      else await createExamPaper(payload);
      toast.success(isEdit ? '수정됐습니다' : '추가됐습니다');
      modal.close(); load();
    } catch (e) { toast.error(e.message); saveBtn.disabled = false; }
  });
}

function emptyState() {
  return `<div class="empty-state">
    <div class="empty-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg></div>
    <div class="empty-title">등록된 시험지가 없습니다</div>
    <div class="empty-desc">오른쪽 상단의 버튼으로 첫 시험지를 추가하세요.</div>
  </div>`;
}
