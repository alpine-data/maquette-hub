from shapash.explainer.smart_explainer import SmartExplainer
import os

xpl = SmartExplainer()
xpl_path = os.getenv("MQ_XPL_PATH", os.path.join(os.path.dirname(__file__), "tests", "xpl.pkl"))
host = os.getenv("MQ_XPL_HOST", "0.0.0.0")
port = os.getenv("MQ_XPL_PORT", "8050")
xpl.load(xpl_path)
shapash_app = xpl.run_app(host=host, port=port)

