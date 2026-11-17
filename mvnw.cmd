@echo off
setlocal
set "MAVEN_PROJECTBASEDIR=%~dp0"
if not "%JAVA_HOME%"=="" (
  set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
) else (
  set "JAVA_CMD=java"
)
"%JAVA_CMD%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" -classpath "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain %*
