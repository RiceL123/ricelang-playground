export default function Loading({ message }: { message: string }) {
  return (
    <div className="flex items-center gap-2">
      <div>{message}</div>
      <style>
        {`
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
    </div>
  );
}
