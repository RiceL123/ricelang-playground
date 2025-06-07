'use client'

import Image from 'next/image'

import book from '../images/book.webp'
import mouse from '../images/mouse.webp'

export default function Background() {
  return (
    <div className="fixed inset-0 -z-10">
      <div
        className="absolute bottom-0 w-[200%] h-32 overflow-hidden"
      >
        <style jsx>{`
          @keyframes wave {
            0% {
              transform: translateX(-90px);
            }
            100% {
              transform: translateX(85px);
            }
          }
        `}</style>
        <svg
          className="w-full h-full text-blue-400"
          viewBox="0 24 150 28"
          preserveAspectRatio="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            style={{
              animation: 'wave 10s linear infinite',
            }}
            fill="currentColor"
            opacity="0.7"
            d="M-160 44c30 0 58-18 88-18s 58 18 88 18 58-18 88-18 58 18 88 18 v44h-352z"
          />
          <path
            style={{
              animation: 'wave 15s linear infinite',
            }}
            fill="rebeccapurple"
            opacity="0.7"
            d="M-160 44c30 0 58-18 88-18s 58 18 88 18 58-18 88-18 58 18 88 18 v44h-352z"
          />
          <path
            style={{
              animation: 'wave 20s linear infinite',
            }}
            fill="red"
            opacity="0.2"
            d="M-160 44c30 0 58-18 88-18s 58 18 88 18 58-18 88-18 58 18 88 18 v44h-352z"
          />
        </svg>

        <div className='fixed top-0 w-full h-full'>
          <Image
            className="dark:invert dark:grayscale fixed w-auto bottom-5 left-1/2 md:translate-x-72 animate-hover transition-transform"
            src={book}
            width={352}
            height={268}
            alt='background book prop'
            priority
          />
          <Image
            className="dark:invert dark:grayscale fixed w-auto top-0 left-0 animate-hover-15 transition-transform will-change-transform"
            src={mouse}
            width={315}
            height={534}
            alt='background computer mouse prop'
            priority
          />
        </div>
      </div>
    </div>
  )
}
