import { API_BASE } from './config.js';
import { auth } from './auth.js';

async function request(method, path, body) {
  const headers = { 'Content-Type': 'application/json' };
  const token = auth.token();
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}${path}`, {
    method,
    headers,
    body: body != null ? JSON.stringify(body) : undefined,
  });

  const data = await res.json().catch(() => null);

  if (!res.ok) {
    const msg = data?.message || data?.msg || `HTTP ${res.status}`;
    throw new Error(msg);
  }
  if (data?.code != null && data.code !== 200 && data.code !== 0) {
    throw new Error(data.message || data.msg || 'Request failed');
  }
  return data?.response ?? data;
}

const get  = (path)         => request('GET',    path);
const post = (path, body)   => request('POST',   path, body);
const put  = (path, body)   => request('PUT',    path, body);
const del  = (path)         => request('DELETE', path);

/* ── Auth ── */
export const login = (username, password) =>
  post('/auth/login', { username, password });

/* ── Subjects ── */
export const getSubjects    = ()         => get('/api/subjects');
export const getSubjectList = ()         => get('/api/subjects?pageIndex=1&pageSize=200').then(r => r.list ?? r ?? []);
export const createSubject  = (data)     => post('/api/subjects', data);
export const updateSubject  = (id, data) => put(`/api/subjects/${id}`, data);
export const getSubjectPage = (p) =>
  get(`/api/subjects?pageIndex=${p.pageIndex||1}&pageSize=${p.pageSize||10}`);

/* ── Questions ── */
export const getQuestionPage = (p)      => post('/api/questions/page', p);
export const getQuestion     = (id)     => get(`/api/questions/${id}`);
export const createQuestion  = (data)   => post('/api/questions', data);
export const updateQuestion  = (id, d)  => put(`/api/questions/${id}`, d);
export const getQuestionsByIds = (ids)  => get(`/api/questions/by-ids?ids=${Array.isArray(ids) ? ids.join(',') : ids}`);

/* ── Exam Papers ── */
export const getExamPaperPage        = (p)     => post('/api/exam-papers/page', p);
export const getStudentExamPaperPage = (p)     => post('/api/exam-papers/student-page', p);
export const getExamPaper            = (id)    => get(`/api/exam-papers/${id}`);
export const getExamPaperVm          = (id)    => get(`/api/exam-papers/${id}/vm`);
export const createExamPaper         = (data)  => post('/api/exam-papers', data);
export const updateExamPaper         = (id, d) => put(`/api/exam-papers/${id}`, d);

/* ── Exam Runtime ── */
export const submitExam      = (data)  => post('/api/exam-runtime/answer/submit', data);
export const getAnswerPage   = (p)     => post('/api/exam-runtime/answer/page', p);
export const getAnswerDetail = (id)    => get(`/api/exam-runtime/answer/${id}`);
export const getMyAnswers    = (p)     => post('/api/exam-runtime/answer/student-page', p);

/* ── Users ── */
export const getUserPage   = (p)     => get(`/api/users?pageIndex=${p.pageIndex||1}&pageSize=${p.pageSize||10}${p.keyword?'&userName='+encodeURIComponent(p.keyword):''}`);
export const createUser    = (data)  => post('/api/users', data);
export const updateUser    = (id, d) => put(`/api/users/${id}`, d);
export const deleteUsers   = (ids)   => del(`/api/users/${ids.join(',')}`);

/* ── Dashboard ── */
export const getAdminDashboard   = ()     => get('/api/admin/dashboard');
export const getStudentDashboard = ()     => get('/api/student/dashboard');
