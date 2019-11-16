import subprocess, sys, os

fname = sys.argv[1]

remove_wav = False
if fname[-3:] == "ogg":
    subprocess.run(["oggdec", fname, "-Q"])
    fname = fname[:-3] + "wav"
    remove_wav = True

for i in range(-3, 3):
    middle = fname[:-4] + str(i+22) + ".wav"
    subprocess.run(["soundstretch_x64", fname, middle, "-pitch=" + str(i)])
    subprocess.run(["oggenc", middle, "-Q"])
    os.remove(middle)
    
if remove_wav:
    os.remove(fname)