#!/bin/bash

repArr=("ru/LingvoUniversal" "ru/Transport" "en/CollinsCobuild")
arr=( "en_ru-LingvoUniversal" "en_ru-Transport" "en_en-CollinsCobuild" )

echo "Compiling dictionaries from sources."
for i in "${!arr[@]}"; do
  java -cp /var/lib/jenkins/workspace/Elex-test/target/lib-elex-jar-with-dependencies.jar io.github.geniot.elex.tools.Rep2Ezp "${repArr[$i]}" "/tmp/${arr[$i]}.ezp"
done

echo "Installing dictionaries to the TEST environment."
sudo systemctl stop elex.service
for dictionaryName in "${arr[@]}"
do
   sudo rm /opt/elex/data/ft-index/$dictionaryName -rf
   sudo rm /opt/elex/data/$dictionaryName.ezp -rf
   echo "Installing $dictionaryName.ezp"
   sudo cp /tmp/$dictionaryName.ezp /opt/elex/data/
   sudo chown vitaly:vitaly /opt/elex/data/$dictionaryName.ezp
done
sudo systemctl start elex.service

echo "Installing dictionaries to the PROD environment."
ssh -t vitaly@quantum sudo /bin/systemctl stop elex
for dictionaryName in "${arr[@]}"
do
   ssh -t vitaly@quantum sudo rm /opt/elex/data/ft-index/$dictionaryName -rf
   ssh -t vitaly@quantum sudo rm /opt/elex/data/$dictionaryName.ezp -rf
   echo "Installing $dictionaryName.ezp"
   scp /tmp/$dictionaryName.ezp vitaly@quantum:/opt/elex/data/
   ssh -t vitaly@quantum sudo chown vitaly:vitaly /opt/elex/data/$dictionaryName.ezp
done
ssh -t vitaly@quantum sudo /bin/systemctl start elex