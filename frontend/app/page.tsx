"use client";
import { useEffect } from "react";
import { useTeaVM } from "./components/TeaVMProvider";
import {
  ResizableHandle,
  ResizablePanel,
  ResizablePanelGroup,
} from "@/components/ui/resizable";
import { Toaster } from "@/components/ui/sonner";
import { toast } from "sonner";
import Navbar from "./components/NavbarHome";
import Output from "./components/Output";
import CodeEditor from "./components/CodeEditor";
import { BookOpen } from "lucide-react";
import { buttonVariants } from "@/components/ui/button";
import Link from "next/link";
import { useIsMobile } from "@/lib/use-mobile";
import { useAtom } from "jotai";
import { BACKEND_URL, teavmAtom } from "@/lib/jotai";

export default function Home() {
  const { teavm } = useTeaVM();
  const [, setTeaVM] = useAtom(teavmAtom);

  useEffect(() => {
    const loadWasmExports = async () => {
      try {
        const teavmModule = await teavm;
        setTeaVM(teavmModule.exports);
      } catch (e) {
        setTeaVM(null);

        toast.error(`WebAssembly failed: ${e}`, {
          description: `Falling back to legacy backend route for all subsequent requests. Check out the Wasm docs for your browser's compatibility.`,
          action: (
            <Link
              href="https://developer.mozilla.org/en-US/docs/WebAssembly#browser_compatibility"
              target="_blank"
              className={buttonVariants({ variant: "default" })}
            >
              <BookOpen />
              Docs
            </Link>
          ),
          duration: 30000,
          closeButton: true,
        });

        toast.promise(
          fetch(`${BACKEND_URL}/healthcheck`).then((res) => {
            if (!res.ok) throw new Error("Backend responded with an error");
            return res;
          }),
          {
            loading: "Checking Spring Boot backend status",
            success: "Spring Boot backend Online",
            error: "Spring Boot backend cannot be reached",
          }
        );

        return;
      }
    };
    loadWasmExports();
  }, [teavm, setTeaVM]);

  return (
    <div className="w-full h-full flex flex-col">
      <Toaster />
      <Navbar />
      <div
        className="grow max-h-full max-w-full"
        style={{ height: "calc(100dvh - 48px)" }}
      >
        <ResizablePanelGroup
          direction={useIsMobile() ? "vertical" : "horizontal"}
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
