import React, { useState } from "react";

export default function FunnyButton({
  onClick,
  children,
}: {
  onClick?: () => void;
  children: React.ReactNode;
}) {
  const [clicked, setClicked] = useState(false);

  const handleClick = () => {
    setClicked(true);
    onClick?.();
    setTimeout(() => setClicked(false), 350);
  };

  return (
    <button
      onClick={handleClick}
      className="relative overflow-hidden px-4 bg-primary text-primary-foreground h-full"
    >
      <span className="relative z-10 line-clamp-1">{children}</span>
      <span className={`absolute top-0 left-0 h-20 bg-accent/20 transition-all duration-300 z-0 ${clicked ? "w-full" : "w-0"}`} />
      <span className={`absolute top-0 right-0 h-20 bg-accent/20 transition-all duration-300 z-0 ${clicked ? "w-full" : "w-0"}`} />
    </button>
  );
}
