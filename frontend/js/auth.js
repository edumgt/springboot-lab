const KEY_TOKEN = 'exam_token';
const KEY_USER  = 'exam_user';

export const auth = {
  save(token, user) {
    localStorage.setItem(KEY_TOKEN, token);
    localStorage.setItem(KEY_USER, JSON.stringify(user));
  },
  token()      { return localStorage.getItem(KEY_TOKEN); },
  user()       { const u = localStorage.getItem(KEY_USER); return u ? JSON.parse(u) : null; },
  isLoggedIn() { return !!this.token(); },
  isAdmin()    { return this.user()?.role === 'ROLE_ADMIN'; },
  isStudent()  { return this.user()?.role === 'ROLE_STUDENT'; },
  clear()      { localStorage.removeItem(KEY_TOKEN); localStorage.removeItem(KEY_USER); },
};
