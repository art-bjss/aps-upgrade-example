# Activiti BPM Definitions

## Purpose

This project contains the BPMN, task listeners, and other components that make up an Activiti workflow.

## Building and Running

This project does not directly provide any runnable targets.
To load the workflows into a running APS instance, use the BPM Loader 
[activiti-bpm-loader](../activiti-bpm-loader/README.md).

##  Implementation

### Spring Beans

All spring beans that are intended to be visible to APS are be defined under
[com.activiti.extension.bean.ds](./src/main/java/com/activiti/extension/bean/ds).
These include:
- Task listeners
- Execution listeners (for example )
- Expression evaluators (e.g. class DsGroup)
- Delegates (e.g. CertificateDelegate)

### Process and Form Definitions

Alfresco Process and Form Definitions are defined under
[resources](./src/main/resources/scot/disclosure/activitibpm/app)
These are organised under subdirectories, one per APS App.


