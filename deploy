program=home-monitoring
path=home-monitor
# mvn clean package
ssh pi rm $path/$program*.jar
scp target/$program*.jar pi:~/$path
ssh pi bash $path/script/start.sh

