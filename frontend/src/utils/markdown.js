import { marked } from 'marked';

marked.setOptions({
  breaks: true,
  gfm: true
});

export function renderMarkdown(text) {
  if (!text || typeof text !== 'string') return '';
  try {
    const html = marked(text);
    // marked v12+ may return a Promise when async:true; force sync
    if (html instanceof Promise) {
      console.warn('[renderMarkdown] marked returned a Promise, using raw text');
      return text;
    }
    return html;
  } catch (e) {
    console.error('[renderMarkdown] error:', e, 'text:', text.slice(0, 100));
    return text; // fallback: 展示原始文本而非空白
  }
}
