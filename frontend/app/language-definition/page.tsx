import Navbar from '../components/Navbar';

export default function LangDef() {
  return (
    <div className="h-full w-full">
      <Navbar />
      <main className='mx-auto my-6 max-w-[960px] backdrop-blur-xs border border-accent p-4'>
        <h2 className="underline text-2xl text-center">The RiceLang Definition</h2>
        <h3 className='underline text-xl'>Introduction</h3>
        <p>RiceLang is a simple programming language that is kinda a subset mix of C and Java with some oddities (like the fact that there is no return keyword; the keyword <code>byebye</code> is used which does the exact same thing)</p>
        <p>It has support for:</p>
        <ul className='ml-2'>
          <li>- primitive data types like <code>int</code>, <code>float</code>, <code>boolean</code> and string literals</li>
          <li>- one dimensional arrays (seebs more dimensions)</li>
          <li>- compound statements and control structures: <code>if</code>, <code>while</code>, <code>for</code>, <code>continue</code>, <code>break</code>, and <code>byebye</code></li>
          <li>- expressions: assignments, logical, relational and arithmetic</li>
        </ul>
      </main>
    </div>
  );
}
