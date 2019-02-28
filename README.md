# Kubernetes HPA custom autoscaler based on the number of files from a GCP Filestore share

## Why this project?

This is useful when you want to scale your Kubernetes cluster based on the number of files on a Cloud Filestore (NFS) share, Stackdriver exposes by default many metrics regarding Filestore, but none of them has the total number of files.

A NFS fileshare is the only type of `PersistentVolume` that can be accessed in `ReadWriteMany` mode on Google Cloud Platform. 

## How does it work
It exposes the number of files from the Filestore instance in the Prometues txt format, prometheus-to-sd will consume the data and import it to Stackdriver.

## API documentation
The REST API supports the following params configurable in the `filestore-hpa.yml` file:

|Name|Description|
|---|---|
|share_name|mandatory path param, it will be used as the Stackdriver resource label.
|recursive|query param, optional, defaults to false. If true it will count the files in the current directory and also from subdirectories.

Example:

`http://localhost:9309/share_name/share_001?recursive=true`
## How to install

First, install the prometheus-to-sd pod:

`kubectl create clusterrolebinding cluster-admin-binding --clusterrole cluster-admin --user "$(gcloud config get-value account)"`

`kubectl apply -f https://raw.githubusercontent.com/GoogleCloudPlatform/k8s-stackdriver/master/custom-metrics-stackdriver-adapter/deploy/production/adapter_new_resource_model.yaml`

Replace the following parameters in `filestore-hpa.yml` file:
- `[DEPLOYMENT]` is the name of the deployment you want to scale
- `[FILESHARE]` is the name of the fileshare on the Cloud Filestore instance.
- `[IP_ADDRESS]` is the IP address for the Cloud Filestore instance.
- `[STORAGE]` is the size of the fileshare on the Cloud Filestore instance
- `[FILESERVER]` is the name of the share used as Stackdriver resource label

And then apply the configuration:

`kubectl apply -f filestore-hpa.yml`
