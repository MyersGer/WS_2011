#!/bin/sh
ifconfig wlan0 down
iwconfig wlan0 mode ad-hoc
iwconfig wlan0 essid olsr
iwconfig wlan0 enc off
iwconfig wlan0 channel 11
iwconfig wlan0 txpower 1
ifconfig wlan0 up
ifconfig wlan0 10.0.0.110 netmask 255.255.255.0
iptables -I FORWARD -s 10.0.0.0/24 -j ACCEPT
iptables -I FORWARD -d 10.0.0.0/24 -j ACCEPT
iptables -t nat -A POSTROUTING -s 192.168.110.0/24 -j MASQUERADE
route add default gw 10.0.0.101
echo "nameserver 141.22.192.101" >> /etc/resolv.conf
echo "nameserver 130.149.2.12" >> /etc/resolv.conf
olsrd -nofork -f /etc/olsrd.conf
