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
        s.send(b"{\"color\": {\"count\": 2, \"colors\": [{\"r\": 255, \"g\": 0, \"b\": 0},{\"r\": 0, \"g\": 0, \"b\": 255}]},\"treshhigh\": 24, \"treshlow\": 20, \"brightness\": 123}")
    except socket.timeout:
        print("Timeout")

        
