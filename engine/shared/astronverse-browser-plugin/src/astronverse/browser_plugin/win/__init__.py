from astronverse.browser_plugin import BrowserType, PluginData, PluginManager, PluginManagerCore
from astronverse.browser_plugin.win.browser_360 import Browser360PluginManager
from astronverse.browser_plugin.win.browser_360x import Browser360XPluginManager
from astronverse.browser_plugin.win.chrome import ChromePluginManager
from astronverse.browser_plugin.win.firefox import FirefoxPluginManager
from astronverse.browser_plugin.win.microsoft_edge import EdgePluginManager


class BrowserPluginFactory(PluginManager):
    @staticmethod
    def get_support_browser():
        return [
            BrowserType.CHROME,
            BrowserType.MICROSOFT_EDGE,
            BrowserType.FIREFOX,
            BrowserType.BROWSER_360,
            BrowserType.BROWSER_360X,
        ]

    @staticmethod
    def get_plugin_manager(browser_type: BrowserType, plugin_data: PluginData) -> PluginManagerCore:
        if browser_type == BrowserType.CHROME:
            return ChromePluginManager(plugin_data)
        elif browser_type == BrowserType.MICROSOFT_EDGE:
            return EdgePluginManager(plugin_data)
        elif browser_type == BrowserType.FIREFOX:
            return FirefoxPluginManager(plugin_data)
        elif browser_type == BrowserType.BROWSER_360:
            return Browser360PluginManager(plugin_data)
        elif browser_type == BrowserType.BROWSER_360X:
            return Browser360XPluginManager(plugin_data)
        else:
            raise ValueError(f"Unsupported browser type: {browser_type}")
