#!/bin/sh
set -e

SIDECAR_HOST=$1
SIDECAR_PORT=$2

echo "Setting up transparent proxy to $SIDECAR_HOST:$SIDECAR_PORT"

# Get the sidecar container IP
SIDECAR_IP=$(getent hosts $SIDECAR_HOST | awk '{ print $1 }')

if [ -z "$SIDECAR_IP" ]; then
    echo "ERROR: Could not resolve $SIDECAR_HOST"
    exit 1
fi

echo "Sidecar IP: $SIDECAR_IP"
echo "255.0.0.0 mail-sender.corp" >> /etc/hosts

# Flush existing rules to start clean
iptables -t nat -F OUTPUT
iptables -t nat -F POSTROUTING

# Configure iptables to redirect all outgoing traffic to sidecar proxy (port 3128)
# Don't redirect DNS traffic (port 53)
iptables -t nat -A OUTPUT -p tcp --dport 53 -j ACCEPT
iptables -t nat -A OUTPUT -p udp --dport 53 -j ACCEPT

# Don't redirect Postgres traffic (port 5432)
iptables -t nat -A OUTPUT -p tcp --dport 5432 -j ACCEPT
iptables -t nat -A OUTPUT -p tcp --dport 587 -j ACCEPT

# Don't redirect traffic to Docker network (internal communication)
iptables -t nat -A OUTPUT -d 127.0.0.0/8 -j ACCEPT
iptables -t nat -A OUTPUT -d 10.0.0.0/8 -j ACCEPT
iptables -t nat -A OUTPUT -d 172.16.0.0/12 -j ACCEPT
iptables -t nat -A OUTPUT -d 192.168.0.0/16 -j ACCEPT

# Don't redirect traffic to Postgres cluster nodes
iptables -t nat -A OUTPUT -d postgres-1 -j ACCEPT

# Don't redirect traffic to the sidecar itself (avoid loops)
iptables -t nat -A OUTPUT -d $SIDECAR_IP -j ACCEPT

# Redirect all other TCP traffic to sidecar proxy port (3128)
iptables -t nat -A OUTPUT -p tcp -j DNAT --to-destination $SIDECAR_IP:$SIDECAR_PORT

# Set up routing for the redirected packets
iptables -t nat -A POSTROUTING -j MASQUERADE

echo "Transparent proxy setup complete. All external TCP traffic will be routed through $SIDECAR_HOST ($SIDECAR_IP:$SIDECAR_PORT)"