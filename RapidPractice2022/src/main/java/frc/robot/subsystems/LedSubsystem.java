/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LedSubsystem extends SubsystemBase {
  private long currTime;
  private long startTime;
  private long delayMs;
  /**
   * Creates a new LedController.
   */

  private AddressableLED m_led;
  private AddressableLEDBuffer m_ledBuffer;
  private AddressableLEDBuffer m_ledOff;
  private int m_rainbowFirstPixelHue = 180;

  private int port, length;

  private boolean isBlink = false;
  private boolean ledOn;

  public LedSubsystem(int port, int length) {
    this.port = port;
    this.length = length;
    //init method, sets up the led strip and if you want it to be one solid color you would do that here
    //you can still change it later
    m_led = new AddressableLED(port);

    m_ledBuffer = new AddressableLEDBuffer(length);  //standard 300
    m_ledOff = new AddressableLEDBuffer(length);
    m_led.setLength(m_ledBuffer.getLength());

    //setting all of the leds to orange by using a for loop
    for(int i = 0; i < m_ledBuffer.getLength(); i++){
      m_ledOff.setRGB(i,0,0,0);
    }
    //taking the data created above and inserting it into the leds
    m_led.setData(m_ledBuffer);
    m_led.start();
  }

  @Override
  public void periodic() {
    currTime = System.currentTimeMillis();
    // Runs the blueSnake method which changes the m_ledBuffer, then the m_led is set to the data that was just created
    //rainbow();

    //this method is esscentially only for controlling blink
    if(isBlink){
      if(currTime - startTime >= delayMs){
        if(ledOn){
          m_led.setData(m_ledOff);
          ledOn = false;
        }else{
          m_led.setData(m_ledBuffer);
          ledOn = true;
        }
        startTime = currTime;
      }
    }
  }

  public void rainbow() {  //completely not needed but proof of concept if we want?
    // For every pixel
    for (var i = 0; i < m_ledBuffer.getLength(); i++) {
      // Calculate the hue - hue is easier for rainbows because the color
      // shape is a circle so only one value needs to precess
      final var hue = (m_rainbowFirstPixelHue + (i * 180 / m_ledBuffer.getLength())) % 180;
      // Set the value
      m_ledBuffer.setHSV(i, hue, 255, 128);
    }
    // Increase by to make the rainbow "move"
    m_rainbowFirstPixelHue += 3;
    // Check bounds
    m_rainbowFirstPixelHue %= 180;
  }

  public void speed(double power){
    //this method shows that you can use the speed of something to change the leds, and its pretty simple to do
    int amountOn = Math.abs((int) ((double) m_ledBuffer.getLength() * power));
    System.out.println(amountOn);
    int leds = m_ledBuffer.getLength() - 1;
    for(int i = 0; i < amountOn; i++){
      m_ledBuffer.setRGB(i,0,255,0);
    }
    for(int i = leds; i >= amountOn; i--){
      m_ledBuffer.setRGB(i,255,20,0);
    }
    m_led.setData(m_ledBuffer);
  }

  public void setSolidColor(int red, int green, int blue){
    isBlink = false;
    for(int i = 0; i < m_ledBuffer.getLength(); i++){
      m_ledBuffer.setRGB(i,red,green,blue);
    }
    m_led.setData(m_ledBuffer);
  }

  public void setBlink(int red, int green, int blue, long delayMs){
    this.delayMs = delayMs;
    for(int i = 0; i < m_ledBuffer.getLength(); i++){
      m_ledBuffer.setRGB(i,red,green,blue);
    }
    ledOn = true;
    m_led.setData(m_ledBuffer);
    startTime = currTime;
    isBlink = true;
  }
}