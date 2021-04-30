#!/bin/bash
eval $(minikube docker-env)
docker build ../../trace-analytics-sample-app/sample-app -t example-k8s/sample-app
docker build ../../kibana-trace-analytics -t example-k8s/kibana
docker build ../../.. -f ../../../examples/dev/trace-analytics-sample-app/Dockerfile -t example-k8s/data-prepper
