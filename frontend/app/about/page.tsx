import Navbar from '../components/Navbar';
import rehypeHighlight from 'rehype-highlight'
import rehypeStringify from 'rehype-stringify'
import rehypeKatex from 'rehype-katex'
import rehypeSlug from 'rehype-slug'

import remarkToc from 'remark-toc'
import remarkMath from 'remark-math'
import remarkParse from 'remark-parse'
import remarkRehype from 'remark-rehype'

import { unified } from 'unified'

import java from 'highlight.js/lib/languages/java'
import hljs from 'highlight.js/lib/core';
import { HLJSApi, LanguageDetail } from 'highlight.js';

import fs from 'fs/promises';
import path from 'path';

import "../language-definition/markdown.css"

const inputFile = './about.md'

function ricelang(hljs: HLJSApi) {
  const base = Object(java(hljs) as LanguageDetail);

  const mergedKeywords = {
    ...base.keywords,
    keyword: base.keywords.keyword.concat("byebye")
  }

  return {
    ...base,
    name: 'ricelang',
    keywords: mergedKeywords,
  }
}

export default async function About() {
  const filePath = path.join(process.cwd(), 'app', 'about', inputFile);
  const fileContents = await fs.readFile(filePath, 'utf8');

  hljs.registerLanguage('ricelang', ricelang);

  const file = await unified()
    .use(remarkParse)
    .use(remarkMath)
    .use(remarkToc)
    .use(remarkRehype)
    .use(rehypeSlug)
    .use(rehypeKatex, { output: 'html', trust: true })
    .use(rehypeHighlight, { languages: { 'ricelang': ricelang } })
    .use(rehypeStringify)
    .process(fileContents);

  return (
    <div className="h-full w-full">
      <Navbar />
      <main
        id="definition"
        className='mx-auto max-w-[960px] my-8 p-4 backdrop-blur-xs border rounded-xl border-muted-foreground overflow-hidden'
        dangerouslySetInnerHTML={{ __html: String(file) }} />
      <p className='w-full text-center'>by Eric L May 2025</p>
    </div>
  );
}
