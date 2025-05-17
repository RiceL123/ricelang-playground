import Link from 'next/link'
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { Button } from "@/components/ui/button"
import { BookOpen, Info } from 'lucide-react'
import ThemeToggle from './ThemeToggle';

export const examples: { [key: string]: string } = {
  "hello world": `// Hello World by ricel123 in ricelang 17/05/2025

int main() {
    putStringLn("Hello World");
    return 0;
}
`,
  "mendelbrot": `// Mendlebrot in ricelang

int MAX_DEPTH = 100;
float LIMIT = 8.0;
int WIDTH = 150;
int HEIGHT = 50;
float REAL_MIN = -2.5;
float REAL_MAX = 1.0;
float IMAG_MIN = -1.5;
float IMAG_MAX = 1.5;
int COLOUR_OFFSET = 2;

int mod(int a, int b) {
    return a - (a / b * b);
}

int mandelbrot(float real, float imag) {
    float z_real = 0.0;
    float z_imag = 0.0;
    float z_real2 = 0.0;
    float z_imag2 = 0.0;
    int depth = 0;

    while (depth < MAX_DEPTH && z_real2 + z_imag2 < LIMIT) {
        z_imag = 2.0 * z_real * z_imag + imag;
        z_real = z_real2 - z_imag2 + real;
        z_real2 = z_real * z_real;
        z_imag2 = z_imag * z_imag;
        depth = depth + 1;
    }

    return depth;
}

int main() {
    int x, y;
    float real, imag;
    int depth;

    for (y = 0; y < HEIGHT; y = y + 1) {
        for (x = 0; x < WIDTH; x = x + 1) {
            real = REAL_MIN + (REAL_MAX - REAL_MIN) * x / WIDTH;
            imag = IMAG_MIN + (IMAG_MAX - IMAG_MIN) * y / HEIGHT;
            depth = mandelbrot(real, imag);
            if (depth == MAX_DEPTH) {
                putString("#");
            } else {
                putString(" ");
            }
        }
        putLn();
    }
}
`,
  "boxes": `// Boxes in ricelang
int mod(int a, int b) {
    return a - a / b * b;
}

int main() {
    int n;
    int size;
    int row;
    int col;

    n = 10;

    size = 4 * n - 1;

    for (row = 0; row < size; row = row + 1) {
        for (col = 0; col < size; col = col + 1) {
            if ((mod(row, 2) == 0) && ((mod(col, 2) == 0) || row < col && row < size - col || row >= col && row >= size - col) ||
                (mod(col, 2) == 0) && (col < row && col < size - row || col >= row && col >= size - row)) {
                putString("*");
                } else {
                    putString("-");
                }
        }
        putLn();
    }

    return 0;
}
`,
  "bubble": `/* bubble.ricelang -- Read an integer array, print it, then sort it and
 * print it. Use the bubble sort method.
*/

void printIntArray(int a[], int n)
/* n is the number of elements in the array a.
 * These values are printed out, five per line.
*/
{
    int i;

    for (i=0; i<n; i=i+1) {
        putInt(a[i]);
        putString(" ");
    }
    putLn();
}

void bubbleSort(int a[], int n)
/* It sorts in non-decreasing order the first N positions of A. It uses
 * the bubble sort method.
*/
{
    int lcv;
    int limit = n-1;
    int temp;
    int lastChange;

    while (limit != 0) {
        lastChange = 0;
        for (lcv=0;lcv<limit;lcv=lcv+1)
            /* Notice that the values in positions LIMIT+1 .. N are in
             * their final position, i.e. they are sorted right 
            */
            if (a[lcv]>a[lcv+1]) {
                temp = a[lcv];
                a[lcv] = a[lcv+1];
                a[lcv+1] = temp;
                lastChange = lcv;
            }
            limit = lastChange;
    }
}

int main() {
    int x[10] = {3, 10, 1, 5, 8, 0, 20, 1, 4, 100};
    int n = 10;

    putStringLn("The array was:");
    printIntArray(x,n);

    putLn();

    bubbleSort(x,n);
    putStringLn("The sorted array is:");
    printIntArray(x,n);

}
`,
  "spiral": `// decimal spiral in ricelang

int solve(int n, int x, int y) {
    int k = (n - 1) * (n + 3) / 2;

    if (y == 0) {
        return k - x;
    } else if (y == 1) {
        if (x == n - 1) {
            return k - x - 1;
        }

        return -1;
    } else if (y == n - 2) {
        if (x == 0) {
            return k - 3 * (n - 1) - 1;
        } else if (x == n - 1) {
            return k - 2 * (n - 1) + 1;
        }

        return -1;
    } else if (y == n - 1) {
        return k - 3 * (n - 1) + x;
    }

    if (x == 0) {
        return k - 4 * (n - 1) + y;
    } else if (x == 1) {
        if (y == 2) {
            return k - 4 * (n - 1) + y - 1;
        }

        return -1;
    } else if (x == n - 1) {
        return k - (n - 1) - y;
    } else if (x == n - 2) {
        return -1;
    } else {
        return solve(n - 4, x - 2, y - 2);
    }

    return -1;
}

int mod(int a, int b) {
    return a - a / b * b;
}

int main() {
    int n, k, y, x;
    n = 13;

    for (y = 0; y < n; y = y + 1) {
        for (x = 0; x < n; x = x + 1) {
            k = solve(n, x, y);
            if (k == -1) {
                putString("-");
            } else {
                putInt(mod(k, 10));
            }
        }
        putLn();
    }
}
`,
  "fibonacci": `// memoized fibonacci in ricelang

int fibonacci(int n, int memo[]) {
  if (n <= 1) return n;

  if (memo[n] != 0) return memo[n];

  memo[n] = fibonacci(n - 1, memo) + fibonacci(n - 2, memo);

  return memo[n];
}

int main() {
    int n = 25;
    int memo[26]; // make sure memo size is n + 1
    int i;

    for (i = 0; i <= n; i = i + 1) {
        memo[i] = 0;
    }

    fibonacci(n, memo);

    for (i = 0; i <= n; i = i + 1) {
        putString("fibonacci(");
        putInt(i);
        putString(") = ");
        putIntLn(memo[i]);
    }

    return 0;
}
`,
  "prime": `// isPrime in ricelang

int mod(int a, int b) {
    return a - (a / b * b);
}

boolean isPrime(int n) {
    int i = 2;
    if (n <= 1) return false;
    while (i * i <= n) {
        if (mod(n, i) == 0) return false;
        i = i + 1;
    }
    return true;
}

int main() {
    int num = 100000001; // funnily enough is divisble by 17
    boolean result = isPrime(num);

    putString("The number ");
    putInt(num);
    putString(" is prime: ");
    putBoolLn(result);

    {
        int i;
        // all except 333333331 are prime for it is also divisble by 17
        int nums[8] = {31, 331, 3331, 33331, 333331, 3333331, 33333331, 333333331};
        for (i = 0; i < 8; i = i + 1) {
            num = nums[i];
            result = isPrime(num);

            putString("The number ");
            putInt(num);
            putString(" is prime: ");
            putBoolLn(result);
        }
    }

    return 0;
}
`,
  "sine": `// sine wave in ricelang

float PI = 3.1415926535;

float sin(float x) {    
    float x2 = x * x, x3 = x2 * x, x5 = x3 * x2, x7 = x5 * x2, x9 = x7 * x2, x11 = x9 * x2, x13 = x11 * x2;

    return x - x3 / 6.0 + x5 / 120.0 - x7 / 5040.0 + x9 / 362880.0 - x11 / 39916800.0  + x13 / 6227020800.0;
}

int main() {
    float amplitude = 24.0, wavelength = 30.0, x, y, offset;
    int width = 60, i = 0, j;

    while (i < width) {
        x = i / wavelength * 2 * PI; 

        while (x > PI) {
            x = x - PI * 2;
        }

        y = sin(x);

        offset = y * amplitude + amplitude;
         
        j = 0;
        while (j < offset) {
            putString(" ");
            j = j + 1;
        }

        putStringLn("#");
        i = i + 1;
    }
} 
`
}

export default function Navbar({ setSourceCode, compile }: { setSourceCode: React.Dispatch<React.SetStateAction<string>>, compile: (srcCode?: string) => Promise<void> }) {
  const handleExampleChange = (value: string) => {
    setSourceCode(examples[value]);
  }

  return (
    <header className="w-full h-[48px]">
      <div className="mx-auto py-3 px-4 flex place-content-between max-w-[1536px]">
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
              <TooltipTrigger asChild>
                <Button onClick={() => compile()}>Compile!</Button>
              </TooltipTrigger>
              <TooltipContent>
                <p>Ctrl + S</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>

          <Select onValueChange={handleExampleChange}>
            <SelectTrigger className="w-[48px] sm:w-[180px] rounded-xl bg-white/20 backdrop-blur-[3px] border border-accent shadow-sm hover:bg-accent transition">
              <SelectValue placeholder="examples" />
            </SelectTrigger>
            <SelectContent className="bg-white/20 backdrop-blur-[3px] border border-accent shadow-sm">
              {Object.keys(examples).map((x, i) => (
                <SelectItem key={i} value={x}>
                  {x}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <hr />

          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger>
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
              <TooltipTrigger>
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
