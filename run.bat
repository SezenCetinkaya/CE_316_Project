@echo off
setlocal
cd /d "%~dp0"

@rem ASCII Gradle cache path (fixes daemon errors when Windows username has non-ASCII chars)
if not defined GRADLE_USER_HOME set "GRADLE_USER_HOME=%~dp0.gradle-home"

@rem Optional: point JAVA_HOME at JDK 21 if installed (Gradle toolchain will auto-download otherwise)
if exist "C:\Program Files\Eclipse Adoptium\jdk-21" set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21"
if exist "C:\Program Files\Java\jdk-21" set "JAVA_HOME=C:\Program Files\Java\jdk-21"

set "JAVA_TOOL_OPTIONS=-Djavafx.cachedir=%~dp0.openjfx-cache"

@rem Stop stale Gradle daemons and unlock build output (fixes processResources failures)
call gradlew.bat --stop >nul 2>&1

call gradlew.bat run --no-daemon %*
endlocal
