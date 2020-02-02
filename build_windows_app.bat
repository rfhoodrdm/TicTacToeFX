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
set MODULES_TARGET_DIR=%OUTPUT_DIR%\modules
set GAME_FOLDER_DIR=%OUTPUT_DIR%\TicTacToeFX_Win%VERSION_NUMBER%
set WIN_IMAGE_TARGET_DIR=%GAME_FOLDER_DIR%\java

@REM Paths to JavaFX Jmod modules. *** SET FOR LOCAL DEV ENVIRONMENT BEFORE RUNNING***
set WINDOWS_JAVAFX_JMOD_PATH=D:\dev\libs\JavaFX_jmods\openjfx-13.0.2_windows-x64_bin-jmods\javafx-jmods-13.0.2
@REM set LINUX_JAVAFX_JMOD_PATH =
@REM set MAC_JAVAFX_JMOD_PATH =

@echo ###############################################
@echo ########    Beginning Build script.    ########
@echo ###############################################

@echo.
@echo ########    Clean up prior build attempt.    ########
rmdir /Q /S %OUTPUT_DIR%

@echo.
@echo ########    Create needed folders    ########
mkdir %MODULES_TARGET_DIR%


@echo.
@echo ########    Converting existing .jar file to module.    ########
jmod  create  --class-path  %JAR_PATH%\TicTacToeFX.jar  --main-class edu.usf.mail.rfhood.TicTacToeFX.main.Main  %MODULES_TARGET_DIR%\TicTacToeFX.jmod


@echo.
@echo ########    Linking to JavaFX platform-specific jmods    ########
jlink  --output %WIN_IMAGE_TARGET_DIR%  --module-path  %MODULES_TARGET_DIR%;%WINDOWS_JAVAFX_JMOD_PATH%  --add-modules  edu.usf.mail.rfhood.TicTacToeFX  --launcher TicTacToeFX=edu.usf.mail.rfhood.TicTacToeFX

@REM Also copy the launcher .bat file into the windows-specific image folder.
copy  build_components\LAUNCH_GAME.bat  %GAME_FOLDER_DIR%

