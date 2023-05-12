ssh -t vitaly@quantum sudo /bin/systemctl stop elex
ssh -t vitaly@quantum rm -f /opt/elex/elex.jar
scp target/elex-*.jar vitaly@quantum:/opt/elex/elex.jar
ssh -t vitaly@quantum sudo /bin/systemctl start elex