//Pins used on Arduino
int keyPins[] = {2, 3, 4, 5, 6, 7, 8, 9}; //piano key pins
int recordPin = 12, playPin = 11, speakerPin = 10; //pins
int numPins = 8; //number of pins to make array iteration easier

//Notes/Tones constants
//char notes[] = "cdefgabC "; //notes available on the piano
int tones[] = { 1915, 1700, 1519, 1432, 1275, 1136, 1014, 956 }; //frequencies that correspond with the notes

//Debounce Variables
int prevStates[] = {LOW, LOW, LOW, LOW, LOW, LOW, LOW, LOW, LOW, LOW, LOW, LOW, LOW, LOW, LOW}; //stores the previous state of the pins
long lastDebounceTime[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; //stores the time a particular button was pressed
int debounceDelay = 50; //debounce time, make it higher if flickers, lower if not sensitive enough

//controls playback/playing of notes
bool recording = false; //controls it he arduino is recoridng or not
bool playing = false; //controls if the song is playing
long startTime = 0; //subtracted from the all the recorded times to prevent overflow
long recordedTones[100], recordedTimes[100]; //stores the tones and notes and LED
int noteCounter = 0; //counts the number of notes played

//Serial Output
bool serialOn = true;

void playTone(int tone, int duration) { //plays a tone
  for (long i = 0; i < duration * 1000L; i += tone * 2) {
    digitalWrite(speakerPin, HIGH);
    delayMicroseconds(tone);
    digitalWrite(speakerPin, LOW);
    delayMicroseconds(tone);
  }
}

void disp(String msg) {
  if (serialOn) Serial.println(msg);
}

int readBtn(int pin) { //reads button with debounce
  int val = digitalRead(pin); //reads input from pin
//  Serial.print(val);
//  Serial.print(" ");
//  Serial.println(prevStates[pin]);
  //delay(50);
  if (val != prevStates[pin] && (millis() - lastDebounceTime[pin]) > debounceDelay) {
    lastDebounceTime[pin] = millis(); //stores the last time the button was pressed.
    if(pin==recordPin || pin==playPin) prevStates[pin] = val; //if the button was held long enough, return high
    return !prevStates[pin];
  } else return LOW;
} 

void setup() {
  //initialize pins
  for (int i = 0 ; i < numPins ; i++)
    pinMode(keyPins[i], INPUT);
  pinMode(recordPin, INPUT);
  pinMode(playPin, INPUT);
  pinMode(speakerPin, OUTPUT);

  if (serialOn) Serial.begin(9600);
}

void loop() {
  //Initialize recording sequence
  if(readBtn(recordPin) == HIGH){
    recording = !recording; //turn off if on, on if off
    disp("Record button pressed");
    
    if (recording) { //if it just started recording, reset variables
      startTime = millis();
      // Need this extra empty note in the beginning to run timing correctly
      recordedTones[0] = 0;
      recordedTimes[0] = 0;
      noteCounter = 1;
      disp("Started recording");
    }
    else { //finish recording
      recordedTimes[noteCounter] = millis(); //add final time get duration of pause at the end
      disp("Finished recording");
    }
  }

  //Do playing sequence
  else if(readBtn(playPin) == HIGH){
    // Set LED pins (keypins) to output
    for (int i = 0 ; i < numPins ; i++)
      pinMode(keyPins[i], OUTPUT);
    
    disp("Play button pressed. Started Playing.");
    for (int i = 1 ; i < noteCounter ; i++){ //go through all the notes
      // Debug
      Serial.print(recordedTones[i]);
      Serial.print(" ");
      Serial.println(recordedTimes[i]-recordedTimes[i-1]);
      
      if (recordedTones[i] != 0) { //play the note or pause
        // Find the right array reference
        for(int j=0; j<8; j++) {
          // Play the note only if it's the right tone
          if(recordedTones[i]==tones[j]) {
            digitalWrite(keyPins[j],HIGH); // Turn on LED
            playTone(recordedTones[i], recordedTimes[i]-recordedTimes[i-1]); // Play note
            digitalWrite(keyPins[j],LOW); // Turn off LED
          }
        }
      } else {
        delay(recordedTimes[i]-recordedTimes[i-1]);
      }
    }
    disp("Finished Playing");

    // Set LED pins (keypins) to back to input
    for (int i = 0 ; i < numPins ; i++)
      pinMode(keyPins[i], INPUT);
  }

  //Otherwise play normal note
  else {
    bool keyPressed = false; //if a key wasn't pressed, need to record pause
    for (int i = 0 ; i < 8 ; i++) {
      if (readBtn(keyPins[i]) == HIGH) { //if the current pin button is being pressed
        //disp("Key Pressed");
        keyPressed = true; 
        if (recording){ //vector add note and add time to the vector
          recordedTones[++noteCounter] = tones[i];
          recordedTimes[noteCounter++] = (millis()-startTime);
          disp("Recorded Note");
        }
        do { //play note while the button is held down
          //disp("Key Pressed");
          playTone(tones[i], 50);
          if (recording){ //vector add note and add time to the vector
            recordedTimes[noteCounter-1] = (millis()-startTime);
            //disp("Recorded Note");
          }
        } while (readBtn(keyPins[i]) == HIGH);
      }
    }

    if (!keyPressed && recording){ //records pauses
      recordedTones[noteCounter] = 0; //adds pause
      recordedTimes[noteCounter] = (millis()-startTime);
      disp("Recorded Pause");
    }
  }
}

