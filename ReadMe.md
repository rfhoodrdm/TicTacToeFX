# ReadMe for Developers
## About this Project
**TicTacToeFX** by **Robert Hood** and **James Baio**

github project repo:  http://www.github.com/rfhoodrdm/TicTacToeFX


## Intro

Does the world _need_ another computer Tic-Tac-Toe game? Probably not, but this incarnation serves two purposes: First, to participate in such a time-honored tradition of creating simple, already-existing games and adding my own personal flourishes on top. Second, to follow some recently given advice to "just code." For the sake of providing coding samples, many smaller, viable projects are preferable to one epic, yet too often, half finished project. Hopefully, this will serve as the first of many such projects that will form a library of games.

## Project Overview

### Dependencies
The project was built in IntelliJ IDE using **OpenJDK13** as the jdk, available at https://jdk.java.net/

Since **JavaFX** is a separate library in Java 13, this will have to be added explicitly as a dependency. You will need a version for each of Windows, Linux and Mac for which you desire to build the game. As of this writing, these can be downloaded from the site of the company maintaining it:
https://gluonhq.com/products/javafx/

Also, **JUnit** was leveraged to test the game's more complex bits of logic. IntelliJ was kind enough to organize this dependency for me. The version recommended at the time was 5.3.

### Build Proceedure
This process was designed to work with IntelliJ. You may have to adapt it somewhat if you use another IDE, but I have tried to keep things to a minimum of complexity in any case.

1) Build the project, thereby compiling the source code into class files.
2) Package the compiled code along with the resources folder into a jar file, complete with the manifest provided. In IntelliJ, this is done via the Build Artifacts feature.
3) Run the build script: build_apps.bat. This will convert the jar into a module, and link the various modules into a stand-alone image (directory for distribution) for each architecture targeted. The images will be found in out\artifacts\TicTacToe_jar\build.
**NOTE:** It is assumed that you have downloaded at least the JDK and JavaFX jmods for each architecture. Set the paths for your local machine at the appropriate places in the build script, where environment variables are set up. If you do not wish to do this, you can instead comment out the lines that build and link modules by prepending @REM.


### Package Setup
The source files for the game's user interface, game logic, and model objects were grouped in the gui, logic, and state packages respectively. The logic package also contains a number of exceptions that indicate any of a handful of issues that may arise during the course of game play. Each other exception extends base class **GameAIException**. The entry point of the program is class Main, in package main.

The game's ai components decide what move the computer should make when the need arises, and each difficulty level is represented by a strategy, each of which extends the base class of type **Strategy**. The three difficulty levels were included because a computer that can play a perfect game every time is boring. Also, players new to the game, young children come to mind, might not possess sufficient skill to play a perfect game through all variations. The easier difficulty levels exist for allowing the player to win every once in a while.

The game's resource files, such as sounds, images, and gui layout descriptions are located in the **resources** folder. The .css file controlling the game's appearance is also located in the gui portion of the resources folder.
