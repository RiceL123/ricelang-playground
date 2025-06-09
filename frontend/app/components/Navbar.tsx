import Link from 'next/link'
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip"
import { BookOpen, Info } from 'lucide-react'
import ThemeToggle from './ThemeToggle';

export default function Navbar() {
  return (
    <header className="fixed top-0 w-full h-[48px] pointer-events-none z-40">
      <div className="mx-auto py-3 px-4 flex place-content-between max-w-[1536px]">
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger className='pointer-events-auto'>
              <Link
                href="/"
                className="text-xl pl-3.25 pr-4 py-1 rounded-xl bg-white/20 backdrop-blur-[3px] border border-accent shadow-sm hover:bg-accent transition"
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
              <TooltipTrigger className='pointer-events-auto'>
                <Link
                  href="/language-definition"
                  className="flex gap-2 px-4 py-1 items-center rounded-xl bg-white/20 backdrop-blur-[3px] border border-accent shadow-sm hover:bg-accent transition"
                >
                  <p className='hidden sm:block'>language definition</p>
                  <BookOpen className='h-[1.2rem] w-[1.2rem]' />
                </Link>
              </TooltipTrigger>
              <TooltipContent>
                <p>syntax and grammar definition for ricelang</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>

          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger className='pointer-events-auto'>
                <Link
                  href="/about"
                  className="flex gap-2 px-4 py-1 items-center rounded-xl bg-white/20 backdrop-blur-[3px] border border-accent shadow-sm hover:bg-accent transition"
                >
                  <p className='hidden sm:block'>about</p>
                  <Info className='h-[1.2rem] w-[1.2rem]' />
                </Link>
              </TooltipTrigger>
              <TooltipContent>
                <p>how I developped the ricelang playground</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>


          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger className='pointer-events-auto'>
                <ThemeToggle
                  className="px-4 py-1.5 rounded-xl bg-white/20 backdrop-blur-[3px] border border-accent shadow-sm hover:bg-accent transition"
                />
              </TooltipTrigger>
              <TooltipContent>
                <p>Toggle light & dark mode</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>

        </div>
      </div>
    </header>
  )
}