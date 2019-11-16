from add_item import *
from easygui import *
from sys import argv

msg = "Enter the information"
title = "Item Adder"
fieldNames = ["Name w/ underscores", "English name w/ underscores", "Turkish name w/ underscores",
              "Class name (optional)", "Texture folder (optional)"]

if len(argv) == 2:
    mod_id = argv[1]
    title = title + " for " + mod_id

    v = []
    while True:
        v = multenterbox(msg, title, fieldNames)
        if v is None:
            break
        add_item(mod_id, v[0], v[1], v[2], v[3], v[4])
        msg = "Item added. You can add more"
else:
    print("Type the mod_id as a parameter")