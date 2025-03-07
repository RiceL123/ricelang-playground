echo "Tests for Recogniser"
javac vc.java

temp_file_actual="temp_actual.out"
temp_file_expected="temp_expected.out"

for input_file in tests/Recogniser/*.vc; do
    cat ${input_file%.vc}.sol | tail -1 > $temp_file_expected
    java VC.vc $input_file | tail -1 > $temp_file_actual

    DIFF=$(diff --color=always $temp_file_actual $temp_file_expected)
    if [ "$DIFF" == "" ]
    then
        echo "$input_file: passed ✅"
    else
        echo "$input_file: failed ❌ $temp_file_expected != $temp_file_actual"

        echo '====================== EXPECTED ======================'
        cat $temp_file_expected


        echo '====================== RECEIVED ======================'
        cat $temp_file_actual

        echo '====================== DIFF ======================'
        echo $DIFF
        echo ''
    fi
done

rm $temp_file_actual
rm $temp_file_expected