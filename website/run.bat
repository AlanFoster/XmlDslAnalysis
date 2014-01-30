@echo off
echo ===============================================
echo Running Application
echo ===============================================

:: Remember where we are
pushd .

:: Begin the app
pushd app 
node ../scripts/start-server.js

:: Reset to the previous directory
popd
