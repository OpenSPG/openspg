from cachetools import TTLCache


class LinkCache:

    def __init__(self, maxsize: int = 500, ttl: int = 60):
        self._cache = TTLCache(maxsize=maxsize, ttl=ttl)

    @property
    def cache(self):
        return self._cache

    def put(self, key, value):
        self.cache[key] = value

    def get(self, key):
        return self.cache.get(key)
