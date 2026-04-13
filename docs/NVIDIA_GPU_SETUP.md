# NVIDIA GPU Setup for Docker and Ollama

This document provides a guide for setting up your **NVIDIA RTX 2070 SUPER** to work with the Docker-based local AI stack.

## Official Documentation
- [NVIDIA Container Toolkit Install Guide](https://docs.nvidia.com/datacenter/cloud-native/container-toolkit/latest/install-guide.html)

## Installation Recipe (Ubuntu/Debian)

Run these steps in your terminal:

1.  **Configure the package repository**:
    ```bash
    # Download the key and save it to a dedicated keyring file (Fixes legacy trusted.gpg warning)
    curl -fsSL https://nvidia.github.io/libnvidia-container/gpgkey | sudo gpg --dearmor -o /usr/share/keyrings/nvidia-container-toolkit-keyring.gpg

    # Configure the repository to use this specific keyring
    curl -s -L https://nvidia.github.io/libnvidia-container/stable/deb/nvidia-container-toolkit.list | \
      sed 's#deb \[signed-by=/usr/share/keyrings/nvidia-container-toolkit-keyring.gpg\] https://#deb [signed-by=/usr/share/keyrings/nvidia-container-toolkit-keyring.gpg] https://#g' | \
      sudo tee /etc/apt/sources.list.d/nvidia-container-toolkit.list
    ```

2.  **Install the toolkit**:
    ```bash
    sudo apt-get update
    sudo apt-get install -y nvidia-container-toolkit
    ```

3.  **Configure the Docker runtime**:
    ```bash
    sudo nvidia-ctk runtime configure --runtime=docker
    ```

4.  **Restart Docker**:
    ```bash
    sudo systemctl restart docker
    ```

5.  **Verify the installation**:
    Run this command to see if Docker can access your GPU:
    ```bash
    sudo docker run --rm --runtime=nvidia --gpus all nvidia/cuda:11.5.2-base-ubuntu20.04 nvidia-smi
    ```
    *If you see the NVIDIA-SMI table with your RTX 2070 SUPER, you are ready!*


