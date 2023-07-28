Settings.MinSimilarity = 0.99

run_button = find("1689430597124.png")
button_center = run_button.getCenter()

config_select = Location(button_center.getX() - 100, button_center.getY())
click(config_select)
click("1689615403851.png")
click("1689615465093.png")
click(run_button)
wait(1)
click(run_button)
click(config_select)
click("1689615403851.png")
click("1689615626698.png")
click(run_button)
