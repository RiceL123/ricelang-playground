'use client'
import { getImageProps } from 'next/image'

const imgPath = "/background-transparent.webp";

export default function Background() {
  
  function getBackgroundImage(srcSet = '') {
    const imageSet = srcSet
      .split(', ')
      .map((str) => {
        const [url, dpi] = str.split(' ')
        return `url("${url}") ${dpi}`
      })
      .join(', ')
    return `image-set(${imageSet})`
  }

  const { props: { srcSet }} = getImageProps({ alt: '', width: 1920, height: 2600, src: imgPath })

  const backgroundImage = getBackgroundImage(srcSet)

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
      </div>

      <div
        className="absolute -inset-px bg-cover bg-center opacity-80 dark:invert dark:grayscale"
        style={{ height: '100vh', width: '100vw', backgroundImage }}
      />
    </div>
  )
}
