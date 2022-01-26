$(document).ready(function() {

    $.ajax({
        url: "https://documentation-dashboard.herokuapp.com/pipelines"
    }).then(function(data) {
        content = data;

        for (let i = 0; i < content.length; i++) {
            $('#tbody').append(`
            <tr>
                <th scope="row">1</th>
                <td>${content[i]}</td>
                <td><a href="pipelinedetails.html?pipelineid=${content[i]}">Error log</a></td>
            </tr>`);

        }
        
    });
});
