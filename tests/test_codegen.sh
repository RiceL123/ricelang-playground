#!/bin/bash
export CLASSPATH=$(cd ..; pwd):"$PWD/jasmin/classes" &&
javac lang/System.java &&
javac CodeGen/Emitter.java &&
javac CodeGen/JVM.java
javac vc.java

test_folder=tests/CodeGen
actual="temp.out"

echo "========== Normal Tests =========="

for input_file in $test_folder/*.vc; do
    base_file=${input_file%.vc}
    expected=$base_file.sol
    java VC.vc $input_file > /dev/null &&
    java jasmin.Main $base_file.j > /dev/null
    java -cp .:.. $base_file > $actual

    DIFF=$(diff --color=always $actual $expected)
    if [ "$DIFF" == "" ]
    then
        echo "$input_file: passed ✅"
    else
        echo "$input_file: failed ❌ $expected != $actual"

        echo '====================== EXPECTED ======================'
        cat $expected


        echo '====================== RECEIVED ======================'
        cat $actual

        echo '====================== DIFF ======================'
        echo $DIFF
        echo ''
    fi
done

# test stdin
echo "========== Stdin Tests =========="

for input_file in $test_folder/stdin/*.vc; do
    base_file=${input_file%.vc}
    expected="$base_file.sol"
    stdin="$base_file.txt"
    java VC.vc $input_file > /dev/null &&
    java jasmin.Main $base_file.j > /dev/null
    cat $stdin | java -cp .:.. $base_file > $actual

    DIFF=$(diff --color=always $actual $expected)
    if [ "$DIFF" == "" ]
    then
        echo "$input_file: passed ✅"
    else
        echo "$input_file: failed ❌ $expected != $actual"

        echo '====================== EXPECTED ======================'
        cat $expected


        echo '====================== RECEIVED ======================'
        cat $actual

        echo '====================== DIFF ======================'
        echo $DIFF
        echo ''
    fi
done

rm $actual
rm $test_folder/*.j
rm $test_folder/*.class
rm $test_folder/**/*.j
rm $test_folder/**/*.class
