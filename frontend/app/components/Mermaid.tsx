"use client";
import React, { useEffect, useRef, useState } from "react";
import mermaid from "mermaid";
import { useTheme } from "next-themes";
import { TransformWrapper, TransformComponent } from "react-zoom-pan-pinch";

import Loading from "./Loading";

export default function Mermaid({ mermaidSrc }: { mermaidSrc: string }) {
  const ref = useRef<HTMLDivElement>(null);
  const { resolvedTheme } = useTheme();
  const [error, SetError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    mermaid.initialize({
      startOnLoad: false,
      maxEdges: 1000,
      theme: "base",
      themeVariables: {
        background: "transparent",
        primaryColor: "transparent",
        primaryTextColor: "#000",
        primaryBorderColor: "#000",
        lineColor: "#000",
        textColor: "#000",
      },
    });

    let isMounted = true;

    if (ref.current) {
      mermaid
        .render("generated-mermaid", mermaidSrc)
        .then(({ svg }) => {
          if (isMounted && ref.current) {
            ref.current.innerHTML = svg;
          }
        })
        .catch((err) => SetError(String(err)))
        .finally(() => setLoading(false));
    }

    return () => {
      isMounted = false;
    };
  }, [mermaidSrc]);

  return (<>
    {loading && <Loading message="Rendering Mermaid..." />}
    {error && <pre>{error}</pre>}
    <TransformWrapper>
      <TransformComponent wrapperStyle={{ height: "100%", width: "100%" }} contentStyle={{ height: "100%", width: "100%" }}>
        <div ref={ref} className={`h-full w-full transition-colors duration-300 ${resolvedTheme === 'dark' && 'invert'}`} />
      </TransformComponent>
    </TransformWrapper>
  </>
  );
}
