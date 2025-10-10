import toml
from astronverse.actionlib.atomic import atomicMg
from astronverse.actionlib.config import config
from astronverse.dialog.dialog import Dialog


def get_version():
    with open("pyproject.toml", encoding="utf-8") as f:
        pyproject_data = toml.load(f)
    return pyproject_data["project"]["version"]


if __name__ == "__main__":
    config.default_value["atomic"] = {
        "icon": "icon-input-form",
    }
    config.set_config_file("config.yaml")
    atomicMg.register(Dialog, version=get_version())
    atomicMg.meta()
