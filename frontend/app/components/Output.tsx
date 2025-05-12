interface OutputProp {
    output: string;
    exitCode: number;
}

export default function Output({ output }: { output: OutputProp }) {
  return (
    <div className="h-full w-full max-w-full max-h-full p-4  overflow-auto rounded-xl bg-primary-foreground/20 backdrop-blur-[4px] border border-2 border-accent-foreground shadow-sm hover:bg-primary-foreground/30 transition">
      <pre>{output.output}</pre>
    </div >
  )
}
