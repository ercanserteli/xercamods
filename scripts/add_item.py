from sys import argv


def camelize(und):
    i = und.find("_")
    out = und
    while i != -1:
        out = out[:i] + out[i + 1].upper() + out[i + 2:]
        i = out.find("_")
    return out


def spacelize(und):
    i = und.find("_")
    out = und
    while i != -1:
        out = out[:i] + " " + out[i + 1:]
        i = out.find("_")
    return out


def add_item(mod_id, item_name, eng_name, tur_name, item_class="Item", tex_folder=""):
    if tex_folder != "":
        tex_folder = tex_folder + "/"
    eng_name = spacelize(eng_name)
    tur_name = spacelize(tur_name)
    is_pure = item_class == "Item"
    is_instrument = item_class == "ItemInstrument"

    add_items_entry(mod_id, item_name, item_class, is_pure)
    add_models_entry(mod_id, item_name, is_instrument, tex_folder)

    add_lang_entry(mod_id, item_name, "en_us", eng_name)
    add_lang_entry(mod_id, item_name, "tr_tr", tur_name)

    if is_instrument:
        add_sound_json_entry(mod_id, item_name)
        add_sound_events_entry(mod_id, item_name)


def add_lang_entry(mod_id, item_name, lang_id, trans_name):
    with open(f"src\\main\\resources\\assets\\{mod_id}\\lang\\{lang_id}.json", 'r+', encoding="utf8") as f:
        text = f.read()

        i = 2
        line = f'"item.{mod_id}.{item_name}": "{trans_name}",\n'

        text = text[:i] + line + text[i:]
        f.seek(0)
        f.write(text)
        f.truncate()


def add_items_entry(mod_id, item_name, item_class, is_pure):
    with open(f'src\\main\\java\\xerca\\{mod_id}\\common\\item\\Items.java', 'r+') as f:
        n_items = f.read()

        # Object holder
        line_find = "public final class Items {"
        i = n_items.find(line_find) + len(line_find) + 2
        line = f'public static final {item_class} {item_name.upper()} = null;\n    '
        n_items = n_items[:i] + line + n_items[i:]

        # Register
        line_find = "public static void registerItems(final RegistryEvent.Register<Item> event) {"
        id1 = n_items.find(line_find) + len(line_find) + 2
        line_find = "event.getRegistry().registerAll("
        id2 = n_items.find(line_find, id1) + len(line_find) + 2
        if is_pure:
            line = f'makeItem("{item_name}"),\n\t'
        else:
            line = f'new {item_class}("{item_name}"),'
        n_items = n_items[:id2] + line + n_items[id2:]

        f.seek(0)
        f.write(n_items)
        f.truncate()


def add_models_entry(mod_id, item_name, is_instrument, tex_folder):
    # Model json
    file_name = f"src\\main\\resources\\assets\\{mod_id}\\models\\item\\{item_name}.json"
    with open(file_name, 'w') as f:
        text = """
{{
    "parent": "item/{}",
    "textures": {{
        "layer0": "{}:item/{}{}"
    }}
}}""".format("handheld" if is_instrument else "generated", mod_id, tex_folder, item_name)
        f.write(text)


def add_sound_json_entry(mod_id, item_name):
    with open(f"src\\main\\resources\\assets\\{mod_id}\\sounds.json", 'r+', encoding="utf8") as f:
        text = f.read()

        i = text.rfind("}") - 1
        line_template = '"{ins}{i}": {{"category": "player", "sounds": [{{"name": "{mod_id}:{ins}/{ins}{i}"}}]}},\n'
        lines = [line_template.format(ins=item_name, i=i, mod_id=mod_id) for i in range(1, 49)]
        line = "".join(lines)

        text = text[:i] + line + text[i:]
        f.seek(0)
        f.write(text)
        f.truncate()


def add_sound_events_entry(mod_id, item_name):
    with open(f'src\\main\\java\\xerca\\{mod_id}\\common\\SoundEvents.java', 'r+') as f:
        text = f.read()

        line_find = "// Instrument SoundEvents declaration"
        i = text.find(line_find) + len(line_find) + 2
        line = "public static SoundEvent[] {}s;".format(item_name)
        text = text[:i] + line + text[i:]

        line_find = "// Instrument SoundEvent initialization"
        i = text.find(line_find) + len(line_find) + 2
        line = "{}s = new SoundEvent[48];".format(item_name)
        text = text[:i] + line + text[i:]

        line_find = "// Instrument SoundEvent creation"
        i = text.find(line_find) + len(line_find) + 2
        line = '{ins}s[i] = createSoundEvent("{ins}" + (i + 1));'.format(ins=item_name)
        text = text[:i] + line + text[i:]

        line_find = "// Instrument SoundEvent registration"
        i = text.find(line_find) + len(line_find) + 2
        line = 'event.getRegistry().registerAll({}s);'.format(item_name)
        text = text[:i] + line + text[i:]

        line_find = "// Instrument SoundEvent setting"
        i = text.find(line_find) + len(line_find) + 2
        line = 'Items.{ins}.setSounds({ins}s);'.format(ins=item_name)
        text = text[:i] + line + text[i:]

        f.seek(0)
        f.write(text)
        f.truncate()


def main():
    mod_id = argv[1]
    name = argv[2]
    eng_name = argv[3]
    tur_name = argv[4]
    item_class = "Item"
    tex_folder = ""

    if len(argv) >= 6:
        item_class = argv[5]
    if len(argv) >= 7:
        tex_folder = argv[6]
    add_item(mod_id, name, eng_name, tur_name, item_class, tex_folder)


# Parameters: mod_id, name, eng, tur[, class, tex_folder]
if __name__ == "__main__":
    main()
