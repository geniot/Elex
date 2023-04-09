#!/bin/bash
arr=( "LingvoUniversal" "Transport" )
for dictionaryName in "${arr[@]}"
do
   java -cp /var/lib/jenkins/workspace/Elex-test/target/lib-elex-jar-with-dependencies.jar io.github.geniot.elex.tools.Rep2Ezp ru/$dictionaryName /tmp/en_ru-$dictionaryName.ezp
done

sudo systemctl stop elex.service
for dictionaryName in "${arr[@]}"
do
   sudo rm /opt/elex/data/ft-index/en_ru-$dictionaryName -rf
   sudo rm /opt/elex/data/en_ru-$dictionaryName.ezp -rf
   sudo cp /tmp/en_ru-$dictionaryName.ezp /opt/elex/data/
   sudo chown vitaly:vitaly /opt/elex/data/en_ru-$dictionaryName.ezp
done
sudo systemctl start elex.service

ssh -t vitaly@quantum sudo /bin/systemctl stop elex
for dictionaryName in "${arr[@]}"
do
   ssh -t vitaly@quantum sudo rm /opt/elex/data/ft-index/en_ru-$dictionaryName -rf
   ssh -t vitaly@quantum sudo rm /opt/elex/data/en_ru-$dictionaryName.ezp -rf
   scp /tmp/en_ru-$dictionaryName.ezp vitaly@quantum:/opt/elex/data/
   ssh -t vitaly@quantum sudo chown vitaly:vitaly /opt/elex/data/en_ru-$dictionaryName.ezp
done
ssh -t vitaly@quantum sudo /bin/systemctl start elex