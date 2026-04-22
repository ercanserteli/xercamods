import subprocess
import sys
from pathlib import Path


def main():
    scripts_dir = Path(__file__).resolve().parent
    recipe_scripts = sorted(
        script for script in scripts_dir.glob("recipes_xerca*.py") if script.name != "recipes_all.py"
    )

    if not recipe_scripts:
        print("No recipe scripts found.")
        return 0

    for script in recipe_scripts:
        print(f"Running {script.name}...")
        result = subprocess.run([sys.executable, script.name], cwd=scripts_dir)
        if result.returncode != 0:
            print(f"Failed: {script.name} (exit code {result.returncode})")
            return result.returncode

    print(f"Done. Ran {len(recipe_scripts)} recipe scripts.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
