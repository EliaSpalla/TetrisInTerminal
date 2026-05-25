@echo off
chcp 65001
javac -cp .;lanterna.jar Main.java
start javaw -cp .;lanterna.jar Main