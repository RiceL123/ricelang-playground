import Link from "next/link";
import Navbar from "./components/Navbar";

export default function NotFound() {
  return (
    <div className="h-full w-full">
      <Navbar />
      <style>{`

.circles {
	position: fixed;
	top: 0;
	left: 0;
	width: 100vw;
	height: 100vh;
	z-index: -1;
}

.circles li {
	position: absolute;
	display: block;
	list-style: none;
	width: 40px;
	height: 40px;
	background: rgb(96, 183, 199);
	animation: animate 25s linear infinite;
	top: -150px;
}

.circles li:nth-child(1) {
	left: 25%;
	width: 160px;
	height: 160px;
	animation-delay: 0s;
	animation-duration: 20s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(47, 44, 92);
}

.circles li:nth-child(2) {
	left: 10%;
	width: 40px;
	height: 40px;
	animation-delay: 0s;
	animation-duration: 25s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(96, 183, 199);
}

.circles li:nth-child(3) {
	left: 70%;
	width: 40px;
	height: 40px;
	animation-delay: 0s;
	animation-duration: 20s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(47, 44, 92);
}

.circles li:nth-child(4) {
	left: 40%;
	width: 60px;
	height: 60px;
	animation-delay: 0s;
	animation-duration: 15s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(114, 63, 145);
}

.circles li:nth-child(5) {
  position: relative;
  left: 65%;
  width: 120px;
  height: 120px;
  animation-delay: 0s;
  animation-duration: 20s;
  background: transparent;
  border: none;
  border-radius: 0;
  transform: rotate(45deg); /* overall rotation to form the heart */
}

.circles li:nth-child(6) {
	left: 75%;
	width: 90px;
	height: 90px;
	animation-delay: 0s;
	animation-duration: 25s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(114, 63, 145);
}

.circles li:nth-child(7) {
	left: 35%;
	width: 50px;
	height: 50px;
	animation-delay: 0s;
	animation-duration: 30s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(47, 44, 92);
}

.circles li:nth-child(8) {
	left: 50%;
	width: 25px;
	height: 25px;
	animation-delay: 0s;
	animation-duration: 35s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(114, 63, 145);
}

.circles li:nth-child(9) {
	left: 20%;
	width: 15px;
	height: 15px;
	animation-delay: 0s;
	animation-duration: 60s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(47, 44, 92);
}

.circles li:nth-child(10) {
	left: 85%;
	width: 50px;
	height: 50px;
	animation-delay: 0s;
	animation-duration: 45s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(96, 183, 199);
}

.circles li:nth-child(11) {
	left: 5%;
	width: 30px;
	height: 30px;
	animation-delay: 2s;
	animation-duration: 20s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(114, 63, 145);
}

.circles li:nth-child(12) {
	left: 30%;
	width: 70px;
	height: 70px;
	animation-delay: 4s;
	animation-duration: 30s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(96, 183, 199);
}

.circles li:nth-child(13) {
	left: 60%;
	width: 40px;
	height: 40px;
	animation-delay: 1s;
	animation-duration: 25s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(47, 44, 92);
}

.circles li:nth-child(14) {
	left: 80%;
	width: 35px;
	height: 35px;
	animation-delay: 3s;
	animation-duration: 28s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(114, 63, 145);
}

.circles li:nth-child(15) {
	left: 15%;
	width: 100px;
	height: 100px;
	animation-delay: 0.5s;
	animation-duration: 22s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(47, 44, 92);
}

.circles li:nth-child(16) {
	left: 45%;
	width: 20px;
	height: 20px;
	animation-delay: 2.5s;
	animation-duration: 80s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(96, 183, 199);
}

.circles li:nth-child(17) {
	left: 55%;
	width: 85px;
	height: 85px;
	animation-delay: 1.5s;
	animation-duration: 18s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(114, 63, 145);
}

.circles li:nth-child(18) {
	left: 35%;
	width: 45px;
	height: 45px;
	animation-delay: 0s;
	animation-duration: 52s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(96, 183, 199);
}

.circles li:nth-child(19) {
	left: 90%;
	width: 60px;
	height: 60px;
	animation-delay: 3s;
	animation-duration: 24s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(47, 44, 92);
}

.circles li:nth-child(20) {
	left: 50%;
	width: 25px;
	height: 25px;
	animation-delay: 1s;
	animation-duration: 72s;
	border-radius: 50%;
	border-top-left-radius: 0 !important;
	background: rgb(96, 183, 199);
}

@keyframes animate {
	0% {
		transform: translateY(0vh) rotate(0deg);
    opacity: 0.6;
	}
	50% {
		transform: translateY(60vh) rotate(360deg);
    opacity: 0.4;
	}
	100% {
		transform: translateY(120vh) rotate(720deg);
    opacity: 0;
	}
}
      `}</style>
      <main className="mx-auto my-16 max-w-[960px] px-4">
        <h1 className="mb-6 text-5xl font-extrabold">404 — Page Not Found</h1>
        <p className="text-muted-foreground mb-10 text-lg">
          You dummy! Navigate back to a real page
        </p>

        <div className="grid grid-cols-1 gap-6 sm:grid-cols-3">
          <Link
            href="/"
            className="block rounded-lg border border-gray-300 p-4 shadow backdrop-blur-[2px] backdrop-hue-rotate-180 transition hover:shadow-lg"
          >
            <h2 className="!text-secondary-foreground mb-2 text-xl font-semibold">
              Home
            </h2>
            <p className="text-muted-foreground">The RiceLang playground</p>
          </Link>

          <Link
            href="/language-definition"
            className="block rounded-lg border border-gray-300 p-4 shadow backdrop-blur-[2px] backdrop-hue-rotate-90 transition hover:shadow-lg"
          >
            <h2 className="!text-secondary-foreground mb-2 text-xl font-semibold">
              Language Definition
            </h2>
            <p className="text-muted-foreground">Read the Docs</p>
          </Link>

          <Link
            href="/about"
            className="block rounded-lg border border-gray-300 p-4 shadow backdrop-blur-[2px] backdrop-hue-rotate-270 transition hover:shadow-lg"
          >
            <h2 className="!text-secondary-foreground mb-2 text-xl font-semibold">
              About
            </h2>
            <p className="text-muted-foreground">
              Learn more about the RiceLang playground development
            </p>
          </Link>
        </div>
      </main>
      <ul className="circles">
        {Array.from({ length: 20 }).map((_, i) => (
          <li key={i} />
        ))}
      </ul>
    </div>
  );
}
