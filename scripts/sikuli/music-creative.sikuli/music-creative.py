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
    
    win.click("1689794186807.png")
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
    clickw(718, 73)
    

    # Keyboard notes
    win.type("qwerty")
    testAssert(exists("1689794946862.png"))

    # Octave wheel
    wheelUp(3)
    testAssert(exists("1689794960532.png"))
    wheelDown(3)
    testAssert(exists("1689794970931.png"))
    
    # Help
    clickw(813, 76)
    testAssert(exists("1689795047937.png"))
    clickw(813, 76)
    testAssert(exists("1689795099653.png"))

    # Exit to save
    win.type(Key.ESC)
    wait(1)
    rclick()
    testAssert(exists("1689795136779.png"))
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
    testAssert(exists("1689796028262.png"))

    # Select all, delete with backspace (delete button does not work for some reason)
    win.type("a", KeyModifier.CTRL)
    win.type(Key.BACKSPACE)
    testAssert(exists("1689796061344.png"))
    win.type("z", KeyModifier.CTRL)
    testAssert(exists("1689796028262.png"))

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
    testAssert(exists("1689797713350.png"))
    win.click("1689797728271.png")
    testAssert(exists("1689797743619.png"))
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
    wait(2.5)
    win.type(Key.F5)
    win.type(Key.F5)
    


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