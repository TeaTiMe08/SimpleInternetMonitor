window.onload = function() {
  loadGraph();
};


let timestamps = [];
let timeouts = [];
let latencies = [];
let maxLatency = 3000; // Set your maximum latency value
const maxLatencyMultiplier = 4;
let myChart = undefined;

let dayMedL = 0;
let dayMinL = 0;
let dayUpT = 0;

let weekMedL = 0;
let weekMinL = 0;
let weekUpT = 0;

let totalMedL = 0;
let totalMinL = 0;
let totalUpT = 0;

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
            timeouts.push(successful === 'n');
            latencies.push(Number(latency) <= 0 || successful != 'y' ? maxLatency : parseInt(latency));
        }

        // Create initial chart
        showSelectedDay();

        // Total Statistics
        totalMedL = median(latencies);
        totalMinL = Math.min(...latencies);
        const totalNumTimeouts = timeouts.filter((wasTimeout) => wasTimeout).length;
        if (totalNumTimeouts == 0) {
            totalUpT = 100;
        } else {
            totalUpT = (timeouts.length - totalNumTimeouts) / timeouts.length;
            totalUpT = roundTo(totalUpT * 100, 2);
        }

        setHeaderStatistics();
    });
}

function showSelectedDay() {
    //const selectedDate = new Date(document.getElementById('datePicker').value).toDateString();
    const selectedDate = new Date(document.getElementById('datePicker').value);
    const selectedTimestamps = [];
    const selectedLatencies = [];
    const selectedDateTimeouts = [];

    const lastWeekDayStart = new Date(selectedDate.getTime());
    lastWeekDayStart.setDate(lastWeekDayStart.getDate() - 7);
    const lastWeekLatencies = [];
    const lastWeekTimeouts = [];

    for (let i = 0; i < timestamps.length; i++) {
        //if (new Date(timestamps[i]).toDateString() === selectedDate) {
        const timestapDate = new Date(timestamps[i]);
        if (timestapDate.toDateString() === selectedDate.toDateString()) {
            selectedTimestamps.push(timestamps[i].split('T')[1]);
            selectedLatencies.push(latencies[i]);
            selectedDateTimeouts.push(timeouts[i]);
        }
        if (timestapDate.toDateString() === selectedDate.toDateString() || (timestapDate <= selectedDate && timestapDate >= lastWeekDayStart)) {
            lastWeekLatencies.push(latencies[i]);
            lastWeekTimeouts.push(timeouts[i]);
        }
    }

    // weekly statistics
    weekMedL = median(lastWeekLatencies);
    weekMinL = Math.min(...lastWeekLatencies);
    const weekNumTimeouts = lastWeekTimeouts.filter((wasTimeout) => wasTimeout).length;
    if (weekNumTimeouts == 0) {
        weekupT = 100;
    } else {
        weekUpT = (lastWeekTimeouts.length - weekNumTimeouts) / lastWeekTimeouts.length;
        weekUpT = roundTo(weekUpT * 100), 2;
    }

    // daily statistics
    const medianTs = median(selectedLatencies);
    dayMedL = medianTs;
    dayMinL = Math.min(...selectedLatencies);
    const dayNumTimeouts = selectedDateTimeouts.filter((wasTimeout) => wasTimeout).length;
    if (dayNumTimeouts == 0) {
        dayUpT = 100;
    } else {
        dayUpT = (selectedDateTimeouts.length - dayNumTimeouts) / selectedDateTimeouts.length;
        dayUpT = roundTo(dayUpT * 100, 2);
    }

    // adjust values fow showing the graph
    // timeouts should be red. Must be over the maxLatency
    maxLatency = maxLatencyMultiplier * medianTs;
    for(let i = 0; i < selectedLatencies.length; i++) {
        if (selectedDateTimeouts[i]) {
            selectedLatencies[i] = maxLatency;
        } else {
            selectedLatencies[i] = selectedLatencies[i] > maxLatency ? 0.99 * maxLatency : selectedLatencies[i];
        }
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

function roundTo(n, digits) {
    var negative = false;
    if (digits === undefined) {
        digits = 0;
    }
    if (n < 0) {
        negative = true;
        n = n * -1;
    }
    var multiplicator = Math.pow(10, digits);
    n = parseFloat((n * multiplicator).toFixed(11));
    n = (Math.round(n) / multiplicator).toFixed(digits);
    if (negative) {
        n = (n * -1).toFixed(digits);
    }
    return n;
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
                    if (context.raw >= maxLatency) {
                        return 'rgba(255, 0, 0, 1)';
                    } else {
                        return 'rgba(75, 192, 192, 0.2)';
                    }
                },
                borderColor: (context) => {
                    if (context.raw >= maxLatency) {
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
            },
            plugins: {
                legend: {
                    labels: {
                        // This more specific font property overrides the global property
                        font: {
                            size: 14
                        }
                    }
                }
            }
        }
    });
}

function setHeaderStatistics() {
    document.getElementById('DayMedianLatency').textContent = Math.round(dayMedL);
    document.getElementById('DayMinimumLatency').textContent = dayMinL;
    document.getElementById('DayUptime').textContent = dayUpT;

    document.getElementById('WeekMedianLatency').textContent = Math.round(weekMedL);
    document.getElementById('WeekMinimumLatency').textContent = weekMinL;
    document.getElementById('WeekUptime').textContent = weekUpT;

    document.getElementById('TotalMedianLatency').textContent = Math.round(totalMedL);
    document.getElementById('TotalMinimumLatency').textContent = totalMinL;
    document.getElementById('TotalUptime').textContent = totalUpT;
}