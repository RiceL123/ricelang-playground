#!/usr/bin/env bash

echo "🤩 ahead of time compiling wasm binary: $WASM_PID"
(
  cd wasm/
  ./gradlew buildWasm --no-configuration-cache
  cp app/build/generated/teavm/wasm-gc/* ../frontend/public
) &
WASM_PID=$!

wait $WASM_PID

echo "😎 starting frontend and backend"
(
  cd ricelang/
  ./gradlew bootRun 2>&1 | sed 's/^/☕: /'
) &
BACKEND_PID=$!

(
  cd frontend/
  npm run dev 2>&1 | sed 's/^/⚛️: /'
) &
FRONTEND_PID=$!

trap "echo 💀 killing frontend pid: $FRONTEND_PID, backend pid $BACKEND_PID... && kill -SIGINT $BACKEND_PID $FRONTEND_PID" SIGINT

wait $BACKEND_PID $FRONTEND_PID
