echo "Tests for Scanner"
javac vc.java

temp_file_actual="temp_actual.out"

for input_file in tests/Scanner/*.vc; do
    file_expected=${input_file%.vc}.sol
    java VC.vc $input_file > $temp_file_actual

    DIFF=$(diff --color=always $temp_file_actual $file_expected)
    if [ "$DIFF" == "" ]
    then
        echo "$input_file: passed ✅"
    else
        echo "$input_file: failed ❌ $file_expected != $temp_file_actual"

        echo '====================== EXPECTED ======================'
        cat $file_expected


        echo '====================== RECEIVED ======================'
        cat $temp_file_actual

        echo '====================== DIFF ======================'
        echo $DIFF
        echo ''
    fi
done

rm $temp_file_actual
