/* ── Toast ── */
export const toast = (() => {
  const icons = {
    success: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>`,
    error:   `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>`,
    warning: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>`,
    info:    `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>`,
  };
  const titles = { success: '성공', error: '오류', warning: '경고', info: '안내' };

  function show(type, msg, duration = 3500) {
    const el = document.createElement('div');
    el.className = `toast toast-${type}`;
    el.innerHTML = `
      <span class="toast-icon">${icons[type]}</span>
      <div class="toast-content">
        <div class="toast-title">${titles[type]}</div>
        <div class="toast-msg">${msg}</div>
      </div>`;
    document.getElementById('toast-container').appendChild(el);
    setTimeout(() => {
      el.classList.add('hiding');
      setTimeout(() => el.remove(), 200);
    }, duration);
  }
  return {
    success: (m) => show('success', m),
    error:   (m) => show('error',   m),
    warning: (m) => show('warning', m),
    info:    (m) => show('info',    m),
  };
})();

/* ── Modal ── */
export function openModal({ title, body, footer, size = '', onClose } = {}) {
  const root = document.getElementById('modal-root');
  const backdrop = document.createElement('div');
  backdrop.className = 'modal-backdrop';
  backdrop.innerHTML = `
    <div class="modal ${size ? 'modal-' + size : ''}">
      <div class="modal-header">
        <span class="modal-title">${title}</span>
        <button class="modal-close btn btn-icon" id="modal-close-btn">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </button>
      </div>
      <div class="modal-body" id="modal-body-content">${body || ''}</div>
      ${footer ? `<div class="modal-footer">${footer}</div>` : ''}
    </div>`;

  const close = () => { backdrop.remove(); onClose?.(); };
  backdrop.querySelector('#modal-close-btn').addEventListener('click', close);
  backdrop.addEventListener('click', (e) => { if (e.target === backdrop) close(); });
  root.appendChild(backdrop);

  return {
    el: backdrop,
    body: backdrop.querySelector('#modal-body-content'),
    close,
  };
}

/* ── Confirm dialog ── */
export function confirm({ title, message, confirmText = '확인', danger = true }) {
  return new Promise((resolve) => {
    const modal = openModal({
      title,
      body: `
        <div class="confirm-text">
          <div class="confirm-icon ${danger ? 'danger' : ''}">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              ${danger
                ? '<path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>'
                : '<circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/>'}
            </svg>
          </div>
          <div class="confirm-title">${title}</div>
          <div class="confirm-body">${message}</div>
        </div>`,
      footer: `
        <button class="btn btn-secondary" id="confirm-cancel">취소</button>
        <button class="btn ${danger ? 'btn-danger' : 'btn-primary'}" id="confirm-ok">${confirmText}</button>`,
      onClose: () => resolve(false),
    });
    modal.el.querySelector('#confirm-cancel').addEventListener('click', () => { modal.close(); resolve(false); });
    modal.el.querySelector('#confirm-ok').addEventListener('click', () => { modal.close(); resolve(true); });
  });
}

/* ── Loading spinner ── */
export const spinner = (msg = '불러오는 중...') =>
  `<div class="loading-spinner"><div class="spinner"></div>${msg}</div>`;

/* ── Pagination ── */
export function renderPagination(container, { page, total, size, onChange }) {
  const totalPages = Math.ceil(total / size) || 1;
  const from = (page - 1) * size + 1;
  const to   = Math.min(page * size, total);

  const pages = [];
  for (let i = Math.max(1, page - 2); i <= Math.min(totalPages, page + 2); i++) pages.push(i);

  container.innerHTML = `
    <div class="pagination">
      <span class="pagination-info">총 ${total}개 (${from}–${to})</span>
      <div class="pagination-controls">
        <button class="page-btn" ${page <= 1 ? 'disabled' : ''} data-p="${page - 1}">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <polyline points="15 18 9 12 15 6"/>
          </svg>
        </button>
        ${pages.map(p => `<button class="page-btn ${p === page ? 'active' : ''}" data-p="${p}">${p}</button>`).join('')}
        <button class="page-btn" ${page >= totalPages ? 'disabled' : ''} data-p="${page + 1}">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </button>
      </div>
    </div>`;

  container.querySelectorAll('[data-p]').forEach(btn => {
    btn.addEventListener('click', () => onChange(+btn.dataset.p));
  });
}

/* ── SVG icons shorthand ── */
export const icons = {
  plus:    `<svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>`,
  edit:    `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>`,
  trash:   `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>`,
  search:  `<svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>`,
  refresh: `<svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/></svg>`,
};
