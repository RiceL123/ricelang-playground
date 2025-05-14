interface OutputProp {
  output: string;
  exitCode: number;
}

export default function Output({ output, loading }: { output: OutputProp, loading: boolean }) {
  return (
    <div className="h-full w-full max-w-full max-h-full p-4 overflow-auto rounded-xl bg-primary-foreground/20 backdrop-blur-[4px] border border-2 border-accent-foreground shadow-sm hover:bg-primary-foreground/30 transition">
      {loading ? (<div className="flex gap-2 items-center">
        <div>Compiling...</div>
        <style>{`
.loader {
  --c:no-repeat linear-gradient(var(--primary) 0 0);
  background: 
    var(--c),var(--c),var(--c),
    var(--c),var(--c),var(--c),
    var(--c),var(--c),var(--c);
  background-size: 16px 16px;
  animation: 
    l32-1 1s infinite,
    l32-2 1s infinite;
}
@keyframes l32-1 {
  0%,100% {width:45px;height: 45px}
  35%,65% {width:65px;height: 65px}
}
@keyframes l32-2 {
  0%,40%  {background-position: 0 0,0 50%, 0 100%,50% 100%,100% 100%,100% 50%,100% 0,50% 0,  50% 50% }
  60%,100%{background-position: 0 50%, 0 100%,50% 100%,100% 100%,100% 50%,100% 0,50% 0,0 0,  50% 50% }
}
        `}
        </style>
        <div className="loader"></div>
      </div>) : <pre>{output.output}</pre>}
    </div >
  )
}
