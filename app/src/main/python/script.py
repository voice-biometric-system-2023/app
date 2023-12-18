import os
import time
import pickle
import warnings
import numpy as np
import socket
from scipy.io.wavfile import read, WavFileWarning

warnings.simplefilter("ignore", WavFileWarning)


def start_server():
    host = '0.0.0.0'
    port = 1234

    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind((host, port))
    server_socket.listen(1)

    client_socket, addr = server_socket.accept()
    response = client_socket.recv(1024).decode()

    client_socket.close()
    server_socket.close()
    return response


def send_audio_data(audio_data, name, do):
    host = '192.168.0.101'
    port = 8888

    response = 'Nothing'

    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect((host, port))

    data_to_send = {'audio_data': audio_data, 'filename': name, 'do': do}

    serialized_data = pickle.dumps(data_to_send)
    client_socket.sendall(serialized_data)

    client_socket.close()

    response = start_server()

    return response if response else "Nothing"


def read_audio_file(audio_path, name, do: str):

    path = audio_path.strip()

    sr, audio = read(path)
    print("Sample Rate: ", str(sr))
    print("Data: ", str(audio))
    print("Data shape: ", audio.shape)

    res = send_audio_data(audio, name, do)

    return res

