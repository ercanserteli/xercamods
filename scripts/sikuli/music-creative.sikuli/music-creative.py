import math
import os
from javax.sound.sampled import AudioSystem
from java.io import File


def playSound(filename):
    script_path = os.path.dirname(getBundlePath())
    sound_path = os.path.join(script_path, filename)
    clip = AudioSystem.getClip()
    audioInputStream = AudioSystem.getAudioInputStream(File(sound_path))
    clip.open(audioInputStream)
    clip.start()

def testAssert(condition):
    assert(condition)
    playSound("success.wav")

def rclick():
    win = mc.window()
    win.rightClick(atMouse())

def calculateDistance(location1, location2):
    return math.sqrt((location2.getX() - location1.getX())**2 + (location2.getY() - location1.getY())**2)

def slowDragDrop(source_location, target_location, delay=0.01):
    drag(source_location)

    # calculate the steps needed for target
    steps_target = int(calculateDistance(target_location, source_location) / 40)

    # move the mouse to the target in steps
    for i in range(1, steps_target):
        x = source_location.getX() + (target_location.getX() - source_location.getX()) * i / steps_target
        y = source_location.getY() + (target_location.getY() - source_location.getY()) * i / steps_target
        l = Location(int(x), int(y))
        dropAt(l)
        drag(l)

    dropAt(target_location)

def clickw(x, y):
    win = mc.window()
    l = Location(win.getX(), win.getY())
    win.click(l.offset(x, y))

def rclickw(x, y):
    win = mc.window()
    l = Location(win.getX(), win.getY())
    win.rightClick(l.offset(x, y))

def mclickw(x, y):
    win = mc.window()
    l = Location(win.getX(), win.getY())
    hover(l.offset(x, y))
    mouseDown(Button.MIDDLE)
    mouseUp(Button.MIDDLE)

def dragw(x, y):
    win = mc.window()
    l = Location(win.getX(), win.getY())
    win.drag(l.offset(x, y))

def dropw(x, y):
    win = mc.window()
    l = Location(win.getX(), win.getY())
    win.dropAt(l.offset(x, y))

def rdragw(x, y):
    win = mc.window()
    l = Location(win.getX(), win.getY())
    hover(l.offset(x, y))
    mouseDown(Button.RIGHT)

def rdropw(x, y):
    win = mc.window()
    l = Location(win.getX(), win.getY())
    hover(l.offset(x, y))
    mouseUp(Button.RIGHT)

def wheelUp(count=1):
    win = mc.window()
    wheel(win, Button.WHEEL_UP, count)

def wheelDown(count=1):
    win = mc.window()
    wheel(win, Button.WHEEL_DOWN, count)
    
def hoverw(x, y):
    win = mc.window()
    l = Location(win.getX(), win.getY())
    win.hover(l.offset(x, y))


def emptyInventory():
    win.type("e")
    inv = win.exists("1689446179355.png")
    if inv: 
        win.click(inv)
    else:
        assert(exists("1689522340644.png"))
    d = win.find("1689506095783.png")
    keyDown(Key.SHIFT)
    win.click(d)
    keyUp(Key.SHIFT)
    win.click(mcLoc.offset(650, 430))
    assert(exists("1689506211306.png"))


def testItemsInCreative(mc, win, mcLoc):
    emptyInventory()
    
    win.click("1690565858766.png")
    win.click("1689794207237.png")
    
    
    # Get items to inventory
    win.click(mcLoc.offset(275, 185))
    win.click(mcLoc.offset(275, 380))
    win.click(mcLoc.offset(275, 185))
    win.click(mcLoc.offset(275+36, 380))
    win.click(mcLoc.offset(275, 185))
    win.click(mcLoc.offset(275+2*36, 380))
    win.click(mcLoc.offset(275+36, 185))
    win.click(mcLoc.offset(275+3*36, 380))
    
    win.click("1689794298377.png")
    win.click(mcLoc.offset(275+4*36, 380))
    win.click("1689794306513.png")
    win.click(mcLoc.offset(275+5*36, 380))
    win.click("1689794316044.png")
    win.click(mcLoc.offset(275+6*36, 380))
    
    win.type("e")

    # Instrument GUI
    win.type("4")
    rclick()

    # Mouse play
    win.drag(mcLoc.offset(444, 150))
    win.dropAt(mcLoc.offset(617, 150))

    # Keyboard play
    win.type("r")
    win.type("t")
    win.type("y")
    win.type("u")

    win.type("s")
    win.type("s")
    
    win.type("r")
    win.type("t")
    win.type("y")
    win.type("u")

    keyDown("r")
    wait(0.3)
    keyDown("p")
    wait(0.3)
    keyDown("w")
    keyUp("r")
    wait(0.3)
    keyUp("p")
    keyUp("w")
    win.type(Key.ESC)

    # Sheet GUI
    win.type("1")
    rclick()
    testAssert(exists("1689794432100.png"))
    dragw(189, 219)
    dropw(230, 219)
    testAssert(exists("1689794486848.png"))

    clickw(199, 309)
    clickw(205, 312)
    clickw(211, 308)
    clickw(217, 303)
    testAssert(exists("1689794504600.png"))
    
    # Preview
    preview_btn = win.find("1689794517427.png")
    win.click(preview_btn)
    hoverw(294, 67)
    testAssert(exists("1689794548427.png"))
    wait(0.8)

    # Tempo
    clickw(596, 68)
    clickw(596, 68)
    clickw(596, 68)
    clickw(596, 68)
    clickw(596, 68)
    win.click(preview_btn)
    wait(0.5)

    # Measure length
    clickw(718, 90)
    clickw(718, 90)
    clickw(630, 90)
    clickw(630, 90)
    clickw(630, 90)
    clickw(630, 90)
    testAssert(exists("1689794808590.png"))
    
    # Note brush
    clickw(460, 68)
    dragw(225, 238)
    dropw(258, 238)
    hoverw(270, 238)
    testAssert(exists("1689794852402.png"))

    # Sheet volume
    clickw(624, 73)
    win.click(preview_btn)
    wait(0.85)
    clickw(722, 73)
    

    # Keyboard notes
    win.type("qwerty")
    testAssert(exists("1690571157012.png"))

    # Octave wheel
    wheelUp(3)
    testAssert(exists("1689794960532.png"))
    wheelDown(3)
    testAssert(exists("1690571257297.png"))
    
    # Help
    clickw(813, 76)
    testAssert(exists("1690626104673.png"))
    clickw(813, 76)
    testAssert(exists("1689795099653.png"))

    # Exit to save
    win.type(Key.ESC)
    wait(1)
    rclick()
    testAssert(exists("1690571257297.png"))
    clickw(412, 67)

    # Middle click
    mclickw(274, 242)
    testAssert(exists("1689795184606.png"))
    clickw(287, 287)
    clickw(287, 287)
    clickw(287, 287)
    testAssert(exists("1689795231588.png"))
    clickw(400, 287)
    clickw(400, 287)
    clickw(400, 287)
    clickw(400, 287)
    clickw(400, 287)
    clickw(400, 287)
    testAssert(exists("1689795245162.png"))
    clickw(287, 287)
    clickw(287, 287)
    clickw(287, 287)
    
    clickw(285, 307)
    clickw(285, 307)
    testAssert(exists("1689795306689.png"))
    clickw(400, 307)
    clickw(400, 307)
    clickw(400, 307)
    testAssert(exists("1689795580613.png"))
    clickw(298, 333)
    clickw(290, 255)
    clickw(392, 333)
    clickw(290, 255)
    clickw(403, 255)

    # Selection
    rdragw(214, 254)
    rdropw(304, 254)
    win.click(preview_btn)
    wait(0.8)

    # Copy-paste
    win.type("c", KeyModifier.CTRL)
    win.type(Key.RIGHT)
    win.type("v", KeyModifier.CTRL)
    testAssert(exists("1690571480492.png"))

    # Select all, delete with backspace (delete button does not work for some reason)
    win.type("a", KeyModifier.CTRL)
    win.type(Key.BACKSPACE)
    testAssert(exists("1689796061344.png"))
    win.type("z", KeyModifier.CTRL)
    testAssert(exists("1690571480492.png"))

    # Record
    rclickw(398, 264)
    win.click("1689799024685.png")
    hoverw(300, 75)
    wait(2)
    keyDown("t")
    wait(0.5)
    keyDown("r")
    wait(0.5)
    keyDown("e")
    wait(0.5)
    keyUp("r")
    wait(0.5)
    keyUp("t")
    wait(0.5)
    keyUp("e")
    win.click("1689796476256.png")

    # Preview instrument
    win.type(Key.ESC)
    wait(0.5)
    win.type("4")
    win.type("f")
    win.type("1")
    rclick()
    testAssert(exists("1689796993328.png"))
    win.click(preview_btn)
    wait(1)
    win.click(preview_btn)

    # Lock prev instrument
    clickw(333, 76)
    win.type(Key.ESC)
    win.type("4")
    win.type("f")
    win.type("1")
    rclick()
    testAssert(exists("1689797210766.png"))
    win.click(preview_btn)
    wait(1)
    win.click(preview_btn)
    clickw(333, 76)
    win.click(preview_btn)
    wait(1)
    win.click(preview_btn)
    
    win.type(Key.ESC)
    win.type("2")
    rclick()
    testAssert(exists("1689797385621.png"))
    
    clickw(596, 68)
    clickw(596, 68)
    clickw(596, 68)
    clickw(596, 68)
    clickw(596, 68)
    win.type(Key.ESC)
    rclick()
    neighbor_btn = exists("1689797421033.png")
    testAssert(neighbor_btn and exists("1689797429976.png"))
    dragw(217, 168)
    dropw(325, 172)
    win.click(preview_btn)
    wait(2)
    win.click(neighbor_btn)
    hoverw(328, 55)
    testAssert(exists("1689797625523.png") and exists("1689797634099.png"))
    win.type(Key.ESC)
    win.type("1")
    rclick()
    win.click("1689797679952.png")
    testAssert(exists("1690571576218.png"))
    win.click("1689797728271.png")
    testAssert(exists("1690571643086.png") and exists("1690571652749.png"))
    win.click("1689797679952.png")
    win.type("Masterpiece")
    win.click("1689797794031.png")
    rclick()
    testAssert(exists("1689797850593.png"))
    win.click(preview_btn)
    wait(2)
    win.type(Key.ESC)
    
    # Playing with instrument
    win.type("f")
    win.type("4")
    win.type(Key.F5)
    rclick()
    wait(1.5)
    win.type("3")
    wait(0.5)
    win.type("5")
    rclick()
    wait(2)
    win.type(Key.F5)
    win.type(Key.F5)

    # Export/import commands
    old_similarity = Settings.MinSimilarity
    Settings.MinSimilarity = 0.7
    
    win.type("t")
    win.paste("/musicexport test")
    win.type(Key.ENTER)
    testAssert(exists("1727556138753.png"))
    wait(0.5)
    win.type("t")
    win.paste("/musicimport test")
    win.type(Key.ENTER)
    testAssert(exists("1727556195598.png"))
    
    Settings.MinSimilarity = old_similarity


def getItem(win, item, count=1):
    win.type("t")
    win.paste("/give @s {} {}".format(item, count))
    win.type(Key.ENTER)
    wait(0.2)


def craftOffset(mcLoc, x, y):
    return mcLoc.offset(330 + x * 35, 150 + y * 35)


def takeCraftResult(win, mcLoc):
    keyDown(Key.SHIFT)
    win.click(mcLoc.offset(520, 190))
    keyUp(Key.SHIFT)


def putOnCraftGrid(win, mcLoc, item, gridCoords):
    win.click(item)
    for (x, y) in gridCoords:
        win.rightClick(craftOffset(mcLoc, x, y))
    win.click(item)


def testCrafting(mc, win, mcLoc):
    Settings.MinSimilarity = 0.99
    emptyInventory()
    win.type("e")
    
    getItem(win, "crafting_table")
    getItem(win, "paper")
    getItem(win, "ink_sac")
    getItem(win, "feather")
    getItem(win, "stick", 21)
    getItem(win, "oak_planks", 64)
    getItem(win, "spruce_planks", 64)
    getItem(win, "acacia_planks", 64)
    getItem(win, "dark_oak_planks", 64)
    getItem(win, "gold_ingot", 64)
    getItem(win, "gold_nugget", 64)
    getItem(win, "iron_ingot", 64)
    getItem(win, "iron_nugget", 64)
    getItem(win, "string", 64)
    getItem(win, "redstone", 64)
    getItem(win, "leather", 3)
    getItem(win, "clock")
    getItem(win, "note_block")
    
    popup("Place and use the crafting table.")
    testAssert(exists("1689507699969.png"))
    stick = win.find("1689507809001.png")
    planks = win.find("1689509631514.png")
    gold = win.find("1727550792351.png")
    gold_nugget = win.find("1727551519366.png")
    redstone = win.find("1727550832057.png")
    string = win.find("1727551526354.png")
    iron = win.find("1727551534568.png")
    iron_nugget = win.find("1727551541169.png")
    spruce = win.find("1727551547811.png")
    acacia = win.find("1727551554482.png")
    leather = win.find("1727553237408.png")
    dark_oak = win.find("1727553244730.png")

    # Music sheet
    keyDown(Key.SHIFT)
    win.click("1689507973422.png")
    win.click("1727550354098.png")
    win.click("1727550359912.png")
    keyUp(Key.SHIFT)
    testAssert(exists("1727550398237.png"))
    takeCraftResult(win, mcLoc)
    
    # Music box
    win.click(planks)
    win.rightClick(craftOffset(mcLoc, 0, 2))
    win.rightClick(craftOffset(mcLoc, 0, 1))
    win.rightClick(craftOffset(mcLoc, 0, 0))
    win.rightClick(craftOffset(mcLoc, 1, 0))
    win.rightClick(craftOffset(mcLoc, 2, 0))
    win.rightClick(craftOffset(mcLoc, 2, 1))
    win.rightClick(craftOffset(mcLoc, 2, 2))
    win.click(planks)
    win.click(gold)
    win.rightClick(craftOffset(mcLoc, 1, 1))
    win.click(gold)
    win.click(redstone)
    win.rightClick(craftOffset(mcLoc, 1, 2))
    win.click(redstone)
    testAssert(exists("1727551047664.png"))
    takeCraftResult(win, mcLoc)

    # Metronome
    keyDown(Key.SHIFT)
    win.click("1727551247900.png")
    win.click("1727551256317.png")
    keyUp(Key.SHIFT)
    testAssert(exists("1727551270425.png"))
    takeCraftResult(win, mcLoc)

    # Instruments
    # Guitar
    putOnCraftGrid(win, mcLoc, stick, [(1, 0)])
    putOnCraftGrid(win, mcLoc, planks, [(0, 1), (1, 2), (2, 1)])
    putOnCraftGrid(win, mcLoc, string, [(1, 1)])
    testAssert(exists("1727551731271.png"))
    takeCraftResult(win, mcLoc)
    # Drum
    putOnCraftGrid(win, mcLoc, leather, [(1, 1)])
    putOnCraftGrid(win, mcLoc, planks, [(0, 1), (1, 0), (1, 2), (2, 1)])
    testAssert(exists("1727551737680.png"))
    takeCraftResult(win, mcLoc)
    # Chimes
    putOnCraftGrid(win, mcLoc, stick, [(0, 0), (1, 0), (2, 0)])
    putOnCraftGrid(win, mcLoc, gold_nugget, [(0, 1), (2, 1)])
    testAssert(exists("1727554486942.png"))
    takeCraftResult(win, mcLoc)
    # Piano
    putOnCraftGrid(win, mcLoc, planks, [(0, 0), (1, 0), (2, 0), (0, 2), (1, 2), (2, 2)])
    putOnCraftGrid(win, mcLoc, string, [(0, 1), (2, 1)])
    putOnCraftGrid(win, mcLoc, iron_nugget, [(1, 1)])
    testAssert(exists("1727554606665.png"))
    takeCraftResult(win, mcLoc)
    # Violin
    putOnCraftGrid(win, mcLoc, stick, [(1, 0)])
    putOnCraftGrid(win, mcLoc, string, [(1, 1)])
    putOnCraftGrid(win, mcLoc, spruce, [(0, 1), (1, 2), (2, 1)])
    testAssert(exists("1727554722520.png"))
    takeCraftResult(win, mcLoc)
    # Bass guitar
    putOnCraftGrid(win, mcLoc, stick, [(1, 0)])
    putOnCraftGrid(win, mcLoc, string, [(1, 1)])
    putOnCraftGrid(win, mcLoc, spruce, [(0, 1), (1, 2), (2, 1)])
    putOnCraftGrid(win, mcLoc, redstone, [(0, 2), (2, 2)])
    testAssert(exists("1727554782382.png"))
    takeCraftResult(win, mcLoc)
    # Lyre
    putOnCraftGrid(win, mcLoc, stick, [(0, 1), (1, 2), (2, 1)])
    putOnCraftGrid(win, mcLoc, string, [(1, 1)])
    testAssert(exists("1727554795348.png"))
    takeCraftResult(win, mcLoc)
    # Cymbal
    putOnCraftGrid(win, mcLoc, gold_nugget, [(1, 0), (0, 1), (2, 1), (1, 2)])
    putOnCraftGrid(win, mcLoc, iron_nugget, [(1, 1)])
    testAssert(exists("1727554808563.png"))
    takeCraftResult(win, mcLoc)
    # Xylophone
    putOnCraftGrid(win, mcLoc, stick, [(0, 0), (1, 0), (2, 0)])
    putOnCraftGrid(win, mcLoc, planks, [(0, 1), (1, 1), (2, 1)])
    testAssert(exists("1727554821367.png"))
    takeCraftResult(win, mcLoc)
    # Flute
    putOnCraftGrid(win, mcLoc, stick, [(0, 1), (1, 1), (2, 1)])
    testAssert(exists("1727554833849.png"))
    takeCraftResult(win, mcLoc)
    # Cello
    putOnCraftGrid(win, mcLoc, stick, [(1, 0)])
    putOnCraftGrid(win, mcLoc, string, [(1, 1)])
    putOnCraftGrid(win, mcLoc, dark_oak, [(0, 1), (1, 2), (2, 1)])
    testAssert(exists("1727554847348.png"))
    takeCraftResult(win, mcLoc)
    # Oboe
    putOnCraftGrid(win, mcLoc, stick, [(0, 1), (1, 1), (2, 1)])
    putOnCraftGrid(win, mcLoc, iron_nugget, [(0, 0), (1, 0), (2, 0)])
    testAssert(exists("1727554862380.png"))
    takeCraftResult(win, mcLoc)
    # Banjo
    putOnCraftGrid(win, mcLoc, stick, [(1, 0)])
    putOnCraftGrid(win, mcLoc, string, [(1, 1)])
    putOnCraftGrid(win, mcLoc, acacia, [(0, 1), (1, 2), (2, 1)])
    testAssert(exists("1727554879071.png"))
    takeCraftResult(win, mcLoc)
    # Drum kit
    putOnCraftGrid(win, mcLoc, gold_nugget, [(1, 0), (0, 1), (2, 1), (1, 2)])
    putOnCraftGrid(win, mcLoc, iron_nugget, [(1, 1)])
    takeCraftResult(win, mcLoc)
    putOnCraftGrid(win, mcLoc, leather, [(1, 1)])
    putOnCraftGrid(win, mcLoc, planks, [(0, 1), (1, 0), (1, 2), (2, 1)])
    takeCraftResult(win, mcLoc)
    putOnCraftGrid(win, mcLoc, leather, [(1, 1)])
    putOnCraftGrid(win, mcLoc, planks, [(0, 1), (1, 0), (1, 2), (2, 1)])
    takeCraftResult(win, mcLoc)
    drum = win.find("1727553340124.png")
    cymbal = win.find("1727553355134.png")
    putOnCraftGrid(win, mcLoc, drum, [(0, 2), (1, 2), (2, 2)])
    putOnCraftGrid(win, mcLoc, cymbal, [(0, 1), (2, 1)])
    testAssert(exists("1727551812353.png"))
    takeCraftResult(win, mcLoc)
    # Sansula
    putOnCraftGrid(win, mcLoc, planks, [(0, 1), (1, 1), (2, 1)])
    putOnCraftGrid(win, mcLoc, iron_nugget, [(0, 0), (1, 0), (2, 0)])
    testAssert(exists("1727554909505.png"))
    takeCraftResult(win, mcLoc)
    # Saxophone
    putOnCraftGrid(win, mcLoc, gold_nugget, [(0, 1), (1, 2), (2, 0), (2, 1)])
    testAssert(exists("1727554921813.png"))
    takeCraftResult(win, mcLoc)
    # Redstone guitar
    putOnCraftGrid(win, mcLoc, stick, [(1, 0)])
    putOnCraftGrid(win, mcLoc, string, [(1, 1)])
    putOnCraftGrid(win, mcLoc, planks, [(0, 1), (1, 2), (2, 1)])
    putOnCraftGrid(win, mcLoc, redstone, [(0, 2), (2, 2)])
    testAssert(exists("1727554936465.png"))
    takeCraftResult(win, mcLoc)
    # French horn
    putOnCraftGrid(win, mcLoc, gold_nugget, [(0, 1), (1, 2), (2, 0), (2, 1), (1, 1), (0, 2)])
    testAssert(exists("1727551832600.png"))
    takeCraftResult(win, mcLoc)

    # Copying
    win.type("e")
    wait(0.5)
    win.type("t")
    win.paste("/musicimport test")
    win.type(Key.ENTER)
    rclick()
    win.click(mcLoc.offset(400, 405))
    win.click(craftOffset(mcLoc, 0, 1))
    win.click(mcLoc.offset(500, 280))
    win.click(craftOffset(mcLoc, 2, 1))
    win.click(mcLoc.offset(500, 280))
    testAssert(exists("1727556399606.png"))
    takeCraftResult(win, mcLoc)
    win.type(Key.ESC)


Settings.MinSimilarity = 0.98
mc = switchApp("Minecraft")
mc.focus()
win = mc.window()
mcLoc = Location(win.getX(), win.getY())

button = win.exists("1689798450998.png", 0.1)
if button:
    win.click(button)
inv = win.exists("1689798484855.png", 0.1)
if inv:
    win.type("e")
wait(0.5)

testItemsInCreative(mc, win, mcLoc)
testCrafting(mc, win, mcLoc)