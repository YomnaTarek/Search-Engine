//TODO://///////////The commented code will be alterd when the html file is done.

function runSpeechRecognition() {
    // getting search box id.
    var searchBox = document.getElementById("searchBox"); 
    // get action element reference
    // var action = document.getElementById("action");
    // new speech recognition object
    var SpeechRecognition = SpeechRecognition || webkitSpeechRecognition;
    var recognition = new SpeechRecognition();

    // This runs when the speech recognition service starts.
    recognition.onstart = function() {
        //To inform the user to start speaking:
        // action.innerHTML = "<small>listening, please speak...</small>";

    };

    recognition.onspeechend = function() {
        //To inform the user that listening has stopped.
        // action.innerHTML = "<small>stopped listening, hope you are done...</small>";
        recognition.stop();
    }

    //This is to display the results from the voice recognition inside the search box.
    recognition.onresult = function(event) {
        var transcript = event.results[0][0].transcript;
        var confidence = event.results[0][0].confidence;
        searchBox.value = transcript; 
        // searchBox.innerHTML = "<b>Text:</b> " + transcript + "<br/> <b>Confidence:</b> " + confidence*100+"%";
        // searchBox.classList.remove("hide");
    };

     recognition.start(); //Starting voice recognition.
}