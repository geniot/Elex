#!/bin/bash

repArr=("ru/LingvoUniversal" "ru/Transport" "en/CollinsCobuild")
arr=( "en_ru-LingvoUniversal" "en_ru-Transport" "en_en-CollinsCobuild" )

for i in "${!arr[@]}"; do
  java -cp /var/lib/jenkins/workspace/Elex-test/target/lib-elex-jar-with-dependencies.jar io.github.geniot.elex.tools.Rep2Ezp "${repArr[$i]}" "/tmp/${arr[$i]}.ezp"
done

for dictionaryName in "${arr[@]}"
do
   java -cp /var/lib/jenkins/workspace/Elex-test/target/lib-elex-jar-with-dependencies.jar io.github.geniot.elex.tools.Rep2Ezp ru/$dictionaryName /tmp/$dictionaryName.ezp
done

sudo systemctl stop elex.service
for dictionaryName in "${arr[@]}"
do
   sudo rm /opt/elex/data/ft-index/$dictionaryName -rf
   sudo rm /opt/elex/data/$dictionaryName.ezp -rf
   sudo cp /tmp/$dictionaryName.ezp /opt/elex/data/
   sudo chown vitaly:vitaly /opt/elex/data/$dictionaryName.ezp
done
sudo systemctl start elex.service

ssh -t vitaly@quantum sudo /bin/systemctl stop elex
for dictionaryName in "${arr[@]}"
do
   ssh -t vitaly@quantum sudo rm /opt/elex/data/ft-index/$dictionaryName -rf
   ssh -t vitaly@quantum sudo rm /opt/elex/data/$dictionaryName.ezp -rf
   scp /tmp/$dictionaryName.ezp vitaly@quantum:/opt/elex/data/
   ssh -t vitaly@quantum sudo chown vitaly:vitaly /opt/elex/data/$dictionaryName.ezp
done
ssh -t vitaly@quantum sudo /bin/systemctl start elex