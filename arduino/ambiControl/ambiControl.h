#ifndef AMBI_CONTROL_H
#define AMBI_CONTROL_H
#endif

#define MIN(a,b) (((a)<(b))?(a):(b))
#define MAX(a,b) (((a)>(b))?(a):(b))
#define BRIGHTNESS(a,b) ((((a)/(b))- 255) * (-1))

void iniFader(void);
void controlLED(void);
void adjustColor(byte, byte);
void adjustCooler(byte, byte, int);
void adjustWarmer(byte, byte, int);
void adjustBrightness();
