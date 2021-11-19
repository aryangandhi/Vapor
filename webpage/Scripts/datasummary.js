let input = document.getElementById("chooseReport");


// Wait for user to choose a file.
input.addEventListener("change", function () {

    // Ensure the files passed exist.
    if (this.files && this.files[0]) {

        var file = this.files[0];

        var reader = new FileReader();
        reader.onload = function(progressEvent) {

            // Parse the data for the information we require.
            var lines = this.result.split('\n');
            let data = [parseFloat(lines[0]), parseFloat(lines[1]), parseFloat(lines[2]),
                        parseFloat(lines[3]), parseFloat(lines[4]), parseFloat(lines[5])];

            createGraph(data);

        }
    };
    reader.readAsText(file);

});

// Function to create a graph on webpage.
function createGraph(data) {
    Chart.defaults.global.defaultFontColor = 'white';
    var ctx = document.getElementById('myChart').getContext('2d');
    var myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Profit', 'Revenue', 'Refunded', 'DailyProfit', 'DailyRevenue', 'DailyRefunded'],
            datasets: [{
                label: 'Amount in dollars',
                data: data,
                backgroundColor: ['rgb(143, 240, 91, 0.4)','rgb(240, 185, 91, 0.4)','rgb(240, 96, 91, 0.4)',
                                  'rgb(143, 240, 91, 0.4)','rgb(240, 185, 91, 0.4)','rgb(240, 96, 91, 0.4)'],
                borderColor: ['rgb(143, 240, 91)','rgb(240, 185, 91)','rgb(240, 96, 91)',
                              'rgb(143, 240, 91)','rgb(240, 185, 91)','rgb(240, 96, 91)'],
                borderWidth: 1
            }]
        },
        options: {  
                scales: { 
                    yAxes: [{ ticks: { beginAtZero: true } }] }}
    });   
}

