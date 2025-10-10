import toml
from astronverse.actionlib.atomic import atomicMg
from astronverse.actionlib.config import config
from astronverse.actionlib.types import typesMg
from astronverse.browser.browser import Browser
from astronverse.browser.browser_element import BrowserElement
from astronverse.browser.browser_script import BrowserScript
from astronverse.browser.browser_software import BrowserSoftware


def get_version():
    with open("pyproject.toml", encoding="utf-8") as f:
        pyproject_data = toml.load(f)
    return pyproject_data["project"]["version"]


if __name__ == "__main__":
    # config.default_value["atomic"] = {
    #     "icon": "icon-list-math-operation",
    # }
    config.set_config_file("config.yaml")
    atomicMg.register(BrowserElement, version=get_version())
    atomicMg.register(BrowserSoftware, version=get_version())
    atomicMg.register(BrowserScript, version=get_version())
    atomicMg.meta()

    config.set_config_file("config_type.yaml")
    typesMg.register_types(Browser, version=get_version(), channel="global", template="Browser对象")
    typesMg.meta()
