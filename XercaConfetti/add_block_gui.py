from add_funcs import *
from easygui import *
from sys import argv
from utils import capitalize_firsts

msg = "Enter the information"
title = "Block Adder"
fieldNames = ["Name with underscores", "English name with underscores  (opt)",
              "Class name (opt)", "Texture folder (opt)"]

mod_id = get_mod_id()
if not mod_id:
    v = multenterbox("Enter mod id", title, ["Mod id"])
    mod_id = v[0]

title = title + " for " + mod_id

v = []
while True:
    v = multenterbox(msg, title, fieldNames)
    if v is None:
        break
    add_block(mod_id, v[0], capitalize_firsts(v[0]) if v[1] == "" else v[1], "Block" if v[2] == "" else v[2], v[3])
    msg = "Block added. You can add more"