const routes = new Map();
let _notFound = () => {};

export const router = {
  on(pattern, handler) { routes.set(pattern, handler); return this; },
  notFound(handler) { _notFound = handler; return this; },

  navigate(path) {
    window.location.hash = path;
  },

  _match(hash) {
    const path = hash.replace(/^#/, '') || '/';
    for (const [pattern, handler] of routes) {
      const keys = [];
      const regex = new RegExp(
        '^' + pattern.replace(/:([^/]+)/g, (_, k) => { keys.push(k); return '([^/]+)'; }) + '$'
      );
      const m = path.match(regex);
      if (m) {
        const params = Object.fromEntries(keys.map((k, i) => [k, m[i + 1]]));
        return { handler, params };
      }
    }
    return null;
  },

  start() {
    const dispatch = () => {
      const hash = window.location.hash || '#/';
      const match = this._match(hash);
      if (match) match.handler(match.params);
      else _notFound();
    };
    window.addEventListener('hashchange', dispatch);
    dispatch();
  },
};
