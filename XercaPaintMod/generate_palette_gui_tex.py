from PIL import Image
import os


palette_file = "src/main/resources/assets/xercapaint/textures/gui/palette.png"
img_folder = "dye_item_textures/"

palette = Image.open(palette_file).convert('RGBA')

y_offset = 0
for entry in os.scandir(img_folder):
    im = Image.open(entry.path).convert('RGBA')
    palette.alpha_composite(im, (240, y_offset))
    y_offset += 16

palette.save(palette_file)
