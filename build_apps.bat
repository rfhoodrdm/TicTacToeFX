@REM
@REM Script to build the application image AFTER the code has been compiled and exported to a jar.
@REM Assumes that the JDK is on the path in some way.
@REM ***NOTE***: Need to set paths for JavaFX JMods for local setup before running.
@REM


@REM **************************************************
@REM First, set up necessary directories and settings.
@REM **************************************************

set VERSION_NUMBER=1.0.0

@REM Paths to folders which are a part of the build process.
set JAR_PATH=out\artifacts\TicTacToeFX_jar
set OUTPUT_DIR=%JAR_PATH%\build

set WIN_MODULES_TARGET_DIR=%OUTPUT_DIR%\modules\win
set WIN_GAME_FOLDER_DIR=%OUTPUT_DIR%\TicTacToeFX_Win_%VERSION_NUMBER%
set WIN_IMAGE_TARGET_DIR=%WIN_GAME_FOLDER_DIR%\java

set MAC_MODULES_TARGET_DIR=%OUTPUT_DIR%\modules\mac
set MAC_GAME_FOLDER_DIR=%OUTPUT_DIR%\TicTacToeFX_Mac_%VERSION_NUMBER%
set MAC_IMAGE_TARGET_DIR=%MAC_GAME_FOLDER_DIR%\java

set LINUX_MODULES_TARGET_DIR=%OUTPUT_DIR%\modules\linux
set LINUX_GAME_FOLDER_DIR=%OUTPUT_DIR%\TicTacToeFX_Linux_%VERSION_NUMBER%
set LINUX_IMAGE_TARGET_DIR=%LINUX_GAME_FOLDER_DIR%\java



@REM Paths to Java and JavaFX jmods. *** SET FOR LOCAL DEV ENVIRONMENT BEFORE RUNNING OR COMMENT OUT***
set WINDOWS_JAVAFX_JMOD_PATH=D:\dev\libs\JavaFX_jmods\openjfx-13.0.2_windows-x64_bin-jmods\javafx-jmods-13.0.2
set LINUX_JAVAFX_JMOD_PATH=D:\dev\libs\JavaFX_jmods\openjfx-13.0.2_linux-x64_bin-jmods\javafx-jmods-13.0.2
set MAC_JAVAFX_JMOD_PATH=D:\dev\libs\JavaFX_jmods\openjfx-13.0.2_osx-x64_bin-jmods\javafx-jmods-13.0.2

@REM For windows, if the JDK is set up, then the path to the JDK jmods is implicit. Others must be set.
@REM Comment out if not using.
set MAC_JDK_JMOD_PATH=D:\dev\libs\jdk-13.0.2.jdk_mac\Contents\Home\jmods
set LINUX_JDK_JMOD_PATH=D:\dev\libs\jdk-13.0.2_linux\jmods



@echo ###############################################
@echo ########    Beginning Build script.    ########
@echo ###############################################

@echo.
@echo ########    Clean up prior build attempt.    ########
rmdir /Q /S %OUTPUT_DIR%

@echo.
@echo ########    Create needed folders    ########
mkdir %WIN_MODULES_TARGET_DIR%
mkdir %MAC_MODULES_TARGET_DIR%
mkdir %LINUX_MODULES_TARGET_DIR%


@echo.
@echo ########    Converting existing .jar file to module.    ########
@REM Comment out jmod creation for architectures we are not targeting.
@REM Target architecture for windows:       windows-amd64
@REM Target architecture for mac:           macos-amd64
@REM Target architecture for linux:         linux-amd64

jmod  create  --class-path  %JAR_PATH%\TicTacToeFX.jar  --main-class edu.usf.mail.rfhood.TicTacToeFX.main.Main  %WIN_MODULES_TARGET_DIR%\TicTacToeFX.jmod
jmod  create  --target-platform macos-amd64  --class-path  %JAR_PATH%\TicTacToeFX.jar  --main-class edu.usf.mail.rfhood.TicTacToeFX.main.Main  %MAC_MODULES_TARGET_DIR%\TicTacToeFX.jmod
jmod  create  --target-platform linux-amd64  --class-path  %JAR_PATH%\TicTacToeFX.jar  --main-class edu.usf.mail.rfhood.TicTacToeFX.main.Main  %LINUX_MODULES_TARGET_DIR%\TicTacToeFX.jmod


@echo.
@echo ########    Linking to JavaFX platform-specific jmods    ########
@REM Comment out jlinking for architectures we are not targeting.
jlink  --output %WIN_IMAGE_TARGET_DIR%  --module-path  %WIN_MODULES_TARGET_DIR%;%WINDOWS_JAVAFX_JMOD_PATH%  --add-modules  edu.usf.mail.rfhood.TicTacToeFX  --launcher TicTacToeFX=edu.usf.mail.rfhood.TicTacToeFX
jlink  --output %MAC_IMAGE_TARGET_DIR%  --module-path  %MAC_JDK_JMOD_PATH%;%MAC_MODULES_TARGET_DIR%;%MAC_JAVAFX_JMOD_PATH%  --add-modules  edu.usf.mail.rfhood.TicTacToeFX  --launcher TicTacToeFX=edu.usf.mail.rfhood.TicTacToeFX
jlink  --output %LINUX_IMAGE_TARGET_DIR%  --module-path  %LINUX_JDK_JMOD_PATH%;%LINUX_MODULES_TARGET_DIR%;%LINUX_JAVAFX_JMOD_PATH%  --add-modules  edu.usf.mail.rfhood.TicTacToeFX  --launcher TicTacToeFX=edu.usf.mail.rfhood.TicTacToeFX


@echo.
@echo ########    Copying launcher files and misc. into image directories    ########
@REM Also copy the launcher .bat or .sh file into the architecture-specific image folder.
@REM Again, comment out any copies for architectures we are not targeting.
copy  build_components\LAUNCH_GAME.bat  %WIN_GAME_FOLDER_DIR%
copy  build_components\LAUNCH_GAME.sh   %MAC_GAME_FOLDER_DIR%
copy  build_components\LAUNCH_GAME.sh   %LINUX_GAME_FOLDER_DIR%


@REM Lastly add any other files such as notes, ReadMe files, game manuals, etc.
copy  build_components\LINUX_READ_ME_FIRST.txt   %LINUX_GAME_FOLDER_DIR%
