"use client";

import Link from "next/link";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuPortal,
  DropdownMenuSub,
  DropdownMenuSubContent,
  DropdownMenuSubTrigger,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { BookOpen, Info, Menu } from "lucide-react";
import ThemeToggle from "./ThemeToggle";
import { useEffect, useState } from "react";
import FunnyButton from "./FunnyButton";
import React from "react";
import { useAtom } from "jotai";
import { writeSourceCodeAtom, actions, writeOutputAtom } from "@/lib/jotai";
import examples from "@/lib/examples.json";

const Navbar = () => {
  const [action, setAction] = useState<keyof typeof actions>(
    Object.keys(actions)[0],
  );
  const [, setSourceCode] = useAtom(writeSourceCodeAtom);
  const [, request] = useAtom(writeOutputAtom);

  const handleExampleChange = (value: keyof typeof examples) => {
    setSourceCode(examples[value]);
  };

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "s" && e.ctrlKey) {
        e.preventDefault();
        request(actions[action].route);
      }
    };

    document.addEventListener("keydown", handleKeyDown, { capture: true });
    return () =>
      document.removeEventListener("keydown", handleKeyDown, { capture: true });
  }, [action, request]);

  return (
    <header className="h-[48px] w-full">
      <div className="mx-auto flex max-w-[1536px] place-content-between px-4 py-3">
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger>
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
          <div className="border-accent flex overflow-hidden rounded-lg border">
            <FunnyButton onClick={() => request(actions[action].route)}>
              {action}
            </FunnyButton>
            <Select
              onValueChange={(x: keyof typeof actions) => {
                setAction(x);
                request(actions[x].route);
              }}
            >
              <SelectTrigger
                className="!bg-primary border-primary w-[40px] rounded-none border"
                aria-label="Select action"
              />
              <SelectContent>
                {Object.entries(actions).map(([key, val], i) => (
                  <SelectItem value={key} key={i}>
                    <div>
                      <p className="text-lg">{key}</p>
                      <p className="text-sm">{val.desc}</p>
                    </div>
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <Select onValueChange={handleExampleChange}>
            <SelectTrigger
              className="border-accent hover:bg-accent hidden w-[48px] rounded-xl border bg-white/20 shadow-sm backdrop-blur-[3px] transition sm:flex md:w-[180px]"
              aria-label="Select example"
            >
              <SelectValue placeholder="examples" />
            </SelectTrigger>
            <SelectContent className="border-accent border bg-white/20 shadow-sm backdrop-blur-[3px]">
              {Object.keys(examples).map((x, i) => (
                <SelectItem key={i} value={x}>
                  {x}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger className="hidden sm:block">
                <Link
                  href="/language-definition"
                  className="border-accent hover:bg-accent flex items-center gap-2 rounded-xl border bg-white/20 px-4 py-1 shadow-sm backdrop-blur-[3px] transition"
                >
                  <p className="hidden lg:block">language definition</p>
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
              <TooltipTrigger className="hidden sm:block">
                <Link
                  href="/about"
                  className="border-accent hover:bg-accent flex items-center gap-2 rounded-xl border bg-white/20 px-4 py-1 shadow-sm backdrop-blur-[3px] transition"
                >
                  <p className="hidden lg:block">about</p>
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
              <TooltipTrigger className="hidden sm:block">
                <ThemeToggle className="border-accent hover:bg-accent rounded-xl border bg-white/20 px-4 py-1.5 shadow-sm backdrop-blur-[3px] transition" />
              </TooltipTrigger>
              <TooltipContent>
                <p>Toggle light & dark mode</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>

          <DropdownMenu>
            <DropdownMenuTrigger className="border-accent hover:bg-accent rounded-xl border bg-white/20 px-4 py-1.5 shadow-sm backdrop-blur-[3px] transition sm:hidden">
              <Menu className="h-[1.2rem] w-[1.2rem]" />
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem asChild>
                <DropdownMenuSub>
                  <DropdownMenuSubTrigger>
                    <p className="py-2">Examples</p>
                  </DropdownMenuSubTrigger>
                  <DropdownMenuPortal>
                    <DropdownMenuSubContent>
                      {Object.keys(examples).map((x, i) => (
                        <DropdownMenuItem
                          key={i}
                          onClick={() =>
                            handleExampleChange(x as keyof typeof examples)
                          }
                        >
                          {x}
                        </DropdownMenuItem>
                      ))}
                    </DropdownMenuSubContent>
                  </DropdownMenuPortal>
                </DropdownMenuSub>
              </DropdownMenuItem>
              <DropdownMenuItem>
                <Link
                  href="/language-definition"
                  className="flex items-center gap-2"
                >
                  <BookOpen className="h-[1.2rem] w-[1.2rem]" />
                  <p className="py-2">language definition</p>
                </Link>
              </DropdownMenuItem>
              <DropdownMenuItem>
                <Link href="/about" className="flex items-center gap-2">
                  <Info className="h-[1.2rem] w-[1.2rem]" />
                  <p className="py-2">about</p>
                </Link>
              </DropdownMenuItem>
              <DropdownMenuItem asChild>
                <ThemeToggle
                  className="mx-autp py-3.5"
                  text="light / dark mode"
                />
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
