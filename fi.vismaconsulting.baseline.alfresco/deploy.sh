#!/bin/bash

REPO=nexus.vismaconsulting.fi::default::https://nexus.vismaconsulting.fi/repository/baseline-alfresco
exec mvn -Pbaseline-alfresco clean deploy -DaltDeploymentRepository=$REPO -Dplatform.skip=true $@

