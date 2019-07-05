//Pins used on Arduino
int keyPins[] = {2, 3, 4, 5, 6}; // Morse message pins
int passwordPins[] = {A0, A1, A2, A3, A4, A5}; // Pins for password input
int speakerPin = 10; //pins
int enterPin = 7,redPin = 12, greenPin = 13; // LED pins
int numPins = 5; //number of pins to make array iteration easier

//Notes/Tones constants
int MorseUnit = 300; // Number of milliseconds for one unit
int tones[] = { 1915, 1700, 1519, 1432, 1275, 1136, 1014, 956 }; // frequencies that correspond with the notes cdefgabC
char *alphabet[26] = { // Holds the morse code for the alphabet (A-Z)
  ".-",   "-...", "-.-.", "-..",  ".",            // ABCDE
  "..-.", "--.",  "....", "..",   ".---",         // FGHIJ
  "-.-",  ".-..", "--",   "-.",   "---",          // KLMNO
  ".--.", "--.-", ".-.",  "...",  "-",            // PQRST
  "..-",  "...-", ".--",  "-..-", "-.--", "--.."  // UVWXYZ
};
char *numeral[10] = { // Holds the morse code for the numberals (0-9)
  "-----", ".----", "..---",                    // 012
  "...--", "....-", ".....",                    // 345
  "-....", "--...", "---..", "----."            // 6789
};

// Messages to play
String message[5] = {
  "HELLO",
  "THERE",
  "MY NAME",
  "IS BING",
  "DONE"
};

// Passowrd: 24 -> 011000
int password[6] = {0,1,1,0,0,0};
int inputPassword[6];

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
    if(message[messageID][i]==' ') delay(5*MorseUnit); // Only 4 units since there is already 3 units from the previous letter
    else {
      playMorse(message[messageID][i]);
      delay(3*MorseUnit); // Only 2 units since there is already 1 unit from the end of the morse tone itself
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
  for(int i=0; i<6; i++)
    pinMode(passwordPins[i], INPUT);
  pinMode(speakerPin, OUTPUT);
  pinMode(enterPin, INPUT);
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  
  if (serialOn) Serial.begin(9600);
}

void loop() 
{
  // Morse code circuit
  // Play recorded message based on button pressed
  for (int i = 0 ; i < numPins; i++) {
    if(digitalRead(keyPins[i]) == LOW){ //if the current pin button is being pressed
      Serial.print("GROUP");
      Serial.println(i+1);
      playMessage(i);
      
      // End of message tone
      delay(5*MorseUnit);
      playTone(tones[0],2000);
    }
  }

  // Password circuit
  if(digitalRead(enterPin) == LOW) {
    // Read password
    for(int i=0; i<6; i++) {
      inputPassword[i] = digitalRead(passwordPins[i]);
    }

    // Check password
    bool correct = true;
    for(int i=0; i<6; i++) {
      if(inputPassword[i]!=password[i]) correct = false;
    }

    // Display correct output for 1 second, and ten turn it off
    if(correct) {
      digitalWrite(greenPin, HIGH);
      Serial.println("y");
    } else {
      digitalWrite(redPin, HIGH);
      Serial.println("n");
    }
    
    delay(1000);
    
    digitalWrite(redPin, LOW);
    digitalWrite(greenPin, LOW);
  }
}

