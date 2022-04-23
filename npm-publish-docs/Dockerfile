# podman build --build-arg=mkdVersion=x.y.z -t docker.io/mpetuska/mkdocs-material-mike:x.y.z .
ARG mkdVersion
FROM squidfunk/mkdocs-material:$mkdVersion
RUN pip install mike
RUN git config --global user.name mike
RUN git config --global user.email mike@mike.mike
