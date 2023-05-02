c# PVS deploy
#### [Visit Official PVS Online](https://pvs.xcc.tw)
Self-host a production-optimized PVS in any place.

Note that this deployment is not for development use.

### Before start

Ensure Docker has already been installed in your environment.
[Install guide](https://docs.docker.com/get-docker/)

### Deploy steps

1. clone this repository.
2. create your `.env` file in the project root. There's an example env file you may use in this repository.
3. `docker compose up -d`
4. The front end will be exposed on the `4344` port (by default), and the backend will be the `9100` port (by default).
5. Now you may use some reverse-proxy server to host PVS, we recommend using [Caddy](https://caddyserver.com/v2).

### Images on docker hub

- [Frontend](https://hub.docker.com/r/xanonymous/pvs-frontend)
- [Backend](https://hub.docker.com/r/xanonymous/pvs-backend)
