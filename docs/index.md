# Hand's on

## Warm up

The required infrastructure is available by running Docker containers.

You can start the whole by running Docker compose.

```bash
cd infrastructure
docker compose up -d
```

You can then check the running containers by running this command:

```bash
docker compose ps
```
 
## Ready? Let's deep dive into versionning!

Here are the chapters covered by this workshop:

1. [Dealing with updates without versionning](./01-without_versionning.md)
2. [Our first version](./02-first_version.md)
3. [Adding new customers and a new functionalities](./03-second-version.md)
4. [Configuration management](./04-scm.md)
5. [Dealing with conflicts](./05-conflicts.md)
6. [Authorization issues](./06-authorization.md)