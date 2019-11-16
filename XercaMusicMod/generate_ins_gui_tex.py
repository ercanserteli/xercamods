from PIL import Image

instruments = [
    "guitar",
    "lyre",
    "banjo",
    "drum",
    "cymbal",
    "drum_kit",
    "xylophone",
    "tubular_bell",
    "sansula",
    "violin",
    "cello",
    "flute",
    "saxophone",
    "god",
]

output_img = "src/main/resources/assets/xercamusic/textures/gui/instruments.png"
lock_img = "src/main/resources/assets/xercamusic/textures/gui/transparent_lock.png"
img_files = ["src/main/resources/assets/xercamusic/textures/item/instruments/" + ins + ".png" for ins in instruments]

images = list(map(Image.open, img_files))
hover_bg = Image.new('RGBA', (16, 16), (81, 160, 213, 128))
combined = Image.new('RGBA', (256, 256), (0, 0, 0, 0))

images = [Image.new("RGBA", (16, 16), (255, 255, 255, 0))] + images

x_offset = 0
for im in images:
    combined.paste(im, (x_offset, 0))
    combined.paste(im, (x_offset, 16), im)
    combined.alpha_composite(hover_bg, (x_offset, 16))
    x_offset += 16

combined.paste(Image.open(lock_img), (1, 240))

combined.save(output_img)
