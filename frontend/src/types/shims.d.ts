declare module 'markdown-it' {
  interface MarkdownItOptions {
    html?: boolean;
    xhtmlOut?: boolean;
    breaks?: boolean;
    langPrefix?: string;
    linkify?: boolean;
    typographer?: boolean;
    quotes?: string | string[];
    highlight?: (str: string, lang: string, attrs: string) => string;
  }

  class MarkdownIt {
    constructor(options?: MarkdownItOptions);
    render(src: string, env?: any): string;
    renderInline(src: string, env?: any): string;
  }

  export = MarkdownIt;
}

declare module 'highlight.js' {
  export function highlight(code: string, options: { language: string }): { value: string };
  export function highlightAuto(code: string): { value: string };
  export function getLanguage(name: string): any;
}
