import Navbar from '../components/Navbar';

import rehypeHighlight from 'rehype-highlight'
import rehypeKatex from 'rehype-katex'
import rehypeStringify from 'rehype-stringify'
import remarkMath from 'remark-math'
import remarkParse from 'remark-parse'
import remarkRehype from 'remark-rehype'
import { unified } from 'unified'

import java from 'highlight.js/lib/languages/java'
import hljs from 'highlight.js/lib/core';

import fs from 'fs/promises';
import path from 'path';
import { HLJSApi, LanguageDetail } from 'highlight.js';

import './codeHighlight.css';

const inputFile = './def.md'

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

export default async function LangDef() {
  const filePath = path.join(process.cwd(), 'app', 'language-definition', inputFile);
  const fileContents = await fs.readFile(filePath, 'utf8');

  hljs.registerLanguage('ricelang', ricelang);

  const file = await unified()
    .use(remarkParse)
    .use(remarkMath)
    .use(remarkRehype)
    .use(rehypeKatex, { output: 'html' })
    .use(rehypeHighlight, { languages: { 'ricelang': ricelang } })
    .use(rehypeStringify)
    .process(fileContents);

  return (
    <div className="h-full w-full">
      <Navbar />
      <main
        className='mx-auto my-6 max-w-[960px] backdrop-blur-xs border border-accent p-4'
        dangerouslySetInnerHTML={{ __html: String(file) }} />
      <link
        rel="stylesheet"
        href="https://cdn.jsdelivr.net/npm/katex@0.16.22/dist/katex.min.css"
        integrity="sha384-5TcZemv2l/9On385z///+d7MSYlvIEw9FuZTIdZ14vJLqWphw7e7ZPuOiCHJcFCP"
        crossOrigin="anonymous" />
    </div>
  );
}