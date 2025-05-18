import Navbar from '../components/Navbar';

export default function About() {
  return (
    <div className="h-full w-full">
      <Navbar />
      <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
        <div className="flex flex-col gap-[32px] row-start-2 items-center sm:items-start">
          <p className="red">This is the about page</p>
          <p>Made this playground cuz it was funny</p>
          <p>I think its pre cool</p>
        </div>
      </div>
    </div>
  );
}
