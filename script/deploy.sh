program=home-monitor
path=home-monitor
local_source_path=/Users/martinschijf/Prive/Sources/Kotlin/

cd $local_source_path/$path
# mvn clean package
ssh pi rm $path/$program*.jar
scp target/$program*.jar pi:~/$path
ssh pi bash $path/script/start.sh

