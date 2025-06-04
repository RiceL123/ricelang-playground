export const dynamic = 'force-static';

import Navbar from '../components/Navbar';
import MermaidHydrate from './MermaidHydrate';
import Toc from '../components/Toc';

import rehypeHighlight from 'rehype-highlight'
import rehypeStringify from 'rehype-stringify'
import rehypeSlug from 'rehype-slug'

import remarkToc from 'remark-toc'
import remarkMath from 'remark-math'
import remarkParse from 'remark-parse'
import remarkRehype from 'remark-rehype'

import { unified } from 'unified'
import { visit } from 'unist-util-visit';
import { Root, Element, Properties } from 'hast';

import java from 'highlight.js/lib/languages/java'
import x86asm from 'highlight.js/lib/languages/x86asm'
import javascript from 'highlight.js/lib/languages/javascript'
import bash from 'highlight.js/lib/languages/bash'

import hljs from 'highlight.js/lib/core';
import { HLJSApi, LanguageDetail } from 'highlight.js';

import fs from 'fs/promises';
import path from 'path';

import './markdown.css'
import ScrollToTopButton from '../components/ScrollToTop';

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

function addLanguageDataAttribute() {
  return (tree: Root) => {
    visit(tree, 'element', (node: Element) => {
      if (node.tagName === 'pre' && Array.isArray(node.children)) {
        const codeNode = node.children.find(
          (child): child is Element =>
            child.type === 'element' &&
            child.tagName === 'code' &&
            Array.isArray(child.properties?.className)
        );

        if (codeNode) {
          const classList = codeNode.properties.className as string[];
          const langClass = classList.find(cls =>
            cls.startsWith('language-')
          );

          if (langClass) {
            const lang = langClass.replace('language-', '');
            node.properties = node.properties || {} as Properties;
            node.properties['data-language'] = lang;
          }
        }
      }
    });
  };
}

function openExternalLinksInNewTabs() {
  return (tree: Root) => {
    visit(tree, 'element', (node: Element) => {
      if (node.tagName === 'a' && node.properties.href?.toString().startsWith("https://")) {
        node.properties['target'] = '_blank'
      }
    });
  };
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
    .use(rehypeHighlight, { languages: { ricelang, javascript, bash, 'jasmin': x86asm } })
    .use(addLanguageDataAttribute)
    .use(openExternalLinksInNewTabs)
    .use(rehypeStringify)
    .process(fileContents);

  return (
    <div className="h-full w-full">
      <Navbar />
      <ScrollToTopButton />
      <Toc />
      <main
        id="about"
        className='mx-auto max-w-[960px] my-16 p-4 backdrop-blur-xs border rounded-xl border-muted-foreground overflow-hidden'
        dangerouslySetInnerHTML={{ __html: String(file) }} />
      <MermaidHydrate />
      <p className='w-full text-center pb-4'>by Eric L May 2025</p>
    </div>
  );
}
