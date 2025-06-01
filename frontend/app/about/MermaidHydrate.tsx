'use client';

import { useEffect } from 'react';
import mermaid from 'mermaid';

mermaid.initialize({ startOnLoad: false });

export default function MermaidHydration() {
  useEffect(() => {
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

    mermaid.run();
  }, []);

  return null;
}
