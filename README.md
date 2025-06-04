# ricelang playground
ricelang is a simple programming language that supports a weird blend of c and java syntax that compiles to java byte code and runs on the JVM.

## running
```sh
# compile wasm and copy to the frontend
cd wasm/
./gradlew buildWasm --no-configuration-cache
cp app/build/generated/teavm/wasm-gc/* ../frontend/public
```

```sh
# start the backend
cd ricelang/
./gradlew bootRun
```

```sh
# start the frontend
cd frontend/
npm run dev
```

## Technologies
- [Java](https://www.java.com/) + [Gradle](https://gradle.org/)
- [Jasmin](https://jasmin.sourceforge.net/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Nextjs](https://nextjs.org/) + [React](https://react.dev/)
- [Shadcn](https://ui.shadcn.com/) + [Tailwind](https://tailwindcss.com/)
- [Monaco](https://microsoft.github.io/monaco-editor/)
- [Mermaid](https://mermaid.js.org/)
- [Markdown](https://markdownguide.org/) + [remark](https://remark.js.org/)
- [TeaVM](https://teavm.org/)
- [Vercel](https://vercel.com)
- [Render](https://render.com)
- [Docker](https://docker.com)
