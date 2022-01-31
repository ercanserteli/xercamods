import os
import re

def get_trailing_number(s):
    m = re.search(r'\d+$', s)
    return int(m.group()) if m else None

files = [os.path.splitext(e.path)[0] for e in list(os.scandir(".")) if os.path.splitext(e.path)[1] == ".ogg"]
print(files)
files.sort(key=get_trailing_number, reverse=True)

for fbody in files:
    print(fbody)
    fext = ".ogg"
    num = get_trailing_number(fbody)
    new_name = fbody[:-len(str(num))] + str(num + 32) + fext
    print("New name:", new_name)
    os.rename(fbody + fext, new_name)
