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
    <header className="w-full h-12">
      <div className="mx-auto py-3 flex place-content-between max-w-[1536px]">
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger>
              <Link
                href="/"
                className="text-xl px-4 py-1 rounded-xl bg-white/20 backdrop-blur-[3px] border border-accent shadow-sm hover:bg-accent transition"
              >
                RiceLang
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
              <TooltipTrigger>
                <Link
                  href="/language-definition"
                  className="flex gap-2 px-4 py-1 items-center rounded-xl bg-white/20 backdrop-blur-[3px] border border-accent shadow-sm hover:bg-accent transition"
                >
                  language definition
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
              <TooltipTrigger>
                <Link
                  href="/about"
                  className="flex gap-2 px-4 py-1 items-center rounded-xl bg-white/20 backdrop-blur-[3px] border border-accent shadow-sm hover:bg-accent transition"
                >
                  about
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
              <TooltipTrigger>
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