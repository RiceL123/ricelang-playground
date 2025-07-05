import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export const BACKEND_URL = "https://ricelang-playground.onrender.com";
// export const BACKEND_URL = "http://127.0.0.1:8080";
