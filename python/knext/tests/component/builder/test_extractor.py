import unittest

from keras.src.engine.base_preprocessing_layer import PreprocessingLayer
from langchain.memory import ConversationKGMemory

from knext.component.builder import UserDefinedExtractor, LLMBasedExtractor


class TestUserDefinedExtractor(unittest.TestCase):
    """UserDefinedExtractor unit test stubs"""

    def setUp(self):
        self.component = UserDefinedExtractor()

    def tearDown(self):
        pass

    def testExecute(self):
        """Test execute"""


class TestLLMBasedExtractor(unittest.TestCase):
    """LLMBasedExtractor unit test stubs"""

    def setUp(self):
        self.component = LLMBasedExtractor()

    def tearDown(self):
        pass

    def testExecute(self):
        """Test execute"""


if __name__ == "__main__":
    unittest.main()
