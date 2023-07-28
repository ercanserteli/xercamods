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

def rclick(win):
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
    win.click("1689431143513.png")
    
    # Get items to inventory
    for i in range(7):
        x = 275 + i*36
        win.click(mcLoc.offset(x, 185))
        win.click(mcLoc.offset(x, 380))
    
    win.type("e")
    
    # Empty palette
    win.type("1")
    rclick(win)
    testAssert(exists("1689434333796.png") and exists("1689434326126.png") and exists("1689434315850.png"))
    win.type(Key.ESC)
    
    # Full palette
    win.type("2")
    rclick(win)
    testAssert(exists("1689505674262.png") and exists("1689505686642.png"))
    
    # Dark gray to first slot
    win.drag("1689433643444.png")
    win.dropAt(mcLoc.offset(540, 240))
    testAssert(exists("1689505731885.png"))
    
    # Orange to right slot 3
    win.drag("1689433651887.png")
    win.dropAt(mcLoc.offset(565, 305))
    
    # Mix light blue with orange
    win.drag("1689433689628.png")
    win.dropAt(mcLoc.offset(565, 305))
    testAssert(exists("1689505764995.png"))
    
    # Delete dark gray
    win.drag("1689433758136.png")
    win.dropAt(mcLoc.offset(540, 240))
    testAssert(exists("1689505792474.png")) 
    win.type(Key.ESC)
    wait(0.5)
    
    # Check if saved properly
    rclick(win)
    testAssert(exists("1689434101942.png"))
    win.type(Key.ESC)
    
    # Empty canvas
    win.type("3")
    rclick(win)
    testAssert(exists("1689434429864.png"))
    win.type(Key.ESC)
    
    # Empty long canvas
    win.type("4")
    rclick(win)
    testAssert(exists("1689434485771.png"))
    win.type(Key.ESC)
    
    # Empty tall canvas
    win.type("5")
    rclick(win)
    testAssert(exists("1689434515287.png"))
    win.type(Key.ESC)
    
    # Empty large canvas
    win.type("6")
    rclick(win)
    testAssert(exists("1689434530128.png"))
    win.type(Key.ESC)




def testDrawOnCanvas(mc, win, mcLoc):
    # Drawing on canvas
    # -----------------
    win.type("2")
    win.type("f")
    # for i in range(4):
    win.type(str(3))
    rclick(win)
    testAssert(exists("1689438634012.png"))
    
    # Draw a line with red
    win.click(mcLoc.offset(810, 128))
    win.click(mcLoc.offset(115, 405))
    slowDragDrop(mcLoc.offset(524, 160), mcLoc.offset(762, 360))
    win.click(mcLoc.offset(400, 440))
    testAssert(exists("1689521777237.png"))
    
    # Draw a half-opaque line with mixed color
    win.click(mcLoc.offset(357, 305))
    win.click(mcLoc.offset(810, 185))
    slowDragDrop(mcLoc.offset(745, 160), mcLoc.offset(524, 360))
    win.click(mcLoc.offset(400, 440))
    testAssert(exists("1689521940375.png"))
    
    # Draw with green big brush
    win.click(mcLoc.offset(805, 337))
    win.click(mcLoc.offset(110, 350))
    win.click(mcLoc.offset(625, 260))
    win.click(mcLoc.offset(400, 440))
    testAssert(exists("1689447745899.png"))
    
    # Exit and come back to check if it saved
    win.type(Key.ESC)
    wait(1)
    rclick(win)
    testAssert(exists("1689447745899.png"))
    
    # Signing (and canceling signing)
    win.click("1689438634012.png")
    win.click("1689502201220.png")
    testAssert(exists("1689438634012.png"))
    win.click("1689438634012.png")
    win.type("Masterpiece")
    win.click("1689442068756.png")
    rclick(win)
    win.mouseMove(mcLoc.offset(100, 100))
    testAssert(exists("1689442696256.png") and exists("1689447745899.png"))
    win.type(Key.ESC)

    # Export/import commands
    old_similarity = Settings.MinSimilarity
    Settings.MinSimilarity = 0.7
    
    win.type("t")
    win.paste("/paintexport test")
    win.type(Key.ENTER)
    testAssert(exists("1689521099196.png"))
    wait(0.5)
    win.type("t")
    win.paste("/paintimport test")
    win.type(Key.ENTER)
    testAssert(exists("1689521073614.png"))
    
    Settings.MinSimilarity = old_similarity


def testCrafting(mc, win, mcLoc):
    Settings.MinSimilarity = 0.99
    emptyInventory()
    win.type("e")
    win.type("t")
    win.paste("/give @s crafting_table")
    win.type(Key.ENTER)
    wait(0.2)
    win.type("t")
    win.paste("/give @s stick 64")
    win.type(Key.ENTER)
    wait(0.2)
    win.type("t")
    win.paste("/give @s paper 8")
    win.type(Key.ENTER)
    wait(0.2)
    win.type("t")
    win.paste("/give @s oak_planks 3")
    win.type(Key.ENTER)
    wait(0.2)
    
    dye_colors = [
        'black',
        'red',
        'green',
        'brown',
        'blue',
        'purple',
        'cyan',
        'light_gray',
        'gray',
        'pink',
        'lime',
        'yellow',
        'light_blue',
        'magenta',
        'orange',
        'white'
    ]
    for c in dye_colors:
        win.type("t")
        win.paste("/give @s {}_dye".format(c))
        win.type(Key.ENTER)
        wait(0.2)
    popup("Place and use the crafting table.")
    testAssert(exists("1689507699969.png"))
    stick = win.find("1689507809001.png")

    # Easel
    win.click(stick)
    win.rightClick(mcLoc.offset(365, 150))
    win.rightClick(mcLoc.offset(365, 185))
    win.rightClick(mcLoc.offset(330, 225))
    win.rightClick(mcLoc.offset(400, 225))
    win.click(stick)
    easel = win.exists("1689510546908.png")
    testAssert(easel)
    print(easel)
    win.doubleClick(stick)
    win.click(stick)
    win.click(mcLoc.offset(200, 150))

    # Canvas
    win.click("1689507973422.png")
    win.click(mcLoc.offset(370, 188))
    
    win.click(stick)
    win.click(mcLoc.offset(330, 150))
    
    win.rightClick(mcLoc.offset(330, 150))
    win.click(mcLoc.offset(365, 150))
    
    win.rightClick(mcLoc.offset(365, 150))
    win.click(mcLoc.offset(400, 150))
    
    win.rightClick(mcLoc.offset(400, 150))
    win.click(mcLoc.offset(400, 180))
    
    win.rightClick(mcLoc.offset(365, 150))
    win.click(mcLoc.offset(400, 230))
    
    win.rightClick(mcLoc.offset(330, 150))
    win.click(mcLoc.offset(330, 180))
    
    win.rightClick(mcLoc.offset(330, 180))
    win.click(mcLoc.offset(330, 225))
    
    win.rightClick(mcLoc.offset(330, 150))
    win.click(mcLoc.offset(370, 225))

    res = exists("1689508961650.png")
    testAssert(res)
    
    keyDown(Key.SHIFT)
    win.click(res)
    keyUp(Key.SHIFT)

    # Long canvas
    win.click(mcLoc.offset(400, 360))
    win.click(mcLoc.offset(330, 150))
    win.click(mcLoc.offset(435, 360))
    win.click(mcLoc.offset(365, 150))
    win.click(mcLoc.offset(200, 150))
    testAssert(exists("1689509312610.png"))

    # Tall canvas
    win.click(mcLoc.offset(365, 150))
    win.click(mcLoc.offset(330, 185))
    win.click(mcLoc.offset(200, 150))
    testAssert(exists("1689509355171.png"))

    # Large canvas
    win.click(mcLoc.offset(470, 360))
    win.click(mcLoc.offset(365, 150))
    win.click(mcLoc.offset(505, 360))
    win.click(mcLoc.offset(365, 185))
    win.click(mcLoc.offset(200, 150))
    res = exists("1689509454876.png")
    testAssert(res)
    keyDown(Key.SHIFT)
    win.click(res)
    keyUp(Key.SHIFT)

    # Palette
    win.click("1689509631514.png")
    win.drag(mcLoc.offset(330, 230))
    win.dropAt(mcLoc.offset(400, 230))
    win.click("1689509695985.png")
    win.click(mcLoc.offset(365, 195))
    testAssert("1689509757648.png")
    win.click("1689509770105.png")
    win.click(mcLoc.offset(400, 195))
    win.click("1689509804017.png")
    win.click(mcLoc.offset(330, 195))
    res = exists("1689509827187.png")
    testAssert(res)

    # Filling palette
    win.click(res)
    win.click(mcLoc.offset(365, 150))
    keyDown(Key.SHIFT)
    win.click("1689509949859.png")
    win.click("1689509975222.png")
    win.click("1689509980008.png")
    win.click("1689509986914.png")
    win.click("1689509993523.png")
    win.click("1689509998973.png")
    win.click("1689510004133.png")
    win.click("1689510008810.png")
    keyUp(Key.SHIFT)
    win.click(mcLoc.offset(200, 150))
    res = exists("1689510083157.png")
    testAssert(res)
    win.click(res)
    win.click(mcLoc.offset(365, 150))
    keyDown(Key.SHIFT)
    win.click("1689510013693.png")
    win.click("1689510020061.png")
    win.click("1689510025314.png")
    win.click("1689510031311.png")
    win.click("1689510037022.png")
    keyUp(Key.SHIFT)
    res = exists("1689510185482.png")
    testAssert(res)
    keyDown(Key.SHIFT)
    win.click(res)
    keyUp(Key.SHIFT)

    # Copying
    win.type("e")
    wait(0.5)
    win.type("t")
    win.paste("/paintimport test")
    win.type(Key.ENTER)
    rclick(win)
    win.click(mcLoc.offset(400, 405))
    win.click(mcLoc.offset(330, 185))
    win.click("1689522685857.png")
    win.click(mcLoc.offset(400, 185))
    win.click(mcLoc.offset(150, 200))
    testAssert(exists("1689522791067.png"))
    keyDown(Key.SHIFT)
    win.click(mcLoc.offset(520, 190))
    keyUp(Key.SHIFT)
    win.type(Key.ESC)
    
    


mc = switchApp("Minecraft")
mc.focus()
win = mc.window()
mcLoc = Location(win.getX(), win.getY())

button = win.exists("1689430960944.png", 0.1)
if button:
    win.click(button)
inv = win.exists("1689446179355.png", 0.1)
if inv:
    win.type("e")


testItemsInCreative(mc, win, mcLoc)
testDrawOnCanvas(mc, win, mcLoc)
testCrafting(mc, win, mcLoc)