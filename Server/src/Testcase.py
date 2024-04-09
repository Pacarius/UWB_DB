import unittest
import random
import string

class TestGenerateData(unittest.TestCase):
    def test_generate_data(self):
        data = self.generate_data(10)
        print(data)
        self.assertEqual(len(data.split(',')), 10)
        for item in data.split(','):
            parts = item.strip('[]').split(';')
            self.assertEqual(len(parts), 3)
            self.assertEqual(parts[1], "00000000")
            self.assertTrue(1 <= float(parts[2]) <= 10)

    def generate_data(self, n):
        data = []
        for _ in range(n):
            lamppostid = ''.join(random.choices(string.ascii_uppercase + string.digits, k=5))
            vehicleid = "00000000"
            distance = round(random.uniform(1, 10), 2)
            item = f'[{lamppostid};{vehicleid};{distance}]'
            data.append(item)
        return ','.join(data)

if __name__ == '__main__':
    unittest.main()