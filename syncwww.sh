rm www.zip
zip -r --symlinks www.zip www

# if you changed config.json, cp to /home/gimme/ on all solr boxes


echo "_______________________________________"
echo "copying to SOLR1"
rsync -avz -e "ssh -p 27576" www.zip glen@solr1:workspace/Minimal/

echo "_______________________________________"
echo "copying to SOLR2"
rsync -avz -e "ssh -p 27576" www.zip glen@solr2:workspace/Minimal/

#echo "_______________________________________"
#echo "copying to SOLR3"
#rsync -avz -e "ssh -p 27576" www.zip glen@solr3:workspace/Minimal/
