// ++++++++++++++++++++ GENERAL +++++++++++++++++++++++++++++
#define PORT 666
#define DELAY 1000
#define LOGGING

// +++++++++++++++ PIN ALLOCATIONS ++++++++++++++++++++++++++
#define PIXELS_PIN 8
// Data wire is plugged into pin 3 on the Arduino
#define ONE_WIRE_BUS 7
// How many NeoPixels are attached to the Arduino?
#define NUMPIXELS 12 // LEDs

// +++++++++++++ AMBILIGHT PARAMETERS +++++++++++++++++++++++
#define MAX_COLORS 5
#define FADE_STEPS 20
#define FADE_DELAY 1 //approx. seconds due to delay()
#define TRESH_HIGH_DEFAULT 22
#define TRESH_LOW_DEFAULT 20
#define MAX_LUX 4000
