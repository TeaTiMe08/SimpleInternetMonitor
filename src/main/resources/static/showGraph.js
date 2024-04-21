window.onload = function() {
  loadGraph();
};


let timestamps = [];
let timeouts = [];
let latencies = [];
let maxLatency = 3000; // Set your maximum latency value
let myChart = undefined;

function loadGraph() {
    document.getElementById('datePicker').valueAsDate = new Date();
    // Load the CSV data
    fetch('http://localhost:8047/datafile')
    .then(response => response.text())
    .then(data => {
        const lines = data.split('\n');

        // Parse CSV data
        for (const line of lines) {
            const [timestamp, successful, latency] = line.split(',');
            timestamps.push(timestamp);
            timeouts.push(successful == 'y');
            latencies.push(Number(latency) <= 0 || successful != 'y' ? maxLatency : parseInt(latency));
        }

        // Create initial chart
        showSelectedDay();
    });
}

function showSelectedDay() {
    const selectedDate = new Date(document.getElementById('datePicker').value).toDateString();
    const selectedTimestamps = [];
    const selectedLatencies = [];

    for (let i = 0; i < timestamps.length; i++) {
        if (new Date(timestamps[i]).toDateString() === selectedDate) {
            selectedTimestamps.push(timestamps[i].split('T')[1]);
            selectedLatencies.push(latencies[i]);
        }
    }


    const medianTs = median(selectedLatencies);
    maxLatency = 5 * medianTs;

    for(let i = 0; i < selectedLatencies.length; i++) {
        selectedLatencies[i] = selectedLatencies[i] > maxLatency ? 0.99 * maxLatency : selectedLatencies[i];
    }

    createChart(selectedTimestamps, selectedLatencies);
}

function median(numbers) {
    const sorted = Array.from(numbers).sort((a, b) => a - b);
    const middle = Math.floor(sorted.length / 2);
    if (sorted.length % 2 === 0) {
        return (sorted[middle - 1] + sorted[middle]) / 2;
    }
    return sorted[middle];
}

function createChart(labels, data) {
    const ctx = document.getElementById('latencyChart').getContext('2d');
    if (myChart) {
        myChart.clear();
        myChart.destroy();
    }
    myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Latency (ms) - only red bars are timeouts',
                data: data,
                backgroundColor: (context) => {
                    if (context.raw === maxLatency) {
                        return 'rgba(255, 0, 0, 1)';
                    } else {
                        return 'rgba(75, 192, 192, 0.2)';
                    }
                },
                borderColor: (context) => {
                    if (context.raw === maxLatency) {
                        return 'rgba(255, 0, 0, 1)';
                    } else {
                        return 'rgba(75, 192, 192, 1)';
                    }
                },
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    title: {
                        display: true,
                        text: 'Timestamp'
                    }
                },
                y: {
                    title: {
                        display: true,
                        text: 'Latency (ms)'
                    },
                    beginAtZero: true,
                    suggestedMax: maxLatency
                }
            }
        }
    });
}