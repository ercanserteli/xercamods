"""Generates the doner block models and blockstate.

A doner is a square column of meat wrapped around an iron bar. There are two stages:

Filling (raw): adding mutton grows the tower in height. Each mutton adds
RAW_PIXELS_PER_STAGE pixels, and it takes RAW_STAGES muttons (meat=1..RAW_STAGES)
to reach the full 12-pixel-tall tower, at which point it can cook.

Slicing (cooked): a knife shaves the meat down one face at a time. Cooking resets
MEAT_AMOUNT to 4 (the full-width box), and slicing peels concentric width layers:
layers 4, 3 and 2 are 1 voxel thick on each side, so they take 4 slices each; the
innermost layer (meat=1) is only 2x2, so it is split into two 2x1 slabs and takes
2 slices. That gives 4 + 4 + 4 + 2 = 14 slices in total.

For cooked (meat, side) the meat box starts at the full layer bounds and gets one
face shaved inward by 1 voxel per side, in the order west, north, east. After the
last side the box equals the next inner layer, so that transition is handled in
code by lowering MEAT_AMOUNT instead of being its own model.
"""
import json
import os
import re

MODELS_DIR = "../XercaFood/src/main/resources/assets/xercafood/models/block/doner/"
BLOCKSTATE_PATH = "../XercaFood/src/main/resources/assets/xercafood/blockstates/block_doner.json"

# Filling stage: how many muttons, and how much height each one adds.
RAW_STAGES = 6
RAW_PIXELS_PER_STAGE = 2

# Cooked stage: full bounds of each width layer (lo..hi in x and z) and how many
# slices (sides) it takes to peel it. The innermost 2x2 layer only has two slabs.
LAYER_BOUNDS = {4: (4, 12), 3: (5, 11), 2: (6, 10), 1: (7, 9)}
LAYER_SIDES = {4: 4, 3: 4, 2: 4, 1: 2}

# The crossed iron bar in the middle, identical for every model.
BAR_ELEMENTS = [
    {
        "from": [8, 0, 7],
        "to": [8, 16, 9],
        "faces": {
            "north": {"uv": [0, 0, 0, 16], "texture": "#bar"},
            "east": {"uv": [12, 0, 14, 16], "texture": "#bar"},
            "south": {"uv": [0, 0, 0, 16], "texture": "#bar"},
            "west": {"uv": [12, 0, 14, 16], "texture": "#bar"},
            "up": {"uv": [0, 0, 0, 2], "texture": "#bar"},
            "down": {"uv": [0, 0, 0, 2], "texture": "#bar"},
        },
    },
    {
        "from": [7, 0, 8],
        "to": [9, 16, 8],
        "faces": {
            "north": {"uv": [7, 0, 9, 16], "texture": "#bar"},
            "east": {"uv": [0, 0, 0, 16], "texture": "#bar"},
            "south": {"uv": [7, 0, 9, 16], "texture": "#bar"},
            "west": {"uv": [0, 0, 0, 16], "texture": "#bar"},
            "up": {"uv": [0, 0, 2, 0], "texture": "#bar"},
            "down": {"uv": [0, 0, 2, 0], "texture": "#bar"},
        },
    },
]


def dumps_compact_arrays(obj, indent):
    """json.dumps but keeps flat number arrays (from/to/uv) on a single line."""
    text = json.dumps(obj, indent=indent)
    return re.sub(r"\[\s*([\d\s,.-]+?)\s*\]",
                  lambda m: "[" + re.sub(r"\s+", " ", m.group(1)).replace(" ,", ",") + "]",
                  text)


def raw_element(meat):
    top = 2 + RAW_PIXELS_PER_STAGE * meat
    vmin = 12 - (top - 2)  # texture is anchored at the bottom (v=12) and grows up
    return {
        "from": [4, 2, 4],
        "to": [12, top, 12],
        "faces": {
            "north": {"uv": [0, vmin, 8, 12], "texture": "#raw"},
            "east": {"uv": [0, vmin, 8, 12], "texture": "#raw"},
            "south": {"uv": [0, vmin, 8, 12], "texture": "#raw"},
            "west": {"uv": [0, vmin, 8, 12], "texture": "#raw"},
            "up": {"uv": [8, 0, 16, 8], "texture": "#raw"},
            "down": {"uv": [8, 0, 16, 8], "texture": "#raw"},
        },
    }


def cooked_element(meat, side):
    lo, hi = LAYER_BOUNDS[meat]
    x0, x1, z0, z1 = lo, hi, lo, hi
    if side >= 1:  # west sliced off
        x0 = lo + 1
    if side >= 2:  # north sliced off
        z0 = lo + 1
    if side >= 3:  # east sliced off
        x1 = hi - 1

    return {
        "from": [x0, 2, z0],
        "to": [x1, 14, z1],
        "faces": {
            "north": {"uv": [x0 - 4, 0, x1 - 4, 12], "texture": "#cooked"},
            "east": {"uv": [z0 - 4, 0, z1 - 4, 12], "texture": "#cooked"},
            "south": {"uv": [x0 - 4, 0, x1 - 4, 12], "texture": "#cooked"},
            "west": {"uv": [z0 - 4, 0, z1 - 4, 12], "texture": "#cooked"},
            "up": {"uv": [8 + x0 - 4, z0 - 4, 8 + x1 - 4, z1 - 4], "texture": "#cooked"},
            "down": {"uv": [8 + x0 - 4, z0 - 4, 8 + x1 - 4, z1 - 4], "texture": "#cooked"},
        },
    }


def model_json(element):
    return {"parent": "xercafood:block/doner/doner", "elements": BAR_ELEMENTS + [element]}


def write_model(out_dir, name, element):
    with open(os.path.join(out_dir, name + ".json"), "w") as f:
        f.write(dumps_compact_arrays(model_json(element), "\t"))
        f.write("\n")


def main():
    here = os.path.dirname(os.path.abspath(__file__))
    out_dir = os.path.normpath(os.path.join(here, MODELS_DIR))

    variants = {}

    # Raw filling stages (height grows with mutton; side is irrelevant when raw).
    for meat in range(1, RAW_STAGES + 1):
        name = "doner_{}".format(meat)
        write_model(out_dir, name, raw_element(meat))
        variants["is_raw=true,meat={}".format(meat)] = {"model": "xercafood:block/doner/" + name}

    # Cooked slicing stages.
    for meat in (4, 3, 2, 1):
        for side in range(LAYER_SIDES[meat]):
            name = "doner_cooked_{}_{}".format(meat, side)
            write_model(out_dir, name, cooked_element(meat, side))
            variants["is_raw=false,meat={},side={}".format(meat, side)] = {
                "model": "xercafood:block/doner/" + name
            }
        # Cover the side values this layer never uses so every state has a variant.
        for side in range(LAYER_SIDES[meat], 4):
            variants["is_raw=false,meat={},side={}".format(meat, side)] = {
                "model": "xercafood:block/doner/doner_cooked_{}_{}".format(meat, LAYER_SIDES[meat] - 1)
            }

    # Meat amounts above 4 only exist while raw; cooking resets to 4. Map the
    # unreachable cooked states to the full box so the model loader stays happy.
    for meat in range(5, RAW_STAGES + 1):
        variants["is_raw=false,meat={}".format(meat)] = {"model": "xercafood:block/doner/doner_cooked_4_0"}

    blockstate_path = os.path.normpath(os.path.join(here, BLOCKSTATE_PATH))
    with open(blockstate_path, "w") as f:
        json.dump({"variants": variants}, f, indent=4)
        f.write("\n")

    print("Generated raw + cooked doner models and updated blockstate")


if __name__ == "__main__":
    main()
