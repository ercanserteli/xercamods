import os
import re
import subprocess
import sys


def get_fname():
    if len(sys.argv) == 2:
        fname = sys.argv[1]
    else:
        fname = input("Enter file path:")
    return fname

def get_trailing_number(s):
    m = re.search(r'\d+$', s)
    return int(m.group()) if m else None
    

def shift(fname, delta):
    if fname[0] == '"':
        fname = fname[1:]
    if fname[-1] == '"':
        fname = fname[:-1]

    remove_wav = False
    if fname[-3:] == "ogg":
        subprocess.run(["oggdec", fname, "-Q"])
        fname = fname[:-3] + "wav"
        remove_wav = True

    fbody = fname[:-4]
    number = get_trailing_number(fbody)
    fbody_name = fbody[:-len(str(number))]
    middle = fbody_name + str(number + delta) + ".wav"
    subprocess.run(["soundstretch_x64", fname, middle, "-pitch=" + str(delta)])
    subprocess.run(["oggenc", middle, "-Q"])
    os.remove(middle)
        
    if remove_wav:
        os.remove(fname)