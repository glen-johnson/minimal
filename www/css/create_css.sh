rm ./amazonalt.css
cp ./responsive.css ./amazonalt.css
cat ./bootstrap.css >> ./amazonalt.css
cat ./custom.css >> ./amazonalt.css
yui-compressor ./amazonalt.css > ./amazonalt.min.css
