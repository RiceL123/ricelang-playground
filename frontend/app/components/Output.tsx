import Loading from "./Loading";
import Mermaid from "./Mermaid";
interface OutputProp {
  isAST: boolean;
  output: string;
  exitCode: number;
}

export default function Output({ output, loading }: { output: OutputProp, loading: boolean }) {
  return (
    <div className="h-full w-full max-w-full max-h-full flex p-4 overflow-auto rounded-xl bg-primary-foreground/20 backdrop-blur-[4px] border border-2 border-accent-foreground shadow-sm hover:bg-primary-foreground/30 transition">
      {loading
        ? <Loading message="Compiling..." />
        : output.isAST
          ? <Mermaid mermaidSrc={output.output} />
          : <pre className="w-full h-full">{output.output}</pre>
      }
    </div >
  )
}
