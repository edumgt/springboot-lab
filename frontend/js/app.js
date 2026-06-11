import { router } from './router.js';
import { auth } from './auth.js';

import { renderLogin } from './views/login.js';
import { renderAdminDashboard } from './views/admin/dashboard.js';
import { renderUsers } from './views/admin/users.js';
import { renderSubjects } from './views/admin/subjects.js';
import { renderQuestions } from './views/admin/questions.js';
import { renderExamPapers } from './views/admin/exam-papers.js';
import { renderStudentDashboard } from './views/student/dashboard.js';
import { renderExamList } from './views/student/exam-list.js';
import { renderTakeExam } from './views/student/take-exam.js';
import { renderResults } from './views/student/results.js';

function requireAuth(fn, adminOnly = false) {
  return (params) => {
    if (!auth.isLoggedIn()) { router.navigate('/login'); return; }
    if (adminOnly && !auth.isAdmin()) { router.navigate('/student/dashboard'); return; }
    fn(params);
  };
}

function requireStudent(fn) {
  return (params) => {
    if (!auth.isLoggedIn()) { router.navigate('/login'); return; }
    if (auth.isAdmin()) { router.navigate('/admin/dashboard'); return; }
    fn(params);
  };
}

router
  .on('/login',                   () => { if (auth.isLoggedIn()) redirectByRole(); else renderLogin(); })
  .on('/',                        () => { if (auth.isLoggedIn()) redirectByRole(); else renderLogin(); })
  .on('/admin/dashboard',         requireAuth(renderAdminDashboard, true))
  .on('/admin/users',             requireAuth(renderUsers, true))
  .on('/admin/subjects',          requireAuth(renderSubjects, true))
  .on('/admin/questions',         requireAuth(renderQuestions, true))
  .on('/admin/exam-papers',       requireAuth(renderExamPapers, true))
  .on('/student/dashboard',       requireStudent(renderStudentDashboard))
  .on('/student/exams',           requireStudent(renderExamList))
  .on('/student/take-exam/:id',   requireStudent(renderTakeExam))
  .on('/student/results',         requireStudent(renderResults))
  .notFound(() => { if (auth.isLoggedIn()) redirectByRole(); else renderLogin(); });

function redirectByRole() {
  router.navigate(auth.isAdmin() ? '/admin/dashboard' : '/student/dashboard');
}

router.start();
