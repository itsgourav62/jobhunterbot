@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  job-hunter-bot startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and JOB_HUNTER_BOT_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\job-hunter-bot-1.0-SNAPSHOT.jar;%APP_HOME%\lib\selenium-java-4.15.0.jar;%APP_HOME%\lib\webdrivermanager-5.6.2.jar;%APP_HOME%\lib\tika-core-2.9.0.jar;%APP_HOME%\lib\tika-parsers-standard-package-2.9.0.jar;%APP_HOME%\lib\slf4j-simple-2.0.9.jar;%APP_HOME%\lib\jakarta.mail-2.0.1.jar;%APP_HOME%\lib\tika-parser-cad-module-2.9.0.jar;%APP_HOME%\lib\docker-java-3.3.4.jar;%APP_HOME%\lib\docker-java-core-3.3.4.jar;%APP_HOME%\lib\docker-java-api-3.3.4.jar;%APP_HOME%\lib\jackson-annotations-2.16.0.jar;%APP_HOME%\lib\jackson-core-2.16.0.jar;%APP_HOME%\lib\jackson-databind-2.16.0.jar;%APP_HOME%\lib\h2-2.2.224.jar;%APP_HOME%\lib\selenium-chrome-driver-4.15.0.jar;%APP_HOME%\lib\selenium-devtools-v117-4.15.0.jar;%APP_HOME%\lib\selenium-devtools-v118-4.15.0.jar;%APP_HOME%\lib\selenium-devtools-v119-4.15.0.jar;%APP_HOME%\lib\selenium-firefox-driver-4.15.0.jar;%APP_HOME%\lib\selenium-devtools-v85-4.15.0.jar;%APP_HOME%\lib\selenium-edge-driver-4.15.0.jar;%APP_HOME%\lib\selenium-ie-driver-4.15.0.jar;%APP_HOME%\lib\selenium-safari-driver-4.15.0.jar;%APP_HOME%\lib\selenium-support-4.15.0.jar;%APP_HOME%\lib\selenium-chromium-driver-4.15.0.jar;%APP_HOME%\lib\selenium-remote-driver-4.15.0.jar;%APP_HOME%\lib\selenium-manager-4.15.0.jar;%APP_HOME%\lib\selenium-http-4.15.0.jar;%APP_HOME%\lib\selenium-json-4.15.0.jar;%APP_HOME%\lib\selenium-os-4.15.0.jar;%APP_HOME%\lib\selenium-api-4.15.0.jar;%APP_HOME%\lib\docker-java-transport-httpclient5-3.3.4.jar;%APP_HOME%\lib\httpclient5-5.2.1.jar;%APP_HOME%\lib\jcl-over-slf4j-2.0.7.jar;%APP_HOME%\lib\tika-parser-news-module-2.9.0.jar;%APP_HOME%\lib\tika-parser-code-module-2.9.0.jar;%APP_HOME%\lib\parso-2.0.14.jar;%APP_HOME%\lib\jmatio-1.5.jar;%APP_HOME%\lib\rome-2.1.0.jar;%APP_HOME%\lib\tika-parser-pkg-module-2.9.0.jar;%APP_HOME%\lib\junrar-7.5.5.jar;%APP_HOME%\lib\rome-utils-2.1.0.jar;%APP_HOME%\lib\slf4j-api-2.0.9.jar;%APP_HOME%\lib\gson-2.10.1.jar;%APP_HOME%\lib\dec-0.1.2.jar;%APP_HOME%\lib\tika-parser-microsoft-module-2.9.0.jar;%APP_HOME%\lib\tika-parser-miscoffice-module-2.9.0.jar;%APP_HOME%\lib\tika-parser-ocr-module-2.9.0.jar;%APP_HOME%\lib\jackcess-4.0.5.jar;%APP_HOME%\lib\commons-lang3-3.13.0.jar;%APP_HOME%\lib\tika-parser-webarchive-module-2.9.0.jar;%APP_HOME%\lib\tika-parser-apple-module-2.9.0.jar;%APP_HOME%\lib\tika-parser-zip-commons-2.9.0.jar;%APP_HOME%\lib\poi-ooxml-5.2.3.jar;%APP_HOME%\lib\commons-compress-1.24.0.jar;%APP_HOME%\lib\jhighlight-1.1.0.jar;%APP_HOME%\lib\poi-scratchpad-5.2.3.jar;%APP_HOME%\lib\poi-5.2.3.jar;%APP_HOME%\lib\tika-parser-mail-module-2.9.0.jar;%APP_HOME%\lib\tika-parser-mail-commons-2.9.0.jar;%APP_HOME%\lib\apache-mime4j-dom-0.8.9.jar;%APP_HOME%\lib\apache-mime4j-core-0.8.9.jar;%APP_HOME%\lib\commons-io-2.13.0.jar;%APP_HOME%\lib\tika-parser-audiovideo-module-2.9.0.jar;%APP_HOME%\lib\tika-parser-crypto-module-2.9.0.jar;%APP_HOME%\lib\tika-parser-digest-commons-2.9.0.jar;%APP_HOME%\lib\tika-parser-font-module-2.9.0.jar;%APP_HOME%\lib\tika-parser-html-module-2.9.0.jar;%APP_HOME%\lib\tika-parser-image-module-2.9.0.jar;%APP_HOME%\lib\tika-parser-pdf-module-2.9.0.jar;%APP_HOME%\lib\tika-parser-text-module-2.9.0.jar;%APP_HOME%\lib\tika-parser-xml-module-2.9.0.jar;%APP_HOME%\lib\tika-parser-xmp-commons-2.9.0.jar;%APP_HOME%\lib\vorbis-java-tika-0.8.jar;%APP_HOME%\lib\vorbis-java-core-0.8.jar;%APP_HOME%\lib\jaxb-runtime-2.3.6.jar;%APP_HOME%\lib\jakarta.activation-2.0.1.jar;%APP_HOME%\lib\auto-service-annotations-1.1.1.jar;%APP_HOME%\lib\guava-32.1.2-jre.jar;%APP_HOME%\lib\opentelemetry-exporter-logging-1.28.0.jar;%APP_HOME%\lib\opentelemetry-sdk-extension-autoconfigure-1.28.0.jar;%APP_HOME%\lib\opentelemetry-sdk-extension-autoconfigure-spi-1.28.0.jar;%APP_HOME%\lib\opentelemetry-sdk-1.28.0.jar;%APP_HOME%\lib\opentelemetry-sdk-trace-1.28.0.jar;%APP_HOME%\lib\opentelemetry-sdk-metrics-1.28.0.jar;%APP_HOME%\lib\opentelemetry-sdk-logs-1.28.0.jar;%APP_HOME%\lib\opentelemetry-sdk-common-1.28.0.jar;%APP_HOME%\lib\opentelemetry-semconv-1.28.0-alpha.jar;%APP_HOME%\lib\opentelemetry-api-events-1.28.0-alpha.jar;%APP_HOME%\lib\opentelemetry-extension-incubator-1.28.0-alpha.jar;%APP_HOME%\lib\opentelemetry-api-1.28.0.jar;%APP_HOME%\lib\opentelemetry-context-1.28.0.jar;%APP_HOME%\lib\byte-buddy-1.14.5.jar;%APP_HOME%\lib\docker-java-transport-3.3.4.jar;%APP_HOME%\lib\jna-5.13.0.jar;%APP_HOME%\lib\httpcore5-h2-5.2.jar;%APP_HOME%\lib\httpcore5-5.2.jar;%APP_HOME%\lib\dd-plist-1.27.jar;%APP_HOME%\lib\metadata-extractor-2.18.0.jar;%APP_HOME%\lib\tagsoup-1.2.1.jar;%APP_HOME%\lib\asm-9.5.jar;%APP_HOME%\lib\bcmail-jdk18on-1.76.jar;%APP_HOME%\lib\jackcess-encrypt-4.0.2.jar;%APP_HOME%\lib\bcpkix-jdk18on-1.76.jar;%APP_HOME%\lib\bcutil-jdk18on-1.76.jar;%APP_HOME%\lib\bcprov-jdk18on-1.76.jar;%APP_HOME%\lib\commons-codec-1.16.0.jar;%APP_HOME%\lib\pdfbox-2.0.29.jar;%APP_HOME%\lib\fontbox-2.0.29.jar;%APP_HOME%\lib\jai-imageio-core-1.4.0.jar;%APP_HOME%\lib\jbig2-imageio-3.0.4.jar;%APP_HOME%\lib\java-libpst-0.9.3.jar;%APP_HOME%\lib\xmpbox-2.0.29.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-collections4-4.4.jar;%APP_HOME%\lib\commons-exec-1.3.jar;%APP_HOME%\lib\pdfbox-tools-2.0.29.jar;%APP_HOME%\lib\jempbox-1.8.17.jar;%APP_HOME%\lib\xz-1.9.jar;%APP_HOME%\lib\juniversalchardet-2.4.0.jar;%APP_HOME%\lib\commons-csv-1.10.0.jar;%APP_HOME%\lib\jwarc-0.28.1.jar;%APP_HOME%\lib\xercesImpl-2.12.2.jar;%APP_HOME%\lib\failsafe-3.3.2.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\checker-qual-3.33.0.jar;%APP_HOME%\lib\error_prone_annotations-2.18.0.jar;%APP_HOME%\lib\xmpcore-6.1.11.jar;%APP_HOME%\lib\commons-math3-3.6.1.jar;%APP_HOME%\lib\SparseBitSet-1.2.jar;%APP_HOME%\lib\poi-ooxml-lite-5.2.3.jar;%APP_HOME%\lib\xmlbeans-5.1.1.jar;%APP_HOME%\lib\log4j-api-2.18.0.jar;%APP_HOME%\lib\curvesapi-1.07.jar;%APP_HOME%\lib\jakarta.xml.bind-api-2.3.3.jar;%APP_HOME%\lib\txw2-2.3.6.jar;%APP_HOME%\lib\istack-commons-runtime-3.0.12.jar;%APP_HOME%\lib\jdom2-2.0.6.1.jar;%APP_HOME%\lib\xml-apis-1.4.01.jar


@rem Execute job-hunter-bot
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %JOB_HUNTER_BOT_OPTS%  -classpath "%CLASSPATH%" com.jobhunter.Main %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable JOB_HUNTER_BOT_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%JOB_HUNTER_BOT_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
