FROM mcr.microsoft.com/vscode/devcontainers/universal:1-linux

## Install dependencies
RUN sudo apt update && \
    sudo apt install -y curl jq

USER codespace

RUN bash -c "python -m pip install --upgrade pip wheel && \
    python -m pip install httpie"

RUN bash -c ". /usr/locaal/sdkman/bin/sdkman-init.sh && \
    sdk install java 17.0.5-tem && \
    sdk default java 17.0.5-tem"