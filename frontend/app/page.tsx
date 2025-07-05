"use client";

import {
  ResizableHandle,
  ResizablePanel,
  ResizablePanelGroup,
} from "@/components/ui/resizable";
import { Toaster } from "@/components/ui/sonner";
import { useIsMobile } from "@/lib/use-mobile";

import Navbar from "./components/NavbarHome";
import Output from "./components/Output";
import CodeEditor from "./components/CodeEditor";

export default function Home() {
  const isMobile = useIsMobile();

  return (
    <div className="flex h-full w-full flex-col">
      <Toaster />
      <Navbar />
      <div
        className="max-h-full max-w-full grow"
        style={{ height: "calc(100dvh - 48px)" }}
      >
        <ResizablePanelGroup
          direction={isMobile ? "vertical" : "horizontal"}
          className="box-border flex gap-2 p-3"
        >
          <ResizablePanel defaultSize={50} key="editor-panel">
            <CodeEditor />
          </ResizablePanel>
          <ResizableHandle className="opacity-0" />
          <ResizablePanel defaultSize={50} key="output-panel">
            <Output />
          </ResizablePanel>
        </ResizablePanelGroup>
      </div>
    </div>
  );
}
