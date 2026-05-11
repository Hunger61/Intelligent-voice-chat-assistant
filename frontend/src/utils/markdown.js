import { marked } from 'marked';

marked.setOptions({
  breaks: true,
  gfm: true
});

export function renderMarkdown(text) {
  if (!text || typeof text !== 'string') return '';
  return marked(text);
}
