import { getQuestionPage, createQuestion, updateQuestion, getSubjectList } from '../../api.js';
import { renderLayout } from '../layout.js';
import { toast, openModal, confirm, renderPagination, icons, spinner } from '../../components.js';

const TYPE_LABELS = { 1: '단답형', 2: '다중선택', 3: 'O/X', 4: '빈칸채우기', 5: '서술형' };
const DIFFICULTY = { 1: '쉬움', 2: '보통', 3: '어려움' };

let page = 1;
let subjects = [];

export async function renderQuestions() {
  renderLayout('admin/questions',
    `<div class="card">
       <div class="table-toolbar">
         <span class="card-title">문항 목록</span>
         <button class="btn btn-primary" id="add-btn">${icons.plus} 문항 추가</button>
       </div>
       <div id="table-wrap">${spinner()}</div>
       <div id="pagination"></div>
     </div>`,
    '문항 관리', '시험 문항을 등록하고 관리합니다');

  subjects = await getSubjectList().catch(() => []);
  document.getElementById('add-btn').addEventListener('click', () => openQuestionForm());
  await load();
}

async function load() {
  const wrap = document.getElementById('table-wrap');
  wrap.innerHTML = spinner();
  try {
    const res = await getQuestionPage({ pageIndex: page, pageSize: 10 });
    const list = res.list ?? res ?? [];
    const total = res.total ?? list.length;

    wrap.innerHTML = list.length === 0
      ? emptyState()
      : `<div class="table-wrap">
           <table>
             <thead><tr>
               <th>ID</th><th style="min-width:200px">문항 내용</th><th>유형</th><th>과목</th><th>난이도</th><th>배점</th><th>작업</th>
             </tr></thead>
             <tbody>
               ${list.map(q => `
                 <tr>
                   <td>${q.id}</td>
                   <td class="truncate-cell">${q.title ?? q.content ?? '-'}</td>
                   <td><span class="badge badge-blue">${TYPE_LABELS[q.questionType] ?? q.questionType}</span></td>
                   <td>${q.subjectName ?? q.subjectId ?? '-'}</td>
                   <td><span class="badge ${diffBadge(q.difficult)}">${DIFFICULTY[q.difficult] ?? q.difficult ?? '-'}</span></td>
                   <td>${q.score ?? '-'}</td>
                   <td><div class="td-actions">
                     <button class="btn btn-sm btn-secondary edit-btn" data-id="${q.id}">${icons.edit} 수정</button>
                   </div></td>
                 </tr>`).join('')}
             </tbody>
           </table>
         </div>`;

    renderPagination(document.getElementById('pagination'), { page, total, size: 10, onChange: (p) => { page = p; load(); } });

    const qMap = Object.fromEntries(list.map(q => [String(q.id), q]));
    wrap.querySelectorAll('.edit-btn').forEach(btn => {
      btn.addEventListener('click', () => openQuestionForm(qMap[btn.dataset.id]));
    });
  } catch (e) { wrap.innerHTML = `<p class="text-muted" style="padding:20px">${e.message}</p>`; }
}

function diffBadge(d) {
  return d === 1 ? 'badge-green' : d === 2 ? 'badge-amber' : d === 3 ? 'badge-red' : 'badge-gray';
}

function buildOptionsHtml(options = []) {
  const items = options.length ? options : ['', '', '', ''];
  return items.map((val, i) => `
    <div class="option-row" style="display:flex;gap:6px;align-items:center;margin-bottom:6px">
      <span style="width:22px;text-align:center;color:#888">${i + 1}.</span>
      <input class="form-input opt-input" data-idx="${i}" placeholder="선택지 ${i + 1}" value="${val}" style="flex:1" />
      <button type="button" class="btn btn-sm btn-ghost opt-del" data-idx="${i}" title="삭제" style="padding:4px 6px">✕</button>
    </div>`).join('');
}

function wireOptions(container) {
  container.querySelectorAll('.opt-del').forEach(btn => {
    btn.addEventListener('click', () => {
      btn.closest('.option-row').remove();
    });
  });
}

function openQuestionForm(data) {
  const isEdit = !!data?.id;
  const subjectOptions = subjects.map(s =>
    `<option value="${s.id}" ${data?.subjectId === s.id ? 'selected' : ''}>${s.name}</option>`
  ).join('');

  const modal = openModal({
    title: isEdit ? '문항 수정' : '문항 추가',
    size: 'large',
    body: `
      <div class="form-row cols-2">
        <div class="form-group">
          <label class="form-label">유형 <span>*</span></label>
          <select class="form-input" id="f-type">
            ${Object.entries(TYPE_LABELS).map(([v, l]) =>
              `<option value="${v}" ${String(data?.questionType) === v ? 'selected' : ''}>${l}</option>`).join('')}
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">과목</label>
          <select class="form-input" id="f-subject"><option value="">선택 안함</option>${subjectOptions}</select>
        </div>
      </div>
      <div class="form-row cols-2 mt-3">
        <div class="form-group">
          <label class="form-label">난이도</label>
          <select class="form-input" id="f-diff">
            ${Object.entries(DIFFICULTY).map(([v, l]) =>
              `<option value="${v}" ${String(data?.difficult) === v ? 'selected' : ''}>${l}</option>`).join('')}
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">배점</label>
          <input class="form-input" id="f-score" type="number" min="0" placeholder="10" value="${data?.score ?? ''}" />
        </div>
      </div>
      <div class="form-group mt-3">
        <label class="form-label">문항 내용 <span>*</span></label>
        <textarea class="form-input" id="f-content" rows="3" placeholder="문항 내용을 입력하세요">${data?.title ?? data?.content ?? ''}</textarea>
      </div>
      <div class="form-group mt-3" id="options-section">
        <label class="form-label">선택지</label>
        <div id="options-list">${buildOptionsHtml(data?.itemList ?? [])}</div>
        <button type="button" class="btn btn-sm btn-secondary mt-2" id="add-option">${icons.plus} 선택지 추가</button>
      </div>
      <div class="form-group mt-3">
        <label class="form-label">정답</label>
        <input class="form-input" id="f-answer" placeholder="예: 1 또는 1,3 또는 true 또는 정답 텍스트" value="${data?.correct ?? data?.answer ?? ''}" />
        <p class="text-sm text-muted mt-1">단답형/다중: 번호(쉼표구분), O/X: true/false, 서술: 텍스트</p>
      </div>`,
    footer: `<button class="btn btn-secondary" id="cancel-btn">취소</button>
             <button class="btn btn-primary" id="save-btn">저장</button>`,
  });

  const optionsSec = modal.el.querySelector('#options-section');
  const optList = modal.el.querySelector('#options-list');
  wireOptions(optList);

  const typeEl = modal.el.querySelector('#f-type');
  function toggleOptions() {
    const t = +typeEl.value;
    optionsSec.style.display = (t === 1 || t === 2) ? '' : 'none';
  }
  typeEl.addEventListener('change', toggleOptions);
  toggleOptions();

  modal.el.querySelector('#add-option').addEventListener('click', () => {
    const idx = optList.querySelectorAll('.option-row').length;
    const row = document.createElement('div');
    row.className = 'option-row';
    row.style.cssText = 'display:flex;gap:6px;align-items:center;margin-bottom:6px';
    row.innerHTML = `<span style="width:22px;text-align:center;color:#888">${idx + 1}.</span>
      <input class="form-input opt-input" placeholder="선택지 ${idx + 1}" style="flex:1" />
      <button type="button" class="btn btn-sm btn-ghost opt-del" style="padding:4px 6px">✕</button>`;
    row.querySelector('.opt-del').addEventListener('click', () => row.remove());
    optList.appendChild(row);
  });

  modal.el.querySelector('#cancel-btn').addEventListener('click', modal.close);
  modal.el.querySelector('#save-btn').addEventListener('click', async () => {
    const content = document.getElementById('f-content').value.trim();
    if (!content) { toast.warning('문항 내용을 입력하세요'); return; }
    const saveBtn = modal.el.querySelector('#save-btn');
    saveBtn.disabled = true;
    try {
      const items = [...optList.querySelectorAll('.opt-input')].map(i => i.value.trim()).filter(Boolean);
      const payload = {
        questionType: +document.getElementById('f-type').value,
        subjectId: +document.getElementById('f-subject').value || undefined,
        difficult: +document.getElementById('f-diff').value,
        score: +document.getElementById('f-score').value || 10,
        title: content,
        itemList: items,
        correct: document.getElementById('f-answer').value.trim() || undefined,
      };
      if (isEdit) await updateQuestion(data.id, { ...payload, id: data.id });
      else await createQuestion(payload);
      toast.success(isEdit ? '수정됐습니다' : '추가됐습니다');
      modal.close(); load();
    } catch (e) { toast.error(e.message); saveBtn.disabled = false; }
  });
}

function emptyState() {
  return `<div class="empty-state">
    <div class="empty-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg></div>
    <div class="empty-title">등록된 문항이 없습니다</div>
    <div class="empty-desc">오른쪽 상단의 버튼으로 첫 문항을 추가하세요.</div>
  </div>`;
}
