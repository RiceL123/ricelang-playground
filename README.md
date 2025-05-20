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
# start the frontend
cd frontend/
npm run dev
```

```sh
# start the backend
cd ricelang/
./gradlew bootRun
```
