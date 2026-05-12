import { Marked } from 'marked';

const marked = new Marked({
  breaks: true,
  gfm: true
});

export function renderMarkdown(text) {
  if (!text || typeof text !== 'string') return '';
  try {
    return marked.parse(text);
  } catch (e) {
    console.error('[renderMarkdown] error:', e, 'text:', text.slice(0, 100));
    return text;
  }
}
