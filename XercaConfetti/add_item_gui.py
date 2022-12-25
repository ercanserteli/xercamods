from add_funcs import *
from easygui import *
from sys import argv
from utils import capitalize_firsts, get_mod_id

msg = "Enter the information"
title = "Item Adder"
fieldNames = ["Name with underscores", "English name with underscores (opt)",
              "Class name (opt)", "Texture folder (opt)", "Is food (opt)"]

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
    add_item(mod_id, v[0], capitalize_firsts(v[0]) if v[1] == "" else v[1], "Item" if v[2] == "" else v[2], v[3], v[4] != "")
    msg = "Item added. You can add more"