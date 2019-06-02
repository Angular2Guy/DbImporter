#!/bin/sh
java -Xms1G -Xmx2G -XX:+UseG1GC -XX:+UseStringDeduplication -jar target/dbimporter-0.0.1-SNAPSHOT.jar