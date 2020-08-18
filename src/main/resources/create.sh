#!/bin/bash
port=${1:-8080}

curl -H "content-type: application/json" -d '{"email":"superRandom"}' http://localhost:${port}/profiles