"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Sidebar } from "lucide-react";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";

type TocItem = {
  id: string;
  text: string;
  children?: TocItem[];
};

export default function Toc() {
  const [open, setOpen] = useState(false);
  const [toc, setToc] = useState<TocItem[]>([]);

  useEffect(() => {
    // Query all h2 and h3 in document order
    const headings = Array.from(
      document.querySelectorAll("h2, h3"),
    ) as HTMLElement[];

    const tocItems: TocItem[] = [];
    let currentH2: TocItem | null = null;

    headings.forEach((heading) => {
      const id = heading.id;
      const text = heading.textContent || "";

      if (!id) return; // skip headings without id

      if (heading.tagName.toLowerCase() === "h2") {
        // New top-level section
        currentH2 = { id, text, children: [] };
        tocItems.push(currentH2);
      } else if (heading.tagName.toLowerCase() === "h3") {
        // Subsection of last h2
        if (currentH2) {
          currentH2.children = currentH2.children || [];
          currentH2.children.push({ id, text });
        } else {
          // if no h2 before this h3, just push it at top level
          tocItems.push({ id, text });
        }
      }
    });

    setToc(tocItems);
  }, []);

  const handleLinkClick = (
    e: React.MouseEvent<HTMLAnchorElement>,
    id: string,
  ) => {
    e.preventDefault();

    const target = document.getElementById(id);
    if (target) {
      target.scrollIntoView({ behavior: "smooth", block: "center" });
    }

    if (window.innerWidth < 768) {
      setOpen(false);
    }
  };

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      <SheetTrigger asChild>
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger asChild>
              <Button
                variant="outline"
                className="3xl:top-3 border-accent fixed top-14 left-5 z-10 border bg-white/20 shadow-sm backdrop-blur-[3px] dark:bg-white/20"
                onClick={() => setOpen(true)}
              >
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
        onCloseAutoFocus={(e) => e.preventDefault()}
        side="left"
        className="w-[80%] sm:w-[540px]"
      >
        <SheetHeader className="p-2">
          <SheetTitle className="pl-2">Contents</SheetTitle>
          <SheetDescription>
            Sections of the RiceLang Definition
          </SheetDescription>
        </SheetHeader>
        <div className="w-full overflow-auto px-8">
          <ul className="list-disc pb-10">
            {toc.map(({ id, text, children }) => (
              <li key={id}>
                <a
                  href={`#${id}`}
                  className="transform text-lg hover:underline"
                  onClick={(e) => handleLinkClick(e, id)}
                >
                  {text}
                </a>
                {children && children.length > 0 && (
                  <ul className="list-disc pl-4">
                    {children.map(({ id: subId, text: subText }) => (
                      <li key={subId}>
                        <a
                          href={`#${subId}`}
                          className="transform text-lg hover:underline"
                          onClick={(e) => handleLinkClick(e, subId)}
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
  );
}
