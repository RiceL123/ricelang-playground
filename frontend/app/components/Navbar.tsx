import Link from "next/link";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { BookOpen, Info } from "lucide-react";
import ThemeToggle from "./ThemeToggle";

export default function Navbar() {
  return (
    <header className="pointer-events-none fixed top-0 z-40 h-[48px] w-full">
      <div className="mx-auto flex max-w-[1536px] place-content-between px-4 py-3">
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger className="pointer-events-auto">
              <Link
                href="/"
                className="border-accent hover:bg-accent rounded-xl border bg-white/20 py-1 pr-4 pl-3.25 text-xl shadow-sm backdrop-blur-[3px] transition"
              >
                🍚 RiceLang
              </Link>
            </TooltipTrigger>
            <TooltipContent>
              <p>Playground Home Page</p>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>

        <div className="flex gap-4">
          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger className="pointer-events-auto">
                <Link
                  href="/language-definition"
                  className="border-accent hover:bg-accent flex items-center gap-2 rounded-xl border bg-white/20 px-4 py-1 shadow-sm backdrop-blur-[3px] transition"
                >
                  <p className="hidden sm:block">language definition</p>
                  <BookOpen className="h-[1.2rem] w-[1.2rem]" />
                </Link>
              </TooltipTrigger>
              <TooltipContent>
                <p>syntax and grammar definition for ricelang</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>

          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger className="pointer-events-auto">
                <Link
                  href="/about"
                  className="border-accent hover:bg-accent flex items-center gap-2 rounded-xl border bg-white/20 px-4 py-1 shadow-sm backdrop-blur-[3px] transition"
                >
                  <p className="hidden sm:block">about</p>
                  <Info className="h-[1.2rem] w-[1.2rem]" />
                </Link>
              </TooltipTrigger>
              <TooltipContent>
                <p>how I developped the ricelang playground</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>

          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger className="pointer-events-auto">
                <ThemeToggle className="border-accent hover:bg-accent rounded-xl border bg-white/20 px-4 py-1.5 shadow-sm backdrop-blur-[3px] transition" />
              </TooltipTrigger>
              <TooltipContent>
                <p>Toggle light & dark mode</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>
        </div>
      </div>
    </header>
  );
}
