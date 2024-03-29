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
  //"HELLO GROUP ONE, YOU WILL NEED THE FOLLOWING INFORMATION FOR A LATER PUZZLE, SO PAY ATTENTION, ROW 2 COL 1, ROW 4 COL 3, ROW 6 COL 5, ROW 7 COL 5, ROW 8 COL 9",
  //"2 1 4 3 6 5 7 5 8 9", 
  "ROWS 2 4 6 7 8 COLUMNS 1 3 5 5 9",
  //"HI GROUP TWO, YOUR MESSAGE IS VERY IMPORTANT AND YOU NEED IT TO SOLVE ONE OF THE LAST PUZZLES, ROW 1 COL 8, ROW 2 COL 3, ROW 5 COL 9, ROW 7 COL 6, ROW 8 COL 4",
  //"1 8 2 3 5 9 7 6 8 4",
  "ROWS 1 2 5 7 8 COLUMNS 8 3 9 6 4",
  //"WELCOME GROUP THREE, TO HELP YOU REACH THE END, WE HAVE FOUND SOME INFORMATION FOR YOU, ROW 1 COL 3, ROW 1 COL 6, ROW 4 COL 1, ROW 4 COL 7, ROW 5 COL 8",
  //"1 3 1 6 4 1 4 7 5 8",
  "ROWS 1 1 4 4 5 COLUMNS 3 6 1 7 8",
  //"HOWDY GROUP FOUR, TO AID YOU IN YOUR QUEST, YOU WILL NEED TO TAKE NOTE OF WHAT YOU ARE ABOUT TO READ, ROW 3 COL 7, ROW 5 COL 4, ROW 6 COL 5, ROW 7 COL 6, ROW 8 COL 7",
  //"3 7 5 4 6 5 7 6 8 7",
  "ROWS 3 5 6 7 8 COLUMNS 7 4 5 6 7",
  //"HEY THERE GROUP FIVE, IN ORDER FOR YOU TO FINISH ALL THE PUZZLES, YOU WILL NEED TO USE THIS INFORMATION, ROW 1 COL 2, ROW 2 COL 4, ROW 4 COL 5, ROW 6 COL 6, ROW 8 COL 8"
  //"1 2 2 4 4 5 6 6 8 8",
  "ROWS 1 2 4 6 8 COLUMNS 2 4 5 6 8"
};

// Passowrd: 20 -> 010100
int password[6] = {0,1,0,1,0,0};
int inputPassword[6];

//Serial Output
bool serialOn = true;

// Take in current mode of operation from java
int mode;

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
  // Do othing if the character is not a letter or a number
  String morse = "";
  if(character>='A' && character<='Z') {
    morse = alphabet[character-'A'];
  } else if(character>='0' && character<='9') {
    morse = numeral[character-'0'];
  }

  for(int i=0; i<morse.length(); i++) {
    Serial.println(morse[i]);
    if(morse[i]=='.') playTone(tones[4],MorseUnit);
    else playTone(tones[5],3*MorseUnit);
    //Serial.println(" "); // Print space so dots will flash
    delay(MorseUnit); // Wait a unit before playing next tone
  }
  Serial.println(" "); // Print space to reset current Morse character on GUI
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
  mode = 0;
}

void loop() 
{
  // if serial port is available, read incoming bytes
  if (Serial.available() > 0) {
    int temp = Serial.read();
    if(temp==109 || temp==112) mode = temp; // 109 = 'm' = morse, 112 = p = password
    else if(temp == 110) mode = 0; // 110 = 'n' = none
  }

  // Run based on current mode of operation
  if(mode == 109) { // Morse code circuit, 'm'
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
  } else if (mode == 112){ // Password circuit, 'p'
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
}

