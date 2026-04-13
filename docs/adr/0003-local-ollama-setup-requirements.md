# ADR 0003: Local Ollama Setup Requirements for Development

## Status
Accepted

## Date
2026-04-13

## Context
The project is moving toward a local-first LLM strategy using Ollama (as proposed in ADR 0001). To use the application, developers must have a functional Ollama instance running locally on their development machine.

Running LLMs locally has significant hardware requirements:
- **RAM**: For 8B models like Llama 3, at least 8GB of RAM is recommended (16GB preferred for a smooth experience alongside development tools).
- **GPU (RTX 2070 SUPER)**: A dedicated GPU with VRAM (e.g., NVIDIA RTX 2070 SUPER) significantly speeds up response generation. (Optional)
- **Disk Space**: Approximately 5GB-10GB of free space is needed for the Llama 3 model and Ollama itself.

To enable GPU support in a Dockerized environment, the machine must have:
1.  **NVIDIA Drivers**: Properly installed and functional on the host.
2.  **NVIDIA Container Toolkit**: Installed on the host to allow Docker to access GPU resources.
3.  **Docker Compose (V2)**: Support for the `deploy` and `resources` reservation fields.

The following setup requirements were identified:
- **Application Compatibility**: The Spring AI Ollama starter expects a running Ollama server.
- **Model Availability**: The application is configured to use the `llama3` model by default.
- **Port Availability**: The default communication port for Ollama is `11434`.

## Decision
Developers can run Ollama using one of two methods:
1.  **Manual Installation (Recommended for simplicity)**: Install from [ollama.com](https://ollama.com). 
2.  **Dockerized Deployment (Recommended for orchestration)**: Run via a `docker-compose.yaml` (see proposed plan).

The following steps are required regardless of method:
1.  **Pull the Model**: Run `ollama run llama3` (locally or inside the container) to download the model.
2.  **Ensure Port Accessibility**: The Ollama server must be reachable at `http://localhost:11434`.

## Alternatives Considered
- **Dockerized Ollama (Revised)**: Initially disregarded because GPU passthrough (NVIDIA) in Docker can be complex to set up. However, for standard CPU-based development, a `docker-compose.yaml` provides a more repeatable and clean environment. We will now provide a Docker Compose configuration for this purpose.
- **Automated Model Download**: Still rejected as large model downloads (multi-GB) should be explicitly managed by the user.

## Consequences
- **Positive**:
  - **Simplicity**: No complex orchestration needed.
  - **Stability**: The application depends on a standard Ollama interface.
- **Negative**:
  - **Manual Step**: Adds a prerequisite for new developers before the application can be fully functional.
  - **Disk Usage**: Requires several gigabytes of free disk space to store the models.

## Related
- `docs/adr/0001-use-local-llm-with-ollama.md`: Proposes switching the application to Ollama.
- `src/main/resources/application.yaml`: Configuration for `ollama.base-url` and `chat.options.model`.
