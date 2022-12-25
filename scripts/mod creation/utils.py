import os


def get_mod_id():
    mod_id = None
    if len(argv) == 2:
        mod_id = argv[1]
    else:
        entries = os.listdir("src\\main\\java\\xerca")
        if len(entries) == 1:
            mod_id = entries[0]
    return mod_id


def capitalize_firsts(a):
    return "_".join(list(map((lambda s: s[0].upper()+s[1:]), a.split("_"))))


def camelize(und):
    i = und.find("_")
    out = und
    while i != -1:
        out = out[:i] + out[i + 1].upper() + out[i + 2:]
        i = out.find("_")
    return out


def ccamelize(und):
    i = und.find("_")
    out = und
    while i != -1:
        out = out[:i] + out[i + 1].upper() + out[i + 2:]
        i = out.find("_")
    return out[0].upper() + out[1:]


def spacelize(und):
    i = und.find("_")
    out = und
    while i != -1:
        out = out[:i] + " " + out[i + 1:]
        i = out.find("_")
    return out
    

def add_to_file(filepath, anchor_line, addition):
    with open(filepath, 'r+', encoding="utf8") as f:
        text = f.read()

        i = text.find(anchor_line) + len(anchor_line) + 1 #2
        text = text[:i] + addition + text[i:]

        f.seek(0)
        f.write(text)
        f.truncate()
    
    
def add_to_java_file(filename, anchor_line, addition, mod_id):
    add_to_file(f'src/main/java/xerca/{mod_id}/{filename}', anchor_line, addition)
    
    
def add_to_asset_file(filename, anchor_line, addition, mod_id):
    add_to_file(f'src/main/resources/assets/{mod_id}/{filename}', anchor_line, addition)