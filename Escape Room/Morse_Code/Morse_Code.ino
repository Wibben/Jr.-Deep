//Pins used on Arduino
int keyPins[] = {2, 3, 4, 5, 6, 7, 8, 9}; //piano key pins
int speakerPin = 10; //pins
int numPins = 8; //number of pins to make array iteration easier

//Notes/Tones constants
//char notes[] = "cdefgabC "; //notes available on the piano
int MorseUnit = 200; // Number of milliseconds for one unit
int tones[] = { 1915, 1700, 1519, 1432, 1275, 1136, 1014, 956 }; //frequencies that correspond with the notes
char *alphabet[26] = { // Holds the morse code for the alphabet (A-Z)
  "", "", "", "", ".", 
  "", "", "", "", "", 
  "", "", "", "", "", 
  "", "", "", "", "", 
  "", "", "", "", "", ""
};
char *numeral[10] = { // Holds the morse code for the numberals (0-9)
  "-----", ".----", "..---",
  "...--", "....-", ".....",
  "-....", "--...", "---..", "----."
};

// Messages to play
String message[5] = {
  "E",
  "E",
  "E",
  "E",
  "E"
};

//Serial Output
bool serialOn = true;

// Plays a tone
void playTone(int tone, int duration) 
{ 
  for (long i = 0; i < duration * 1000L; i += tone * 2) {
    digitalWrite(speakerPin, HIGH);
    delayMicroseconds(tone);
    digitalWrite(speakerPin, LOW);
    delayMicroseconds(tone);
  }
}

// Plays the morse for a character
void playMorse(char character)
{
  Serial.println(character);
  // Figure out if it's a letter or number and play from the right array
  String morse;
  if(character>='A' && character<='Z') {
    morse = alphabet[character-'A'];
  } else morse = numeral[character-'0'];

  for(int i=0; i<morse.length(); i++) {
    if(morse[i]=='.') playTone(tones[3],MorseUnit);
    else playTone(tones[4],3*MorseUnit);
    delay(MorseUnit); // Wait a unit before playing next tone
  }
}

// Plays a selected message
void playMessage(int messageID) 
{
  // Timings for morse code
  // Dots = 1 unit
  // Dashes = 3 units
  // Within letters = 1 unit
  // Between letters = 3 units
  // Between words = 7 units
  for(int i=0; i<message[messageID].length(); i++) {
    // If it's a space, wait 7 units, otherwise
    // Play the morse code of the corresponding letter/number and then wait 3 units
    if(message[messageID][i]==' ') delay(4*MorseUnit); // Only 4 units since there is already 3 units from the previous letter
    else {
      playMorse(message[messageID][i]);
      delay(2*MorseUnit); // Only 2 units since there is already 1 unit from the end of the morse tone itself
    }
  }
}

void disp(String msg) 
{
  if (serialOn) Serial.println(msg);
}

void setup() 
{
  //initialize pins
  for (int i = 0 ; i < numPins ; i++)
    pinMode(keyPins[i], INPUT);
  pinMode(speakerPin, OUTPUT);

  if (serialOn) Serial.begin(9600);
}

void loop() 
{
  //Play recorded message based on button pressed
  for (int i = 0 ; i < 5; i++) {
    if(digitalRead(keyPins[i])==LOW){
    //if (readBtn(keyPins[i]) == HIGH) { //if the current pin button is being pressed
      Serial.println(keyPins[i]);
      playMessage(i);
    }
  }
}

