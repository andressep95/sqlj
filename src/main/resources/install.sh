#!/bin/bash

# Crear directorio de instalación
INSTALL_DIR="/usr/local/sqlj"
mkdir -p $INSTALL_DIR

# Copiar el JAR y el script de shell al directorio de instalación
cp "$(dirname "$0")/sqlj-1.0.0.jar" $INSTALL_DIR
cp "$(dirname "$0")/sqlj" $INSTALL_DIR

# Asegurarse de que el script de shell tenga permisos de ejecución
chmod +x $INSTALL_DIR/sqlj

# Añadir el directorio de instalación al PATH
if [[ ":$PATH:" != *":$INSTALL_DIR:"* ]]; then
    echo "export PATH=\$PATH:$INSTALL_DIR" >> ~/.bashrc
    source ~/.bashrc
fi

echo "sqlj instalado correctamente. Puedes ejecutarlo usando el comando 'sqlj init'."
