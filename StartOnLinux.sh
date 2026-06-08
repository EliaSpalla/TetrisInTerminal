#!/bin/bash

# Vai nella cartella dello script
cd "$(dirname "$0")"

# Controlla se Java è installato
if ! command -v java &> /dev/null; then
    echo "ERRORE: Java non trovato. Installalo con: sudo apt install default-jdk"
    read -p "Premi Invio per uscire..."
    exit 1
fi

# Compila solo se necessario
NEED_COMPILE=false
if [ ! -f Main.class ]; then
    NEED_COMPILE=true
elif [ Main.java -nt Main.class ]; then
    NEED_COMPILE=true
fi

if [ "$NEED_COMPILE" = true ]; then
    echo "Compilazione in corso..."
    javac -cp .:lanterna.jar -encoding UTF-8 Main.java
    if [ $? -ne 0 ]; then
        echo ""
        echo "ERRORE: Compilazione fallita. Controlla il codice sorgente."
        read -p "Premi Invio per uscire..."
        exit 1
    fi
    echo "Compilazione completata."
fi

java -cp .:lanterna.jar Main
