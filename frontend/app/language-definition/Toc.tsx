'use client'

import { useState } from "react"
import { Button } from "@/components/ui/button"
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet"
import { Sidebar } from 'lucide-react'
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip"

export default function Toc() {
  const [open, setOpen] = useState(false)

  const handleLinkClick = (e: React.MouseEvent<HTMLAnchorElement>, id: string) => {
    e.preventDefault()

    const target = document.getElementById(id)
    if (target) {
      target.scrollIntoView({ behavior: "smooth", block: "center" })
    }

    if (window.innerWidth < 768) {
      setOpen(false)
    }
  }

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      <SheetTrigger asChild>
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger asChild>
              <Button variant="outline" className="fixed top-14 3xl:top-3 left-5 z-10 dark:bg-white/20 bg-white/20 backdrop-blur-[3px] border border-accent shadow-sm" onClick={() => setOpen(true)}>
                <Sidebar />
              </Button>
            </TooltipTrigger>
            <TooltipContent>
              <p>Language Definition Contents</p>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      </SheetTrigger>
      <SheetContent
        onCloseAutoFocus={e => e.preventDefault()}
        side="left"
        className="w-[80%] sm:w-[540px]"
      >
        <SheetHeader className="p-2">
          <SheetTitle className="pl-2">Contents</SheetTitle>
          <SheetDescription>Sections of the RiceLang Definition</SheetDescription>
        </SheetHeader>
        <div className="w-full px-8 overflow-auto">
          <ul className="pb-10 list-disc">
            {[
              ["introduction", "Introduction"],
              ["grammar", "Grammar"],
              ["program-structure", "Program Structure", [
                ["comments", "Comments"],
                ["separators", "Separators"],
                ["identifiers", "Identifiers"],
              ]],
              ["operators", "Operators"],
              ["basic-types", "Basic Types", [
                ["int", "int"],
                ["float", "float"],
                ["boolean", "boolean"],
                ["string-literals", "string literals"],
              ]],
              ["arrays", "Arrays"],
              ["variables", "Variables"],
              ["statements", "Statements", [
                ["if", "If"],
                ["while", "While"],
                ["for", "For"],
                ["break", "Break"],
                ["continue", "Continue"],
                ["byebye", "Byebye"],
                ["expression-statements", "Expression Statements"],
              ]],
              ["scope-rules", "Scope rules"],
              ["functions", "Functions", [
                ["built-in", "Built-in"],
              ]]
            ].map(([id, text, children], i) => (
              <li key={i}>
                <a
                  href={`#${id}`}
                  className="transform hover:underline text-lg"
                  onClick={e => handleLinkClick(e, id as string)}
                >
                  {text}
                </a>
                {Array.isArray(children) && (
                  <ul className="pl-4 list-disc">
                    {children.map(([subId, subText]) => (
                      <li key={subId}>
                        <a
                          href={`#${subId}`}
                          className="transform hover:underline text-lg"
                          onClick={e => handleLinkClick(e, subId)}
                        >
                          {subText}
                        </a>
                      </li>
                    ))}
                  </ul>
                )}
              </li>
            ))}
          </ul>
        </div>
      </SheetContent>
    </Sheet>
  )
}
