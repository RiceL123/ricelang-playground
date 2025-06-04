export const dynamic = 'force-static';

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

import './markdown.css';

import ScrollToTopButton from '../components/ScrollToTop';
import Toc from '../components/Toc';

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
    .use(remarkToc)
    .use(remarkRehype)
    .use(rehypeSlug)
    .use(rehypeKatex, { output: 'html', trust: true })
    .use(rehypeHighlight, { languages: { 'ricelang': ricelang } })
    .use(rehypeStringify)
    .process(fileContents);

  return (<>
    <link
      rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/katex@0.16.22/dist/katex.min.css"
      integrity="sha384-5TcZemv2l/9On385z///+d7MSYlvIEw9FuZTIdZ14vJLqWphw7e7ZPuOiCHJcFCP"
      crossOrigin="anonymous" />
    <div className="pb-6">
      <Navbar />
      <ScrollToTopButton />
      <Toc />
      <main
        id="definition"
        className='mx-auto max-w-[960px] my-16 p-4 backdrop-blur-xs border rounded-xl border-muted-foreground overflow-hidden'
        dangerouslySetInnerHTML={{ __html: String(file) }} />
      <p className='w-full text-center pb-4'>by Eric L May 2025</p>
    </div>
  </>);
}
