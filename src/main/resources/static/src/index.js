
//new gridjs.Grid({
//  columns: ['Date', 'Doc name'],
//  server: {
//    url: 'https://documentation-dashboard.herokuapp.com/outputs/11111',
//    then: data => data.results.map(movie => 
//      [movie.title, movie.director, movie.producer]
//    )
//  }  
//}).render(document.getElementById("wrapper"));


new gridjs.Grid({
  columns: ['Pokemon', 'URL'],
  server: {
    url: 'https://pokeapi.co/api/v2/pokemon',
    then: data => data.results.map(pokemon => [
      pokemon.name, pokemon.url
    ])
  } 
}).render(document.getElementById("wrapper"));