cd $(dirname $0)
git submodule update --remote  
cd react-api-client-poc
npm run build
cd ..

