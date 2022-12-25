from add_funcs import *
from easygui import *
from sys import argv
from utils import capitalize_firsts

msg = "Enter the information"
title = "Entity Adder"
fieldNames = ["Name with underscores", "English name with underscores (opt)", "Is Mob? (opt)"]

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
    add_entity(mod_id, v[0], capitalize_firsts(v[0]) if v[1] == "" else v[1], None, v[2] != "")
    msg = "Entity added. You can add more"