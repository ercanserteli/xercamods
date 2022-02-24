import os
import subprocess

for entry in os.scandir("."):
    if os.path.splitext(entry.path)[1] == ".wav":
        print(entry.path)
        middle = os.path.splitext(entry.path)[0] + "_mono" + os.path.splitext(entry.path)[1]
        subprocess.run(["ffmpeg", "-i", entry.path, "-ac", "1", middle])
        subprocess.run(["oggenc", middle, "-Q"])
        os.remove(middle)
        os.rename(os.path.splitext(middle)[0] + ".ogg", os.path.splitext(entry.path)[0] + ".ogg")

