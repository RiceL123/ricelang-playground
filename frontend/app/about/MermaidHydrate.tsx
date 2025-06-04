'use client';

import { useEffect } from 'react';
import mermaid from 'mermaid';

export default function MermaidHydrate() {
  useEffect(() => {
    const renderMermaid = async () => {
      const targets = document.querySelectorAll('pre[data-language="mermaid"]');

      targets.forEach((pre, i) => {
        const code = pre.textContent?.trim();
        if (!code) return;

        const wrapper = document.createElement('div');
        wrapper.className = 'mermaid-container';

        const mermaidDiv = document.createElement('div');
        mermaidDiv.className = 'mermaid';
        mermaidDiv.id = `mermaid-${i}`;
        mermaidDiv.textContent = code;

        wrapper.appendChild(mermaidDiv);
        pre.replaceWith(wrapper);
      });

      await mermaid.run();

      const texts = document.querySelectorAll('text.messageText');

      texts.forEach(text => {
        if (!(text instanceof SVGTextElement) || !text.parentNode) return;
        const bbox = text.getBBox();
        const rect = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
        rect.setAttribute('x', String(bbox.x - 4));
        rect.setAttribute('y', String(bbox.y - 2));
        rect.setAttribute('width', String(bbox.width + 8));
        rect.setAttribute('height', String(bbox.height + 4));
        rect.setAttribute('fill', 'rgba(232,232,232, 0.8)');
        rect.setAttribute('rx', '4');
        rect.setAttribute('ry', '4');

        text.parentNode.insertBefore(rect, text); // insert rect *before* text
      });
    }

    if ('requestIdleCallback' in window) {
      window.requestIdleCallback(renderMermaid);
    } else {
      renderMermaid();
    }
  }, []);

  return null;
}
