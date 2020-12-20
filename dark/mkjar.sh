#!/bin/bash
echo making jar
jar cvfe dark.jar dc bwana.properties xml -C classes .
echo done
