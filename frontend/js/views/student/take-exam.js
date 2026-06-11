import { getExamPaper, getQuestionsByIds, submitExam } from '../../api.js';
import { renderLayout } from '../layout.js';
import { toast, confirm, spinner } from '../../components.js';
import { router } from '../../router.js';

export async function renderTakeExam({ id: examPaperId }) {
  renderLayout('student/exams',
    `<div id="exam-root">${spinner('시험 준비 중...')}</div>`,
    '시험 응시', '');

  let paper, questions;
  try {
    paper = await getExamPaper(examPaperId);
    const qids = (paper.questionIds ?? paper.questions?.map(q => q.id) ?? []).join(',');
    questions = qids ? await getQuestionsByIds(qids) : paper.questions ?? [];
  } catch (e) {
    toast.error('시험 정보를 불러오지 못했습니다: ' + e.message);
    document.getElementById('exam-root').innerHTML = `<p class="text-muted">${e.message}</p>`;
    return;
  }

  if (!questions.length) {
    document.getElementById('exam-root').innerHTML = `<div class="empty-state"><div class="empty-title">문항이 없는 시험입니다</div></div>`;
    return;
  }

  const answers = {};
  const startTime = Date.now();
  const limitMs = (paper.limitedTime ?? 60) * 60 * 1000;
  let timerInterval;

  document.getElementById('exam-root').innerHTML = `
    <div class="exam-shell">
      <div class="exam-header">
        <div class="exam-header-info">
          <span class="exam-header-title">${paper.name}</span>
          <span class="exam-header-meta">${questions.length}문항 · ${paper.totalScore ?? '?'}점 만점</span>
        </div>
        <div class="exam-timer" id="exam-timer">--:--</div>
      </div>
      <div class="exam-body" id="exam-body">
        ${questions.map((q, i) => renderQuestion(q, i)).join('')}
      </div>
      <div class="exam-footer">
        <button class="btn btn-secondary" id="cancel-exam">취소</button>
        <button class="btn btn-primary btn-lg" id="submit-exam">제출하기</button>
      </div>
    </div>`;

  wireAnswers(questions, answers);
  startTimer(startTime, limitMs, () => submitAnswers(examPaperId, questions, answers, paper, startTime, true));

  document.getElementById('cancel-exam').addEventListener('click', async () => {
    const ok = await confirm({ title: '시험 취소', message: '시험을 취소하면 진행 상황이 저장되지 않습니다. 취소하시겠습니까?', danger: true });
    if (ok) { clearInterval(timerInterval); router.navigate('/student/exams'); }
  });

  document.getElementById('submit-exam').addEventListener('click', async () => {
    const unanswered = questions.filter(q => !hasAnswer(answers, q));
    if (unanswered.length > 0) {
      const ok = await confirm({ title: '제출 확인', message: `${unanswered.length}개 문항이 답변되지 않았습니다. 그대로 제출하시겠습니까?` });
      if (!ok) return;
    }
    clearInterval(timerInterval);
    await submitAnswers(examPaperId, questions, answers, paper, startTime, false);
  });

  function startTimer(start, limit, onExpire) {
    timerInterval = setInterval(() => {
      const elapsed = Date.now() - start;
      const remaining = Math.max(0, limit - elapsed);
      const m = Math.floor(remaining / 60000);
      const s = Math.floor((remaining % 60000) / 1000);
      const timerEl = document.getElementById('exam-timer');
      if (timerEl) timerEl.textContent = `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
      if (remaining <= 0) { clearInterval(timerInterval); onExpire(); }
      if (timerEl && remaining <= 60000) timerEl.classList.add('timer-danger');
    }, 1000);
  }
}

function renderQuestion(q, idx) {
  const t = q.questionType;
  const items = q.itemList ?? [];
  let inputHtml = '';

  if (t === 1) {
    inputHtml = items.map((opt, i) => `
      <label class="choice-label">
        <input type="radio" name="q-${q.id}" value="${i + 1}" /> ${opt}
      </label>`).join('');
  } else if (t === 2) {
    inputHtml = items.map((opt, i) => `
      <label class="choice-label">
        <input type="checkbox" data-qid="${q.id}" value="${i + 1}" /> ${opt}
      </label>`).join('');
  } else if (t === 3) {
    inputHtml = `
      <label class="choice-label"><input type="radio" name="q-${q.id}" value="true" /> O (맞음)</label>
      <label class="choice-label"><input type="radio" name="q-${q.id}" value="false" /> X (틀림)</label>`;
  } else if (t === 4) {
    inputHtml = `<input class="form-input gap-input" data-qid="${q.id}" placeholder="빈칸에 들어갈 단어" />`;
  } else {
    inputHtml = `<textarea class="form-input" data-qid="${q.id}" rows="3" placeholder="답변을 입력하세요"></textarea>`;
  }

  return `
    <div class="question-block" id="qblock-${q.id}">
      <div class="question-num">Q${idx + 1}
        <span class="question-score">${q.score ? `${q.score}점` : ''}</span>
      </div>
      <div class="question-content">${q.title ?? q.content}</div>
      <div class="question-inputs">${inputHtml}</div>
    </div>`;
}

function wireAnswers(questions, answers) {
  questions.forEach(q => {
    const t = q.questionType;
    if (t === 1 || t === 3) {
      document.querySelectorAll(`input[name="q-${q.id}"]`).forEach(el => {
        el.addEventListener('change', () => { answers[q.id] = el.value; });
      });
    } else if (t === 2) {
      document.querySelectorAll(`input[data-qid="${q.id}"]`).forEach(el => {
        el.addEventListener('change', () => {
          const checked = [...document.querySelectorAll(`input[data-qid="${q.id}"]:checked`)].map(c => c.value);
          answers[q.id] = checked;
        });
      });
    } else {
      const el = document.querySelector(`[data-qid="${q.id}"]`);
      if (el) el.addEventListener('input', () => { answers[q.id] = el.value; });
    }
  });
}

function hasAnswer(answers, q) {
  const a = answers[q.id];
  if (a === undefined || a === null || a === '') return false;
  if (Array.isArray(a)) return a.length > 0;
  return true;
}

async function submitAnswers(examPaperId, questions, answers, paper, startTime, timedOut) {
  const doTime = Math.floor((Date.now() - startTime) / 1000);
  const answerItems = questions.map(q => {
    const a = answers[q.id];
    const t = q.questionType;
    if (t === 2 && Array.isArray(a)) return { questionId: q.id, contentArray: a };
    return { questionId: q.id, content: a != null ? String(a) : '' };
  });

  const submitBtn = document.getElementById('submit-exam');
  if (submitBtn) { submitBtn.disabled = true; submitBtn.textContent = '제출 중...'; }

  if (timedOut) toast.warning('시간이 초과되어 자동 제출됩니다.');

  try {
    await submitExam({ examPaperId: +examPaperId, doTime, answerItems });
    toast.success('제출 완료! 채점 결과를 확인하세요.');
    router.navigate('/student/results');
  } catch (e) {
    toast.error('제출 실패: ' + e.message);
    if (submitBtn) { submitBtn.disabled = false; submitBtn.textContent = '제출하기'; }
  }
}
