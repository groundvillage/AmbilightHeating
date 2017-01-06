#!/usr/bin/env python3
import socket

print("Hello, cruel world")
PORT = 666
IP_ADDR_YUN = "192.168.240.1"

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.settimeout(5)
    try:
        s.connect((IP_ADDR_YUN, PORT))
        print("Connected!")
        s.send(b"{\"color\":{\"count\":2}}")
    except socket.timeout:
        print("Timeout")
