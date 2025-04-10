#!/bin/bash

echo "Tests for Checker"
javac vc.java

temp_file_actual="temp_actual.out"

# Function to run error tests
run_error_tests() {
    dir=$1
    for input_file in "$dir"/*.vc; do
        java VC.vc "$input_file" > "$temp_file_actual"

        sol_file="${input_file%.vc}.sol"
        grep ERROR "$sol_file" | while read -r line; do
            error_number=$(echo "$line" | grep -Eo "\*[0-9]+:")
            if grep -q "$error_number" "$temp_file_actual"; then
                echo "$input_file: passed ✅"
            else
                echo "$input_file: failed ❌ - can't find error: $error_number"
            fi
        done
    done
}

# Function to run success tests (no errors expected)
run_success_tests() {
    dir=$1
    for input_file in "$dir"/*.vc; do
        java VC.vc "$input_file" > "$temp_file_actual"
        last_line=$(tail -n 1 "$temp_file_actual")

        if [[ "$last_line" == "Compilation was successful." ]]; then
            echo "$input_file: passed ✅"
        else
            echo "$input_file: failed ❌"
            cat "$temp_file_actual"
        fi
    done
}

# Run tests
run_error_tests tests/Checker/everything/errors
run_error_tests tests/Checker/failure
run_error_tests tests/Checker/math2001
run_error_tests tests/Checker/supplied
run_success_tests tests/Checker/everything/edges
run_success_tests tests/Checker/success

rm "$temp_file_actual"
