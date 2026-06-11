import { getUserPage, createUser, updateUser } from '../../api.js';
import { renderLayout } from '../layout.js';
import { toast, openModal, confirm, renderPagination, icons, spinner } from '../../components.js';

let page = 1;
let keyword = '';

export async function renderUsers() {
  renderLayout('admin/users',
    `<div class="card">
       <div class="table-toolbar">
         <span class="card-title">사용자 목록</span>
         <div style="display:flex;gap:8px;align-items:center">
           <input class="form-input" id="search-input" placeholder="아이디/이름 검색" style="width:200px" value="${keyword}" />
           <button class="btn btn-secondary" id="search-btn">${icons.search}</button>
           <button class="btn btn-primary" id="add-btn">${icons.plus} 사용자 추가</button>
         </div>
       </div>
       <div id="table-wrap">${spinner()}</div>
       <div id="pagination"></div>
     </div>`,
    '사용자 관리', '사용자 계정을 등록하고 관리합니다');

  document.getElementById('add-btn').addEventListener('click', () => openUserForm());
  document.getElementById('search-btn').addEventListener('click', () => { page = 1; keyword = document.getElementById('search-input').value.trim(); load(); });
  document.getElementById('search-input').addEventListener('keydown', (e) => { if (e.key === 'Enter') document.getElementById('search-btn').click(); });
  await load();
}

async function load() {
  const wrap = document.getElementById('table-wrap');
  wrap.innerHTML = spinner();
  try {
    const res = await getUserPage({ pageIndex: page, pageSize: 10, keyword });
    const list = res.list ?? res ?? [];
    const total = res.total ?? list.length;

    wrap.innerHTML = list.length === 0
      ? emptyState()
      : `<div class="table-wrap">
           <table>
             <thead><tr>
               <th>ID</th><th>아이디</th><th>이름</th><th>이메일</th><th>역할</th><th>상태</th><th>작업</th>
             </tr></thead>
             <tbody>
               ${list.map(u => `
                 <tr>
                   <td>${u.id}</td>
                   <td><strong>${u.username}</strong></td>
                   <td>${u.realName ?? '-'}</td>
                   <td>${u.email ?? '-'}</td>
                   <td><span class="badge ${u.role === 'ROLE_ADMIN' ? 'badge-purple' : 'badge-blue'}">${u.role === 'ROLE_ADMIN' ? '관리자' : '학생'}</span></td>
                   <td><span class="badge ${u.status === 1 ? 'badge-green' : 'badge-gray'}">${u.status === 1 ? '활성' : '비활성'}</span></td>
                   <td><div class="td-actions">
                     <button class="btn btn-sm btn-secondary edit-btn" data-id="${u.id}">${icons.edit} 수정</button>
                   </div></td>
                 </tr>`).join('')}
             </tbody>
           </table>
         </div>`;

    renderPagination(document.getElementById('pagination'), { page, total, size: 10, onChange: (p) => { page = p; load(); } });

    const userMap = Object.fromEntries(list.map(u => [String(u.id), u]));
    wrap.querySelectorAll('.edit-btn').forEach(btn => {
      btn.addEventListener('click', () => openUserForm(userMap[btn.dataset.id]));
    });
  } catch (e) { wrap.innerHTML = `<p class="text-muted" style="padding:20px">${e.message}</p>`; }
}

function openUserForm(data) {
  const isEdit = !!data?.id;
  const modal = openModal({
    title: isEdit ? '사용자 수정' : '사용자 추가',
    body: `
      <div class="form-row cols-2">
        <div class="form-group">
          <label class="form-label">아이디 <span>*</span></label>
          <input class="form-input" id="f-username" placeholder="로그인 아이디" value="${data?.username||''}" ${isEdit ? 'disabled' : ''} />
        </div>
        <div class="form-group">
          <label class="form-label">비밀번호 ${isEdit ? '' : '<span>*</span>'}</label>
          <input class="form-input" id="f-password" type="password" placeholder="${isEdit ? '변경 시에만 입력' : '비밀번호'}" />
        </div>
      </div>
      <div class="form-row cols-2 mt-3">
        <div class="form-group">
          <label class="form-label">이름</label>
          <input class="form-input" id="f-realname" placeholder="홍길동" value="${data?.realName||''}" />
        </div>
        <div class="form-group">
          <label class="form-label">이메일</label>
          <input class="form-input" id="f-email" type="email" placeholder="user@example.com" value="${data?.email||''}" />
        </div>
      </div>
      <div class="form-row cols-2 mt-3">
        <div class="form-group">
          <label class="form-label">역할</label>
          <select class="form-input" id="f-role">
            <option value="ROLE_STUDENT" ${(data?.role||'ROLE_STUDENT') === 'ROLE_STUDENT' ? 'selected' : ''}>학생</option>
            <option value="ROLE_ADMIN" ${data?.role === 'ROLE_ADMIN' ? 'selected' : ''}>관리자</option>
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">상태</label>
          <select class="form-input" id="f-status">
            <option value="1" ${(data?.status ?? 1) === 1 ? 'selected' : ''}>활성</option>
            <option value="0" ${data?.status === 0 ? 'selected' : ''}>비활성</option>
          </select>
        </div>
      </div>`,
    footer: `<button class="btn btn-secondary" id="cancel-btn">취소</button>
             <button class="btn btn-primary" id="save-btn">저장</button>`,
  });

  modal.el.querySelector('#cancel-btn').addEventListener('click', modal.close);
  modal.el.querySelector('#save-btn').addEventListener('click', async () => {
    const username = document.getElementById('f-username').value.trim();
    const password = document.getElementById('f-password').value;
    if (!isEdit && !username) { toast.warning('아이디를 입력하세요'); return; }
    if (!isEdit && !password) { toast.warning('비밀번호를 입력하세요'); return; }
    const saveBtn = modal.el.querySelector('#save-btn');
    saveBtn.disabled = true;
    try {
      const payload = {
        username: isEdit ? data.username : username,
        realName: document.getElementById('f-realname').value.trim() || undefined,
        email: document.getElementById('f-email').value.trim() || undefined,
        role: document.getElementById('f-role').value,
        status: +document.getElementById('f-status').value,
      };
      if (password) payload.password = password;
      if (isEdit) await updateUser(data.id, { ...payload, id: data.id });
      else await createUser(payload);
      toast.success(isEdit ? '수정됐습니다' : '추가됐습니다');
      modal.close(); load();
    } catch (e) { toast.error(e.message); saveBtn.disabled = false; }
  });
}

function emptyState() {
  return `<div class="empty-state">
    <div class="empty-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg></div>
    <div class="empty-title">등록된 사용자가 없습니다</div>
    <div class="empty-desc">오른쪽 상단의 버튼으로 첫 사용자를 추가하세요.</div>
  </div>`;
}
