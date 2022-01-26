$(document).ready(function() {

    $.ajax({
        url: "https://documentation-dashboard.herokuapp.com/outputs/"+pipid
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
