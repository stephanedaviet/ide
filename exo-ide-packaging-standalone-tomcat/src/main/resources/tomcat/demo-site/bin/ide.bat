@REM
@REM Copyright (C) 2010 eXo Platform SAS.
@REM
@REM This is free software; you can redistribute it and/or modify it
@REM under the terms of the GNU Lesser General Public License as
@REM published by the Free Software Foundation; either version 2.1 of
@REM the License, or (at your option) any later version.
@REM
@REM This software is distributed in the hope that it will be useful,
@REM but WITHOUT ANY WARRANTY; without even the implied warranty of
@REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
@REM Lesser General Public License for more details.
@REM
@REM You should have received a copy of the GNU Lesser General Public
@REM License along with this software; if not, write to the Free
@REM Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
@REM 02110-1301 USA, or see the FSF site: http://www.fsf.org.
@REM

@echo off

rem Computes the absolute path of eXo
setlocal ENABLEDELAYEDEXPANSION
for %%i in ( !%~f0! )         do set BIN_DIR=%%~dpi
for %%i in ( !%BIN_DIR%\..! ) do set TOMCAT_HOME=%%~dpni

rem Sets some variables
set LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger"
set SECURITY_OPTS="-Djava.security.auth.login.config=%TOMCAT_HOME%\conf\jaas.conf"
set EXO_OPTS="-Djavasrc=%JAVA_HOME%\src.zip" "-Djre.lib=%JAVA_HOME%\jre\lib -Dorg.exoplatform.mimetypes=conf\mimetypes.properties -Dorg.exoplatform.ide.git.repo-dir=git"
set JAVA_OPTS=-Xshare:auto -Xms128m -Xmx512m %LOG_OPTS% %SECURITY_OPTS% %EXO_OPTS%


rem Launches the server
cd %BIN_DIR%
call catalina.bat %*
