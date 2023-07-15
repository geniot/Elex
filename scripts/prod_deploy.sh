sudo systemctl stop elex
sudo rm -f /opt/elex/elex.jar
sudo cp target/elex-*.jar /opt/elex/elex.jar
sudo systemctl start elex