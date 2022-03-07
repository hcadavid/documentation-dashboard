cd $(dirname $0)
rm src/main/resources/static/* -R
cp react-api-client-poc/build/* src/main/resources/static -R
