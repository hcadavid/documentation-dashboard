$(document).ready(function() {

    urlp = new URLSearchParams(window.location.search);
    pipid = urlp.get("pipelineid");
    docid = urlp.get("docid");
    content = "";

    $.ajax({
        url: "https://documentation-dashboard.herokuapp.com/pipelines/"+pipid
    }).then(function(data) {
        content = data;

        for (let i = 0; i < content.results.length; i++) {
            $('#tbody').append(`
            <tr>
                <th scope="row">1</th>
                <td>${content.results[i].docName}</td>
                <td>${content.results[i].errors[0]}</td>
            </tr>`);

        }
        
    });
});
