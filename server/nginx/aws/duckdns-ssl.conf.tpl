server {
    listen 80;
    server_name ${project_name}.duckdns.org;

    location / {
        return 301 https://$host$request_uri;
    }
}

server {
    listen 443 ssl;
    server_name ${project_name}.duckdns.org;

    ssl_certificate /etc/letsencrypt/live/${project_name}.duckdns.org/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/${project_name}.duckdns.org/privkey.pem;

    include conf.d/app_locations.inc;
}