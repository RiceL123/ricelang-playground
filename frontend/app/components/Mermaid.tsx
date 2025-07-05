"use client";

import React, { useEffect, useRef, useState } from "react";
import mermaid from "mermaid";
import { useTheme } from "next-themes";
import { TransformWrapper, TransformComponent } from "react-zoom-pan-pinch";

import Loading from "./Loading";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";

import { Plus, Minus } from "lucide-react";

export default function Mermaid({ mermaidSrc }: { mermaidSrc: string }) {
  const ref = useRef<HTMLDivElement>(null);
  const { resolvedTheme } = useTheme();
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [showSource, setShowSource] = useState(false);

  useEffect(() => {
    setLoading(true);
    let isMounted = true;

    toast.promise(
      (async () => {
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

        if (ref.current) {
          mermaid
            .render("generated-mermaid", mermaidSrc)
            .then(({ svg }) => {
              if (isMounted && ref.current) {
                ref.current.innerHTML = svg;
              }
            })
            .catch((err) => setError(String(err)))
            .finally(() => setLoading(false));
        }
      })(),
      {
        loading: "Rendering mermaid chart...",
        success: "Rendered mermaid chart",
        error: "Failed to render mermaid chart",
      },
    );

    return () => {
      isMounted = false;
    };
  }, [mermaidSrc]);

  return (
    <>
      <style>
        {`
      #generated-mermaid { height: 100%; }
      `}
      </style>
      {loading && <Loading message="Rendering Mermaid..." />}
      {error && <pre className="text-red-500">{error}</pre>}

      {showSource && (
        <pre className="bg-blur absolute z-10 w-full overflow-x-auto">
          <code>{mermaidSrc}</code>
        </pre>
      )}

      <TransformWrapper maxScale={16}>
        {({ zoomIn, zoomOut }) => (
          <>
            <div className="absolute top-2 right-2 z-20 flex gap-2">
              <Button
                className="bg-blur"
                variant="outline"
                onClick={() => zoomIn()}
              >
                <Plus />
              </Button>
              <Button
                className="bg-blur"
                variant="outline"
                onClick={() => zoomOut()}
              >
                <Minus />
              </Button>
              <Button
                className="bg-blur"
                variant="outline"
                onClick={() => setShowSource((prev) => !prev)}
              >
                {showSource ? "Show Diagram" : "Show Source"}
              </Button>
            </div>

            <TransformComponent
              wrapperStyle={{ height: "100%", width: "100%" }}
              contentStyle={{
                minHeight: "100%",
                height: "100%",
                minWidth: "100%",
              }}
            >
              <div
                ref={ref}
                className={`h-full w-full transition-opacity duration-300 ${resolvedTheme === "dark" ? "invert" : ""} ${showSource ? "opacity-0" : "opacity-100"} `}
              />
            </TransformComponent>
          </>
        )}
      </TransformWrapper>
    </>
  );
}
