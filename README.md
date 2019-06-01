# Db Importer

![Build Status](https://travis-ci.com/Angular2Guy/DbImporter.svg?branch=master)

## Purpose
The project had the purpose to test the performance of Spring Boot with Jpa and Webflux for the import of larger(3GB file, 10M rows) Dataset. As Database Postgresql is used because easy to use and free and can be run as Docker image.

## Create a inputfile
To import a larger Dataset the Db Importer can create it with the rest endpoint: /rest/import/generate?rows=10000000&type=csv
The file will be stored at a directory that has to be set in the property: dbimporter.tempdir
The filename is: import.csv


## Import the inputfile
To import the file the Db Importer uses the rest endpoint: /rest/import/single?type=csv
The Db Importer uses the Spring Flux to read in the file map it in Jpa entities and split it in lists of 1000 entities. Then the Flux stores the entities in parallel in Postgresql. Each list of entities gets a transaction.
To import multiple '.csv' files in the directory the rest endpoint: /rest/import/multi?type=csv can be used. Then a Flux of file Fluxes is created and the import process is started. 

## Result
With 2 GB Ram the Java Cpu load is not the limiting factor. The Postgresql db gets loaded to the max. The Result will depend on the power of your db but until you hit the limit of your Java Cpu load this simple setup is sufficiant. 