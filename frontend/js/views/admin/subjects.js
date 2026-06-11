import { getSubjectPage, createSubject, updateSubject } from '../../api.js';
import { renderLayout } from '../layout.js';
import { toast, openModal, confirm, renderPagination, icons, spinner } from '../../components.js';

let page = 1;

export async function renderSubjects() {
  renderLayout('admin/subjects',
    `<div class="card">
       <div class="table-toolbar">
         <span class="card-title">과목 목록</span>
         <button class="btn btn-primary" id="add-btn">${icons.plus} 과목 추가</button>
       </div>
       <div id="table-wrap">${spinner()}</div>
       <div id="pagination"></div>
     </div>`,
    '과목 관리', '시험 과목을 등록하고 관리합니다');

  document.getElementById('add-btn').addEventListener('click', () => openSubjectForm());
  await load();
}

async function load() {
  const wrap = document.getElementById('table-wrap');
  wrap.innerHTML = spinner();
  try {
    const res = await getSubjectPage({ pageIndex: page, pageSize: 10 });
    const list = res.list ?? res ?? [];
    const total = res.total ?? list.length;

    wrap.innerHTML = list.length === 0
      ? emptyState()
      : `<div class="table-wrap">
           <table>
             <thead><tr>
               <th>ID</th><th>과목명</th><th>학년</th><th>학년명</th><th>작업</th>
             </tr></thead>
             <tbody>
               ${list.map(s => `
                 <tr>
                   <td>${s.id}</td>
                   <td><strong>${s.name}</strong></td>
                   <td>${s.level ?? '-'}</td>
                   <td>${s.levelName ?? '-'}</td>
                   <td><div class="td-actions">
                     <button class="btn btn-sm btn-secondary edit-btn" data-id="${s.id}" data-name="${s.name}" data-level="${s.level||''}" data-levelname="${s.levelName||''}">${icons.edit} 수정</button>
                   </div></td>
                 </tr>`).join('')}
             </tbody>
           </table>
         </div>`;

    renderPagination(document.getElementById('pagination'), { page, total, size: 10, onChange: (p) => { page = p; load(); } });

    wrap.querySelectorAll('.edit-btn').forEach(btn => {
      const { id, name, level, levelname } = btn.dataset;
      btn.addEventListener('click', () => openSubjectForm({ id: +id, name, level: +level || undefined, levelName: levelname }));
    });
  } catch (e) { wrap.innerHTML = `<p class="text-muted" style="padding:20px">${e.message}</p>`; }
}

function openSubjectForm(data) {
  const isEdit = !!data?.id;
  const modal = openModal({
    title: isEdit ? '과목 수정' : '과목 추가',
    body: `
      <div class="form-group">
        <label class="form-label">과목명 <span>*</span></label>
        <input class="form-input" id="f-name" placeholder="예: 자바 프로그래밍" value="${data?.name||''}" />
      </div>
      <div class="form-row cols-2 mt-3">
        <div class="form-group">
          <label class="form-label">학년</label>
          <input class="form-input" id="f-level" type="number" placeholder="1" value="${data?.level||''}" />
        </div>
        <div class="form-group">
          <label class="form-label">학년명</label>
          <input class="form-input" id="f-levelname" placeholder="1학년" value="${data?.levelName||''}" />
        </div>
      </div>`,
    footer: `<button class="btn btn-secondary" id="cancel-btn">취소</button>
             <button class="btn btn-primary" id="save-btn">저장</button>`,
  });

  modal.el.querySelector('#cancel-btn').addEventListener('click', modal.close);
  modal.el.querySelector('#save-btn').addEventListener('click', async () => {
    const name = document.getElementById('f-name').value.trim();
    if (!name) { toast.warning('과목명을 입력하세요'); return; }
    const saveBtn = modal.el.querySelector('#save-btn');
    saveBtn.disabled = true;
    try {
      const payload = {
        name,
        level: +document.getElementById('f-level').value || undefined,
        levelName: document.getElementById('f-levelname').value.trim() || undefined,
      };
      if (isEdit) await updateSubject(data.id, { ...payload, id: data.id });
      else await createSubject(payload);
      toast.success(isEdit ? '수정됐습니다' : '추가됐습니다');
      modal.close(); load();
    } catch (e) { toast.error(e.message); saveBtn.disabled = false; }
  });
}

function emptyState() {
  return `<div class="empty-state">
    <div class="empty-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg></div>
    <div class="empty-title">등록된 과목이 없습니다</div>
    <div class="empty-desc">오른쪽 상단의 버튼으로 첫 과목을 추가하세요.</div>
  </div>`;
}
