"use client";

import Script from "next/script";
import { createContext, useContext, useRef, useEffect } from "react";

export interface WasmOutput {
  output: string;
  verbose: string;
  error: boolean;
}

export interface WasmInstance {
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
};

const TeaVMContext = createContext<{ teavm: Promise<WasmInstance> } | null>(null);

export function TeaVMProvider({ children }: { children: React.ReactNode }) {
  const teavmPromiseRef = useRef<{
    resolve: (val: WasmInstance) => void;
    reject: (err: unknown) => void;
    promise: Promise<WasmInstance>;
  }>(null);

  if (!teavmPromiseRef.current) {
    let resolve: (val: WasmInstance) => void;
    let reject: (err: unknown) => void;
    const promise = new Promise<WasmInstance>((res, rej) => {
      resolve = res;
      reject = rej;
    });
    teavmPromiseRef.current = { resolve: resolve!, reject: reject!, promise };
  }

  useEffect(() => {
    const loadWasm = async () => {
      try {
        const instance = await TeaVM.wasmGC.load("/ricelang.wasm");
        teavmPromiseRef.current!.resolve(instance);
      } catch (err) {
        teavmPromiseRef.current!.reject(err);
      }
    };
    loadWasm();
  }, []);

  return (
    <>
      <Script
        src="/ricelang.wasm-runtime.js"
        strategy="beforeInteractive"
      />
      <TeaVMContext.Provider value={{ teavm: teavmPromiseRef.current.promise }}>
        {children}
      </TeaVMContext.Provider>
    </>
  );
}

export function useTeaVM() {
  const context = useContext(TeaVMContext);
  if (!context) {
    throw new Error("useTeaVM must be used within a TeaVMProvider");
  }
  return context;
}
