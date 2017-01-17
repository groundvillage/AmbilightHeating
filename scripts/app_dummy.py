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
        s.send(b"{\"color\": {\"count\": 2, \"colors\": [{\"r\": 255, \"g\": 0, \"b\": 0},{\"r\": 0, \"g\": 0, \"b\": 255}]},\"treshhigh\": 20, \"treshlow\": 20, \"brightness\": 123}")
        #s.send(b"{\"color\": {\"count\": 1, \"colors\": [{\"r\": 0, \"g\": 255, \"b\": 100}]},\"treshhigh\": 28, \"treshlow\": 28, \"brightness\": 50}")
        #s.send(b"{\"color\": {\"count\": 2, \"colors\": [{\"r\": 55, \"g\": 0, \"b\": 0}]},\"treshhigh\": 30, \"treshlow\": 29, \"brightness\": 123}")
    except socket.timeout:
        print("Timeout")

        
