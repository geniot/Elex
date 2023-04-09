#!/bin/bash
arr=( "en_ru-LingvoUniversal" )
for dictionaryName in "${arr[@]}"
do
   java -Xmx2048m -cp /var/lib/jenkins/workspace/Elex-test/target/lib-elex-jar-with-dependencies.jar io.github.geniot.elex.tools.Rep2Ezr data /tmp/$dictionaryName.ezr
done

sudo systemctl stop elex.service
for dictionaryName in "${arr[@]}"
do
   sudo rm /opt/elex/data/$dictionaryName.ezr -rf
   sudo cp /tmp/$dictionaryName.ezr /opt/elex/data/
   sudo chown vitaly:vitaly /opt/elex/data/$dictionaryName.ezr
done
sudo systemctl start elex.service

ssh -t vitaly@quantum sudo /bin/systemctl stop elex
for dictionaryName in "${arr[@]}"
do
   ssh -t vitaly@quantum sudo rm /opt/elex/data/$dictionaryName.ezr -rf
   scp /tmp/$dictionaryName.ezr vitaly@quantum:/opt/elex/data/
   ssh -t vitaly@quantum sudo chown vitaly:vitaly /opt/elex/data/$dictionaryName.ezr
   sudo rm /tmp/$dictionaryName.ezr -rf
done
ssh -t vitaly@quantum sudo /bin/systemctl start elex