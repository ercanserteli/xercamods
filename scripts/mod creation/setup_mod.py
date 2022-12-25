from sys import argv
import os
from distutils.dir_util import copy_tree
from utils import get_mod_id

mod_id = ""


def main():
    mod_id = get_mod_id()
    if not mod_id:
        mod_id = input("Give me the mod id:")
    
    with open(f'src\\main\\java\\xerca\\{mod_id}\\Mod.java', 'r+', encoding="utf8") as f:
        file_string = f.read()

        # Definitions
        line_find = "implements ModInitializer {"
        i = file_string.find(line_find) + len(line_find) + 2
        line = """
    public static final String modId = "{}";
    public static final Logger LOGGER = LogManager.getLogger(modId);

    // Packet Definitions


    // Block Definitions


    // Item Definitions


    // EntityType Definitions


    // Sound Definitions


""".format(mod_id)
        file_string = file_string[:i] + line + file_string[i:]

        # Registeries
        line_find = "public void onInitialize() {"
        i = file_string.find(line_find) + len(line_find) + 2
        line = """
        // Block Registration


        // Item Registration


        // Sound Registration


"""
        file_string = file_string[:i] + line + file_string[i:]

        f.seek(0)
        f.write(file_string)
        f.truncate()

    with open(f'src\\main\\java\\xerca\\{mod_id}\\client\\ModClient.java', 'r+', encoding="utf8") as f:
        file_string = f.read()

        # Registeries
        line_find = "onInitializeClient() {"
        i = file_string.find(line_find) + len(line_find) + 2
        line = """
        // Entity Renderer Registration

"""
        file_string = file_string[:i] + line + file_string[i:]

        f.seek(0)
        f.write(file_string)
        f.truncate()

    # Copy resources
    copy_tree("..\\Base mod\\resources", "src\\main\\resources")
    os.rename("src\\main\\resources\\assets\\modid", "src\\main\\resources\\assets\\" + mod_id)
    os.rename("src\\main\\resources\\data\\modid", "src\\main\\resources\\data\\" + mod_id)


if __name__ == "__main__":
    main()
