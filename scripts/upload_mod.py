import argparse
import json
import os
import requests

# Common synonyms / normalization for CurseForge "game version" names
CF_VERSION_SYNONYMS = {
    "forge": "Forge",
    "fabric": "Fabric",
    "fabric-loader": "Fabric",
    "neoforge": "NeoForge",
    "neo-forge": "NeoForge",
}


def get_game_versions(api_token):
    url = "https://minecraft.curseforge.com/api/game/versions"
    headers = {"X-Api-Token": api_token}
    resp = requests.get(url, headers=headers)
    if resp.status_code != 200:
        raise Exception(f"GET {url} failed with status code {resp.status_code}: {resp.text}")
    return resp.json()


def get_game_version_id(version_name, api_token, cached_versions=None):
    versions = cached_versions if cached_versions is not None else get_game_versions(api_token)

    # Normalize via synonyms
    key = (version_name or "").strip()
    normalized = CF_VERSION_SYNONYMS.get(key.lower(), key)

    # Try exact match first
    for v in versions:
        if v.get("name") == normalized:
            return v["id"]

    # Then case-insensitive match
    norm_lower = normalized.lower()
    for v in versions:
        name = (v.get("name") or "").lower()
        if name == norm_lower:
            return v["id"]

    raise Exception(f"No CurseForge game version found with name '{version_name}' (normalized to '{normalized}')")


def build_curseforge_game_version_ids(api_token, mc_versions, loaders):
    versions = get_game_versions(api_token)

    ids = []
    # Minecraft versions (e.g. 1.20.1)
    for v in mc_versions:
        ids.append(get_game_version_id(v, api_token, cached_versions=versions))

    # Loaders (e.g. forge / fabric)
    for l in (loaders or []):
        ids.append(get_game_version_id(l, api_token, cached_versions=versions))

    # de-dupe while preserving order
    seen = set()
    out = []
    for i in ids:
        if i not in seen:
            seen.add(i)
            out.append(i)
    return out


def upload_mod_to_curseforge(
        api_token,
        project_id,
        file_path,
        changelog,
        changelog_type,
        display_name,
        game_versions,
        release_type,
        relations,
        is_dry,
):
    headers = {"X-Api-Token": api_token}
    url = f"https://minecraft.curseforge.com/api/projects/{project_id}/upload-file"

    metadata = {
        "changelog": changelog,
        "changelogType": changelog_type,
        "displayName": display_name,
        "gameVersions": game_versions,
        "releaseType": release_type,
    }

    if relations:
        metadata["relations"] = {"projects": relations}

    data = {"metadata": (None, json.dumps(metadata), "application/json")}

    if is_dry:
        print("Dry run, not uploading file.")
        print(f"URL: {url}")
        print(f"Headers: {headers}")
        print(f"Metadata: {metadata}")
        return

    with open(file_path, "rb") as f:
        files = {"file": f}
        response = requests.post(url, headers=headers, files={**data, **files})

    if response.status_code == 200:
        print(f'Success! File uploaded. File id: {response.json()["id"]}')
    else:
        print(f"Failed to upload file. Status code: {response.status_code}, message: {response.text}")


def upload_mod_to_modrinth(
        api_token,
        project_id,
        file_path,
        name,
        version_number,
        version_type,
        game_versions,
        changelog,
        loaders,
        is_featured,
        status,
        is_dry,
):
    headers = {"Authorization": api_token}
    url = "https://api.modrinth.com/v2/version"

    metadata = {
        "project_id": project_id,
        "name": name,
        "version_number": version_number,
        "changelog": changelog,
        "dependencies": [],
        "game_versions": game_versions,
        "version_type": version_type,
        "loaders": loaders,
        "featured": is_featured,
        "status": status,
        "file_parts": [os.path.basename(file_path)],
        "primary_file": os.path.basename(file_path),
    }

    if is_dry:
        print("Dry run, not uploading file.")
        print(f"URL: {url}")
        print(f"Headers: {headers}")
        print(f"Metadata: {metadata}")
        return

    with open(file_path, "rb") as f:
        data = {
            "data": (None, json.dumps(metadata), "application/json"),
            "file": (os.path.basename(file_path), f),
        }
        response = requests.post(url, headers=headers, files=data)

    if response.status_code == 200:
        print(f'Success! File uploaded. Version id: {response.json()["id"]}')
    else:
        print(f"Failed to upload file. Status code: {response.status_code}, message: {response.text}")


def main():
    parser = argparse.ArgumentParser(description="Upload a Minecraft mod to CurseForge.")

    parser.add_argument("--curseforge-api-token", help="Your CurseForge API token")
    parser.add_argument("--modrinth-api-token", help="Your Modrinth API token")
    parser.add_argument("--project-id", required=True, help="Your CurseForge project ID")
    parser.add_argument("--file-path", required=True, help="Path to the mod file")
    parser.add_argument("--changelog", required=True, help="Changelog for this version")
    parser.add_argument("--changelog-type", choices=["text", "html", "markdown"], default="text",
                        help="Type of the changelog")
    parser.add_argument("--display-name", required=True, help="Display name of the mod")
    parser.add_argument("--game-versions", nargs="+", type=str, required=True,
                        help="List of supported game versions (e.g. 1.20.1)")
    parser.add_argument("--release-type", choices=["alpha", "beta", "release"], required=True, help="Release type")
    parser.add_argument("--relations", nargs="+", type=json.loads, required=False,
                        help='List of related projects. Use format: \'{"slug": "mantle", "type": ["embeddedLibrary"]}\'')
    parser.add_argument("--dry", action="store_true",
                        help="Dry run, don't actually upload the file. Useful for testing.")
    parser.add_argument("--loaders", nargs="+", default=["forge"],
                        help="The mod loaders that this version supports (curseforge: forge/fabric/neoforge)")
    parser.add_argument("--featured", action="store_true", help="Whether the version is featured or not")
    parser.add_argument("--status", default="listed",
                        choices=["listed", "archived", "draft", "unlisted", "scheduled", "unknown"],
                        help="The status of the version")

    args = parser.parse_args()

    if args.curseforge_api_token:
        mod_name_to_id = {"xercamod": 341575, "music": 341448, "paint": 350727}
        cf_project_id = args.project_id
        if cf_project_id in mod_name_to_id:
            cf_project_id = mod_name_to_id[cf_project_id]

        # include loader IDs in gameVersions
        cf_game_versions = build_curseforge_game_version_ids(
            args.curseforge_api_token,
            mc_versions=args.game_versions,
            loaders=args.loaders,
        )

        upload_mod_to_curseforge(
            args.curseforge_api_token,
            cf_project_id,
            args.file_path,
            args.changelog,
            args.changelog_type,
            args.display_name,
            cf_game_versions,
            args.release_type,
            args.relations,
            args.dry
        )

    if args.modrinth_api_token:
        mod_name_to_id = {"xercamod": "Z110yRfL", "music": "qQpWCN75", "paint": "YOs4tZea"}
        mod_name_to_title = {"xercamod": "XercaMod", "music": "Music Maker Mod", "paint": "Joy of Painting"}

        title = mod_name_to_title[args.project_id]
        if args.project_id in mod_name_to_id:
            args.project_id = mod_name_to_id[args.project_id]

        version = "-".join(os.path.splitext(os.path.basename(args.file_path))[0].split("-")[1:])
        if version.startswith("fabric-"):
            version = version[7:]
        if version.startswith("forge-"):
            version = version[6:]
        version_name = f"{title} {version}"

        upload_mod_to_modrinth(
            args.modrinth_api_token,
            args.project_id,
            args.file_path,
            version_name,
            version,
            args.release_type,
            args.game_versions,
            args.changelog,
            args.loaders,
            args.featured,
            args.status,
            args.dry
        )


if __name__ == "__main__":
    main()
