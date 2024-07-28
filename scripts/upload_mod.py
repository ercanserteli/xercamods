import requests
import json
import argparse
import os


def get_game_version_id(version_name, api_token):
    url = 'https://minecraft.curseforge.com/api/game/versions'
    headers = {
        'X-Api-Token': api_token,
    }

    response = requests.get(url, headers=headers)

    # Raise an exception if the GET request was not successful
    if response.status_code != 200:
        raise Exception(f"GET request failed with status code {response.status_code}")

    # Parse the JSON response
    versions = response.json()

    # Find the version object whose name matches the command-line argument
    for version in versions:
        if version['name'] == version_name:
            return version['id']

    # If no version was found that matches the name, raise an exception
    raise Exception(f"No version found with name {version_name}")


def upload_mod_to_curseforge(api_token, project_id, file_path, changelog, changelog_type, display_name, game_versions, release_type, relations, is_dry):
    # Header
    headers = {
        'X-Api-Token': api_token
    }

    # URL for project upload
    url = f'https://minecraft.curseforge.com/api/projects/{project_id}/upload-file'

    # Metadata for the mod
    metadata = {
        "changelog": changelog,
        "changelogType": changelog_type,
        "displayName": display_name,
        "gameVersions": game_versions,
        "releaseType": release_type
    }

    if relations:
        metadata["relations"] = {
            "projects": relations
        }

    # Prepare data
    data = {
        'metadata': (None, json.dumps(metadata), 'application/json')
    }
    files = {
        'file': open(file_path, 'rb')
    }

    if not is_dry:
        # Make the POST request
        response = requests.post(url, headers=headers, files={**data, **files})

        # Check the response
        if response.status_code == 200:
            print(f'Success! File uploaded. File id: {response.json()["id"]}')
        else:
            print(f'Failed to upload file. Status code: {response.status_code}, message: {response.text}')
    else:
        print('Dry run, not uploading file.')
        print(f'URL: {url}')
        print(f'Headers: {headers}')
        print(f'Data: {data}')
        print(f'Files: {files}')
        print(f'Metadata: {metadata}')


def upload_mod_to_modrinth(api_token, project_id, file_path, name, version_number, version_type, game_versions, changelog, loaders, is_featured, status, is_dry):
    # Header
    headers = {
        'Authorization': api_token
    }

    # URL for project version creation
    url = f'https://api.modrinth.com/v2/version'

    # Metadata for the mod
    metadata = {
        "project_id": project_id,
        "name": name,
        "version_number": version_number,
        "changelog": changelog,
        "dependencies": [],  # Add dependencies if any
        "game_versions": game_versions,
        "version_type": version_type,
        "loaders": loaders,
        "featured": is_featured,
        "status": status,
        "file_parts": [os.path.basename(file_path)],  # Add the filename to file_parts
        "primary_file": os.path.basename(file_path),  # Mark this file as the primary file
    }

    # Prepare data
    data = {
        'data': (None, json.dumps(metadata), 'application/json'),
        'file': (os.path.basename(file_path), open(file_path, 'rb')),
    }

    if not is_dry:
        try:
            # Make the POST request
            response = requests.post(url, headers=headers, files=data)

            # Close the file after upload
            data['file'][1].close()

            # Check the response
            if response.status_code == 200:
                print(f'Success! File uploaded. Version id: {response.json()["id"]}')
            else:
                print(f'Failed to upload file. Status code: {response.status_code}, message: {response.text}')
        except Exception as e:
            print(f'Error occurred: {str(e)}')

            # Close the file if an error occurred
            data['file'][1].close()
    else:
        print('Dry run, not uploading file.')
        print(f'URL: {url}')
        print(f'Headers: {headers}')
        print(f'Data: {data}')


def main():
    parser = argparse.ArgumentParser(description='Upload a Minecraft mod to CurseForge.')

    parser.add_argument('--curseforge-api-token', help='Your CurseForge API token')
    parser.add_argument('--modrinth-api-token', help='Your Modrinth API token')
    parser.add_argument('--project-id', required=True, help='Your CurseForge project ID')
    parser.add_argument('--file-path', required=True, help='Path to the mod file')
    parser.add_argument('--changelog', required=True, help='Changelog for this version')
    parser.add_argument('--changelog-type', choices=['text', 'html', 'markdown'], default='text', help='Type of the changelog')
    parser.add_argument('--display-name', required=True, help='Display name of the mod')
    parser.add_argument('--game-versions', nargs='+', type=str, required=True, help='List of supported game versions')
    parser.add_argument('--release-type', choices=['alpha', 'beta', 'release'], required=True, help='Release type')
    parser.add_argument('--relations', nargs='+', type=json.loads, required=False, help='List of related projects. Use format: \'{"slug": "mantle", "type": ["embeddedLibrary"]}\'')
    parser.add_argument('--dry', action='store_true', help="Dry run, don't actually upload the file. Useful for testing.")
    parser.add_argument('--loaders', nargs='+', default=['forge'], help='The mod loaders that this version supports')
    parser.add_argument('--featured', action='store_true', help='Whether the version is featured or not')
    parser.add_argument('--status', default='listed', choices=['listed', 'archived', 'draft', 'unlisted', 'scheduled', 'unknown'], help='The status of the version')

    args = parser.parse_args()

    if args.curseforge_api_token:
        mod_name_to_id = {
            "xercamod": 341575,
            "music": 341448,
            "paint": 350727
        }
        cf_project_id = args.project_id
        if cf_project_id in mod_name_to_id:
            cf_project_id = mod_name_to_id[cf_project_id]

        cf_game_versions = [get_game_version_id(version_name, args.curseforge_api_token) for version_name in args.game_versions]
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
        mod_name_to_id = {
            "xercamod": "Z110yRfL",
            "music": "qQpWCN75",
            "paint": "YOs4tZea"
        }
        mod_name_to_title = {
            "xercamod": "XercaMod",
            "music": "Music Maker Mod",
            "paint": "Joy of Painting"
        }

        title = mod_name_to_title[args.project_id]
        if args.project_id in mod_name_to_id:
            args.project_id = mod_name_to_id[args.project_id]

        version = '-'.join(os.path.splitext(os.path.basename(args.file_path))[0].split('-')[1:])
        if version.startswith("fabric-"):
            version = version[7:]
        if version.startswith("forge-"):
            version = version[6:]
        version_name = f'{title} {version}'

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


if __name__ == '__main__':
    main()
