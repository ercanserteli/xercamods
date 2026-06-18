from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
MOD_DIR_PREFIX = "Xerca"

TEMPLATE = """@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@FieldsAreNonnullByDefault
package {package_name};

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
"""


def iter_java_roots():
    for mod_dir in sorted(REPO_ROOT.iterdir()):
        if not mod_dir.is_dir() or not mod_dir.name.startswith(MOD_DIR_PREFIX):
            continue

        src_dir = mod_dir / "src" / "main"
        if not src_dir.is_dir():
            continue

        for java_root in sorted(src_dir.glob("java")):
            if java_root.is_dir():
                yield java_root


def directory_has_java_sources(directory: Path) -> bool:
    return any(
        path.suffix == ".java" and path.name != "package-info.java"
        for path in directory.iterdir()
    )


def create_package_info(java_root: Path, directory: Path) -> bool:
    package_info = directory / "package-info.java"
    if package_info.exists():
        return False

    package_name = ".".join(directory.relative_to(java_root).parts)
    if not package_name:
        return False

    package_info.write_text(TEMPLATE.format(package_name=package_name), encoding="utf-8")
    return True


def main():
    print(f"Starting with REPO_ROOT = {REPO_ROOT}")
    created = 0

    for java_root in iter_java_roots():
        for directory in sorted(path for path in java_root.rglob("*") if path.is_dir()):
            if not directory_has_java_sources(directory):
                continue

            if create_package_info(java_root, directory):
                created += 1
                print(directory.relative_to(REPO_ROOT))

    print(f"Created {created} package-info.java files.")


if __name__ == "__main__":
    main()
