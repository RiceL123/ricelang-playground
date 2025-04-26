echo 'setting class path to'
echo $(cd ..; pwd)

export CLASSPATH=$(cd ..; pwd)

export PATH="$PATH:./jasmin/bin"
