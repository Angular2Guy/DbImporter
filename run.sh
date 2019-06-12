#!/bin/sh
java -Xms512m -Xmx1G -XX:+UseG1GC -XX:+UseStringDeduplication -jar target/dbimporter-0.0.1-SNAPSHOT.jar