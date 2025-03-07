test_file=$1
temp_file="temp.out"

echo "diffing java VC.vc $test_file.vc with $test_file"
javac vc.java && java VC.vc Recogniser/$test_file.vc > $temp_file
echo '====================== RECEIVED ======================'
cat $temp_file

echo '====================== EXPECTED ======================'
cat Recogniser/$test_file.sol

echo '====================== DIFF ======================'
diff $temp_file Recogniser/$test_file.sol
rm $temp_file
