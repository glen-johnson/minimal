rm ./gimmeusa.css
cp ./responsive.css ./gimmeusa.css
cat ./bootstrap.css >> ./gimmeusa.css
cat ./custom.css >> ./gimmeusa.css
yui-compressor ./gimmeusa.css > ./gimmeusa.min.css
