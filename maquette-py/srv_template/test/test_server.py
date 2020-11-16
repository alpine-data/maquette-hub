import unittest

from srv_template.server import root

class TestServerPy(unittest.TestCase):

    def test_root(self):
        assert root == { "message": "Hello World!" }