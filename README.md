# Privacy-Aware communication for smartphones using vibration.

This project was developed as a part of the course CSE 570 - Wireless and Mobile Networks in the department of Computer Science at Stony Brook University.

In this project, we used Android Studio to develop an Android application for smartphones which can communicate using vibration as the means of communication. One smartphone (as a transmitter) accepts an input from a user and transmits the input as vibrations, which are transmitted through the surface on which the smartphone is placed. One or multiple other smartphones (as receivers) can sense these vibrations and decode the data transmitted. This project was implemented based on the idea of an IEEE paper which can be accessed on the right. The goal of this project was to get hands-on experience in developing an application that achieves communication in wireless means.

## Getting Started
* Make sure you have Android Studio in your system.
* Clone the repository and build it.
* Generate an APK and install it on your Android smartphone and start communicating without radio.

## Things to remember.
* The performance of communication depends a lot on the surface on which the transmitting and receiving smartphones are kept. A highly rigid or a highly soft surface are bad transmitters of vibration.
* The distance between the transmitter and receiver also vary the performance. The optimal distance that worked out for us was 15 - 30 cm on an empty laptop cardboard box.
* Depending on the accelerometer sensitivity, the threshold for detecting a binary 1 might have to be changed in the program.
