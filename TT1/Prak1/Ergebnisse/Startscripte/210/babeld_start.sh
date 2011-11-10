#!/bin/sh
ifconfig wlan0 down
iwconfig wlan0 mode ad-hoc
iwconfig wlan0 essid babel
iwconfig wlan0 enc off
iwconfig wlan0 channel 1
iwconfig wlan0 txpower 1
ifconfig wlan0 up
ifconfig wlan0 10.0.0.210 netmask 255.255.255.0 # XYZ=AP-Nummer
echo "nameserver 141.22.192.101" >> /etc/resolv.conf
echo "nameserver 130.149.2.12" >> /etc/resolv.conf
iptables -I FORWARD -s 10.0.0.0/24 -j ACCEPT
iptables -I FORWARD -d 10.0.0.0/24 -j ACCEPT
iptables -t nat -A POSTROUTING -s 192.168.210.0/24 -j MASQUERADE
route add default gw 10.0.0.201 wlan0
babeld -C 'redistribute metric 256' -D wlan0 eth0 # /etc/babeld.conf -D
