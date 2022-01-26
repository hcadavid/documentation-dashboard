
//new gridjs.Grid({
//  columns: ['Date', 'Doc name'],
//  server: {
//    url: 'https://documentation-dashboard.herokuapp.com/outputs/11111',
//    then: data => data.results.map(movie => 
//      [movie.title, movie.director, movie.producer]
//    )
//  }  
//}).render(document.getElementById("wrapper"));
urlp = new URLSearchParams(window.location.search);
pipid = urlp.get("pipelineid");


new gridjs.Grid({
  columns: ['Pokemon', 'URL'],
  server: {
    url: 'https://documentation-dashboard.herokuapp.com/outputs/11111',
    then: data => data.results.map(pokemon => [
      pokemon.date, pokemon.docName
    ])
  } 
}).render(document.getElementById("wrapper"));