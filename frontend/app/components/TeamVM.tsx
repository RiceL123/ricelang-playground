"use client";

import { buttonVariants } from "@/components/ui/button";
import { BACKEND_URL } from "@/lib/utils";
import { atom, useAtom } from "jotai";
import { BookOpen } from "lucide-react";
import Link from "next/link";
import Script from "next/script";
import { useEffect } from "react";
import { toast } from "sonner";

export interface WasmOutput {
  output: string;
  verbose: string;
  error: boolean;
}

interface WasmInstance {
  exports: {
    getJasmin(input: string): WasmOutput;
    getMermaid(input: string): WasmOutput;
    getNodeJS(input: string): WasmOutput;
    getVanillaJS(input: string): WasmOutput;
  };
  instance: WebAssembly.Instance;
  module: WebAssembly.Module;
}

declare const TeaVM: {
  wasmGC: {
    load: (path: string) => Promise<WasmInstance>;
  };
}; // not undefined if /ricelang.wasm-runtime.js completes

export const teavmAtom = atom<WasmInstance["exports"] | undefined | null>(
  undefined,
);

export default function TeamVM() {
  const [, setTeaVM] = useAtom(teavmAtom);

  useEffect(() => {
    const loadWasm = async () => {
      try {
        const wasm = await TeaVM.wasmGC.load("/ricelang.wasm");
        setTeaVM(wasm.exports);
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
          duration: Infinity,
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
          },
        );
      }
    };

    loadWasm();
  }, [setTeaVM]);

  return (
    <Script src="/ricelang.wasm-runtime.js" strategy="beforeInteractive" />
  );
}
