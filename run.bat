@echo off

set PROG="c:\Program Files\Java\jdk1.8.0_112\bin\javaw"
set JARZIP=.\MouseDictionary-1.0-SNAPSHOT-jar-with-dependencies.jar

start "test" %PROG% -Dfile.encoding=utf-8  -jar %JARZIP%

