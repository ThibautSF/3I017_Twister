#!/bin/bash

mongoimport --db twister_mongo --collection message --upsert --file twister_mongo.message.json

