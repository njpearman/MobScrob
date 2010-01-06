echo off
rem
rem Batch script for obfuscating the mobscrob jar
rem


set PROGUARD=C:\Development\java\common-libs\security\proguard4.2\lib
echo %PROGUARD%
java -jar %PROGUARD%\proguard.jar @dist.pro

pause